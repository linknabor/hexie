package com.yumu.hexie.service.notify.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.AppUtil;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.customservice.dto.OperatorDTO;
import com.yumu.hexie.integration.customservice.dto.OperatorDTO.Operator;
import com.yumu.hexie.integration.customservice.dto.ServiceCfgDTO;
import com.yumu.hexie.integration.customservice.dto.ServiceCfgDTO.ServiceCfg;
import com.yumu.hexie.integration.notify.ConversionNotification;
import com.yumu.hexie.integration.notify.InvoiceNotification;
import com.yumu.hexie.integration.notify.PartnerNotification;
import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;
import com.yumu.hexie.integration.notify.PayNotification.ServiceNotification;
import com.yumu.hexie.integration.notify.WorkOrderNotification;
import com.yumu.hexie.integration.wechat.service.MsgCfg;
import com.yumu.hexie.integration.wuye.req.CommunityRequest;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.eshop.PartnerService;
import com.yumu.hexie.service.maintenance.MaintenanceService;
import com.yumu.hexie.service.msgtemplate.WechatMsgService;
import com.yumu.hexie.service.notify.NotifyQueueTask;
import com.yumu.hexie.service.sales.BaseOrderService;
import com.yumu.hexie.service.shequ.CommunityService;
import com.yumu.hexie.service.shequ.NoticeService;
import com.yumu.hexie.service.user.CouponService;

@Service
public class NotifyQueueTaskImpl implements NotifyQueueTask {
	
	private static Logger logger = LoggerFactory.getLogger(NotifyQueueTaskImpl.class);
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private MaintenanceService maintenanceService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private GotongService gotongService;
	@Autowired
	private ServiceOperatorRepository serviceOperatorRepository;
	@Autowired
	private ServiceOrderRepository serviceOrderRepository;
	@Autowired
	private PartnerService partnerService;
	@Autowired
	private BaseOrderService baseOrderService;
	@Autowired
	private CouponService couponService;
	@Autowired
	private WechatMsgService wechatMsgService;
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private CommunityService communityService;
	
	/**
	 * 异步发送到账模板消息
	 */
	@Override
	@Async("taskExecutor")
	public void sendWuyeNotificationAysc() {
		
		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String json = redisTemplate.opsForList().leftPop(ModelConstant.KEY_NOTIFY_PAY_QUEUE, 30, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(json)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				AccountNotification queue = objectMapper.readValue(json, new TypeReference<AccountNotification>(){});
				
				logger.info("start to consume wuyeNotificatione queue : " + queue);
				
				boolean isSuccess = false;
				
				/*推广订单 特殊处理 start*/
				String tradeWaterId = queue.getOrderId();
				ServiceOrder order = serviceOrderRepository.findByOrderNo(tradeWaterId);
				if (order != null) {
					int orderType = order.getOrderType();
					if (ModelConstant.ORDER_TYPE_PROMOTION == orderType || ModelConstant.ORDER_TYPE_SAASSALE == orderType) {
						List<Map<String, String>> openidList = new ArrayList<>();
						int operType = 0;
						if (ModelConstant.ORDER_TYPE_PROMOTION == orderType) {
							operType = ModelConstant.SERVICE_OPER_TYPE_PROMOTION;
						}else if (ModelConstant.ORDER_TYPE_SAASSALE == orderType) {
							operType = ModelConstant.SERVICE_OPER_TYPE_SAASSALE;
						}
						List<ServiceOperator> opList = serviceOperatorRepository.findByType(operType);
						for (ServiceOperator serviceOperator : opList) {
							Map<String, String> openids = new HashMap<>();
							openids.put("openid", serviceOperator.getOpenId());
							openidList.add(openids);
						}
						queue.setOpenids(openidList);
						
						if (ModelConstant.ORDER_TYPE_PROMOTION == orderType) {
										
							String address = order.getAddress();	//逗号分隔，需要split
							String[]addrArr = address.split(",");
							
							String remark = "";
							if (addrArr.length!=4) {
								logger.error("当前地址: " + address + "，不能分成 省市区");
							}else {
								String province = addrArr[0];
								String city = addrArr[1];
								String county = addrArr[2];
								String sect = addrArr[3];
								
								if(province.indexOf("上海")>=0
										||province.indexOf("北京")>=0
										||province.indexOf("重庆")>=0
										||province.indexOf("天津")>=0){
									province = "";
								}
								
								remark = province + city + county + sect;
								remark = order.getReceiverName() + "-" + remark;
								logger.info("remark : " + remark);
							}
							queue.setRemark(remark);
						}
						
					}
				}
				/*推广订单 特殊处理 end*/
				
				List<Map<String, String>> openidList = queue.getOpenids();
				if (openidList == null || openidList.isEmpty()) {
					continue;
				}
				List<Map<String, String>> resendList = new ArrayList<>();
				for (Map<String, String> openidMap : openidList) {
					
					User user = null;
					String openid = openidMap.get("openid");
					if (StringUtils.isEmpty(openid)) {
						logger.warn("openid is empty, will skip. ");
						continue;
					}
					List<User> userList = userRepository.findByOpenid(openid);
					if (userList!=null && !userList.isEmpty()) {
						user = userList.get(0);
					}else {
						logger.warn("can not find user, openid : " + openid);
					}
					if (user!=null) {
						try {
							queue.setUser(user);
							gotongService.sendPayNotification(queue);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);	//发送失败的，需要重发
							resendList.add(openidMap);
							
						}
					}
					
				}
				if (resendList.isEmpty()) {
					isSuccess = true;
				}
				
				if (!isSuccess) {
					queue.setOpenids(resendList);
					String value = objectMapper.writeValueAsString(queue);
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_PAY_QUEUE, value);
				}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	
		
	}

	/**
	 * 异步发送自定义服务模板消息
	 */
	@Override
	@Async("taskExecutor")
	public void sendCustomServiceNotificationAysc() {
		
		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String json = redisTemplate.opsForList().leftPop(ModelConstant.KEY_NOTIFY_SERVICE_QUEUE, 30, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(json)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				ServiceNotification queue = objectMapper.readValue(json, new TypeReference<ServiceNotification>(){});
				logger.info("start to consume customServiceNotification queue : " + queue);
				
				boolean isSuccess = false;
				List<Map<String, String>> openidList = queue.getOpenids();
				if (openidList == null || openidList.isEmpty()) {
					logger.info("openIdList is empty, will skip !");
					continue;
				}
				
				if (StringUtils.isEmpty(queue.getOrderId())) {
					logger.info("order id is null, will skip !");
					continue;
				}
				ServiceOrder serviceOrder = serviceOrderRepository.findByOrderNo(String.valueOf(queue.getOrderId()));
				if (serviceOrder == null || serviceOrder.getId() == 0) {
					logger.info("can not find order : " + queue.getOrderId());
					continue;
				}
				
				List<Map<String, String>> resendList = new ArrayList<>();
				for (Map<String, String> openidMap : openidList) {
					
					User user = null;
					String openid = openidMap.get("openid");
					if (StringUtils.isEmpty(openid)) {
						logger.warn("openid is empty, will skip. ");
						continue;
					}
					List<User> userList = userRepository.findByOpenid(openid);
					if (userList!=null && !userList.isEmpty()) {
						user = userList.get(0);
					}else {
						logger.warn("can not find user, openid : " + openid);
					}
					if (user!=null) {
						try {
							gotongService.sendServiceNotification(user, serviceOrder);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);	//发送失败的，需要重发
							resendList.add(openidMap);
						}
					}
					
				}
				if (resendList.isEmpty()) {
					isSuccess = true;
				}
				
				if (!isSuccess) {
					queue.setOpenids(resendList);
					String value = objectMapper.writeValueAsString(queue);
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_SERVICE_QUEUE, value);
				}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		
	}
	
	/**
	 * 异步更新服务人员信息
	 */
	@Override
	@Async("taskExecutor")
	public void updateOpereratorAysc() {
		
		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String json = redisTemplate.opsForList().leftPop(ModelConstant.KEY_UPDATE_OPERATOR_QUEUE, 30, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(json)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				OperatorDTO queue = objectMapper.readValue(json, new TypeReference<OperatorDTO>(){});
				
				logger.info("start to consume opererator queue : " + queue);

				List<Operator> operList = queue.getOperatorList();
				if (operList!=null && !operList.isEmpty()) {
					serviceOperatorRepository.deleteByType(ModelConstant.SERVICE_OPER_TYPE_SERVICE);
				}
				List<Operator> failedList = new ArrayList<>();
				operList.forEach(operator->{
					try {
						if (StringUtils.isEmpty(operator.getTel()) || StringUtils.isEmpty(operator.getOpenid())) {
							logger.warn("operator tel or openid is null, oper : " + operator);
							return;
						}
						List<User> userList = userRepository.findByTelAndOpenid(operator.getTel(), operator.getOpenid());
						User user = null;
						if (userList!=null && !userList.isEmpty()) {
							user = userList.get(0);
						}
						if (user == null) {
							logger.warn("user not exists, openid : " + operator.getOpenid());
						}
						if ("0".equals(operator.getServiceId())) {
							return;
						}
						ServiceOperator serviceOperator = new ServiceOperator();
						serviceOperator.setName(user.getName());
						serviceOperator.setOpenId(operator.getOpenid());
						serviceOperator.setTel(operator.getTel());
						serviceOperator.setType(ModelConstant.SERVICE_OPER_TYPE_SERVICE);
						serviceOperator.setUserId(user.getId());
						serviceOperator.setSubType(operator.getServiceId());
						serviceOperatorRepository.save(serviceOperator);
					} catch (Exception e) {
						logger.error("save serviceOperator failed ! oper : " + operator);
						failedList.add(operator);
					
					}
				});
				
				if (failedList.size() == operList.size() && operList.size() > 0) {
					queue.setOperatorList(failedList);
					String value = objectMapper.writeValueAsString(queue);
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_UPDATE_OPERATOR_QUEUE, value);
				}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	
		
	}
	
	/**
	 * 异步更新服务配置信息
	 */
	@Override
	@Async("taskExecutor")
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_SERVE_ROLE, allEntries = true)
	public void updateServiceCfgAysc() {
		
		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String json = redisTemplate.opsForList().leftPop(ModelConstant.KEY_UPDATE_SERVICE_CFG_QUEUE, 30, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(json)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				ServiceCfgDTO dto = objectMapper.readValue(json, new TypeReference<ServiceCfgDTO>(){});
				logger.info("start to consume service cfg queue : " + dto);
				ServiceCfg cfg = dto.getServiceCfg();
				String serviceId = cfg.getServiceId();
				if ("0".equals(serviceId)) {
					continue;
				}
				
				boolean isSuccess = false;
				try {
					//不要循环操作redisTemplate，有TCP成本
					String operType = cfg.getOperType();
					if ("add".equals(operType) || "edit".equals(operType)) {
						redisTemplate.opsForHash().put(ModelConstant.KEY_CUSTOM_SERVICE, serviceId, cfg.getServiceName());
						
						String sectIds = cfg.getSectId();
						if (!StringUtils.isEmpty(sectIds)) {
							String[]sectArr = sectIds.split(",");
							for (String sectId : sectArr) {
								Map<Object, Object> csMap = redisTemplate.opsForHash().entries(ModelConstant.KEY_CS_SERVED_SECT + sectId);
								csMap.put(sectId, cfg.getServiceId());
								redisTemplate.opsForHash().putAll(ModelConstant.KEY_CS_SERVED_SECT + sectId, csMap);
							}
						}
						
						
					}else if ("delete".equals(operType)) {
						redisTemplate.opsForHash().delete(ModelConstant.KEY_CUSTOM_SERVICE, serviceId);
						
						String sectIds = cfg.getSectId();
						if (!StringUtils.isEmpty(sectIds)) {
							String[]sectArr = sectIds.split(",");
							for (String sectId : sectArr) {
								Map<Object, Object> csMap = redisTemplate.opsForHash().entries(ModelConstant.KEY_CS_SERVED_SECT + sectId);
								csMap.remove(sectId);
								redisTemplate.opsForHash().putAll(ModelConstant.KEY_CS_SERVED_SECT + sectId, csMap);
							}
						}
					}

					if ("delete".equals(operType)) {
						List <ServiceOperator> opList = serviceOperatorRepository.findByType(ModelConstant.SERVICE_OPER_TYPE_SERVICE);
						opList.forEach(oper->{
							String subTypes = oper.getSubType();
							if (StringUtils.isEmpty(subTypes)) {
								return;
							}
							String[]subTypeArr = subTypes.split(",");
							List<String> tepmList = new ArrayList<>();
							List<String> opSubList = Arrays.asList(subTypeArr);	//返回的list不是java.util.list，是一个内部类，不能使用remove等操作。所以外面套一层
							tepmList.addAll(opSubList);
							tepmList.remove(serviceId);
							StringBuffer bf = new StringBuffer();
							for (String subType : tepmList) {
								bf.append(subType).append(",");
							}
							String subs = bf.substring(0, bf.length()-1);
							oper.setSubType(subs);
							serviceOperatorRepository.save(oper);
							
						});
					}
					
					isSuccess = true;
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				
				if (!isSuccess) {
					String value = objectMapper.writeValueAsString(json);
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_UPDATE_SERVICE_CFG_QUEUE, value);
				}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	
		
	}

	@Override
	@Async("taskExecutor")
	public void updateOrderStatusAysc() {
		
		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String tradeWaterId = redisTemplate.opsForList().leftPop(ModelConstant.KEY_UPDATE_ORDER_STATUS_QUEUE, 30, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(tradeWaterId)) {
					continue;
				}
				logger.info("start to consume orderStatus update queue : " + tradeWaterId);
				
				boolean isSuccess = false;
				try {
					baseOrderService.finishOrder(tradeWaterId);
					isSuccess = true;
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				
				
				if (!isSuccess) {
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_UPDATE_ORDER_STATUS_QUEUE, tradeWaterId);
				}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			
		}
	
		
	}

	/**
	 * 给操作员发送发货提醒
	 */
	@Override
	@Async("taskExecutor")
	public void sendDeliveryNotificationAsyc() {

		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String tradeWaterId = redisTemplate.opsForList().leftPop(ModelConstant.KEY_NOTIFY_DELIVERY_QUEUE, 30, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(tradeWaterId)) {
					continue;
				}
				logger.info("start to consume notify delivery queue : " + tradeWaterId);
				
				boolean isSuccess = false;
				try {
					logger.info("notify delivery, tradeWaterId : " + tradeWaterId);
					
					ServiceOrder serviceOrder = serviceOrderRepository.findByOrderNo(tradeWaterId);
					if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
						continue;
					}
					logger.info("notify delivery, orderNo : " + serviceOrder.getOrderNo());
					logger.info("notify delivery, orderType : " + serviceOrder.getOrderType());
					
					if (ModelConstant.ORDER_TYPE_ONSALE == serviceOrder.getOrderType()) {
						
						long groupOrderId = serviceOrder.getGroupOrderId();
						logger.info("notify delivery, groupOrderId : " + groupOrderId);
						
						List<ServiceOrder> orderList = serviceOrderRepository.findByGroupOrderId(groupOrderId);
						for (ServiceOrder o : orderList) {
							int operType = ModelConstant.SERVICE_OPER_TYPE_ONSALE_TAKER;
							long agentId = o.getAgentId();
							logger.info("agentId is : " + agentId);
							List<ServiceOperator> opList = new ArrayList<>();
							if (agentId > 1) {	//1是默认奈博的，所以跳过
								opList = serviceOperatorRepository.findByTypeAndAgentId(operType, agentId);
							}else {
								opList = serviceOperatorRepository.findByType(operType);
							}
							logger.info("oper list size : " + opList.size());
							for (ServiceOperator serviceOperator : opList) {
								logger.info("delivery user id : " + serviceOperator.getUserId());
								User sendUser = userRepository.findById(serviceOperator.getUserId());
								if (sendUser != null) {
									logger.info("send user : " + sendUser.getId());
									gotongService.sendDeliveryNotification(sendUser, o);
								}
							}
						}
						
					} 

					isSuccess = true;
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				
				if (!isSuccess) {
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_DELIVERY_QUEUE, tradeWaterId);
				}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}
	
	/**
	 * 合伙人退款更新有效期
	 */
	@Override
	@Async("taskExecutor")
	public void updatePartnerAsync() {

		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String queue = redisTemplate.opsForList().leftPop(ModelConstant.KEY_NOTIFY_PARTNER_REFUND_QUEUE, 30, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(queue)) {
					continue;
				}
				boolean isSuccess = false;
				try {
					ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
					List<PartnerNotification> list = objectMapper.readValue(queue, new TypeReference<List<PartnerNotification>>(){});
					
					logger.info("start to consume notify update partner refund queue : " + queue);
				
					if (list == null || list.isEmpty()) {
						continue;
					}
					for (PartnerNotification partnerNotification : list) {
						logger.info("partnerNotification : " + partnerNotification);
						partnerService.invalidate(partnerNotification);
					}
					isSuccess = true;
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
						
				if (!isSuccess) {
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_DELIVERY_QUEUE, queue);
				}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}
	
	/**
	 * 商品订单退款，包括：特卖、团购、核销券、合伙人、saas套件的售卖、自定义服务订单
	 */
	@Override
	@Async("taskExecutor")
	public void eshopRefundAsync() {

		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String orderNo = redisTemplate.opsForList().leftPop(ModelConstant.KEY_NOTIFY_ESHOP_REFUND_QUEUE, 30, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(orderNo)) {
					continue;
				}
				boolean isSuccess = false;
				try {
					logger.info("start to consume eshop refund queue, orderNo : " + orderNo);
					if (StringUtils.isEmpty(orderNo)) {
						continue;
					}
					
					ServiceOrder order = serviceOrderRepository.findByOrderNo(orderNo);
					if (order != null) {
						List<ServiceOrder> orderList = new ArrayList<>();
						if (ModelConstant.ORDER_TYPE_ONSALE == order.getOrderType()) {
							orderList = serviceOrderRepository.findByGroupOrderId(order.getGroupOrderId());
						}else {
							orderList.add(order);
						}
						for (ServiceOrder o : orderList) {
							baseOrderService.finishRefund(o);
						}
						
					}
					isSuccess = true;
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
						
				if (!isSuccess) {
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_ESHOP_REFUND_QUEUE, orderNo);
				}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}
	
	/**
	 * 商品订单退款，包括：特卖、团购、核销券、合伙人、saas套件的售卖、自定义服务订单
	 */
	@Override
	@Async("taskExecutor")
	public void consumeWuyeCouponAsync() {

		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String value = redisTemplate.opsForList().leftPop(ModelConstant.KEY_NOTIFY_WUYE_COUPON_QUEUE, 30, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(value)) {
					continue;
				}
				boolean isSuccess = false;
				try {
					ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
					TypeReference<Map<String, String>> typeReference = new TypeReference<Map<String,String>>() {};
					Map<String, String> map = objectMapper.readValue(value, typeReference);
					String couponId = map.get("couponId");
					String orderId = map.get("orderId");
					logger.info("start to consume wuye conpon queue, couponId : " + couponId + ", orderId : " + orderId);
					couponService.consume(orderId, couponId);
					isSuccess = true;
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
						
				if (!isSuccess) {
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_WUYE_COUPON_QUEUE, value);
				}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}
	
	/**
	 * 异步发送到账模板消息(给房屋绑定者推送)
	 */
	@Override
	@Async("taskExecutor")
	public void sendWuyeNotification4HouseBinderAysc() {

		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
}
				String json = redisTemplate.opsForList().leftPop(ModelConstant.KEY_NOTIFY_HOUSE_BINDER_QUEUE, 30, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(json)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				AccountNotification queue = objectMapper.readValue(json, new TypeReference<AccountNotification>(){});
				
				logger.info("start to consume wuyeNotificatione4HouseBinde queue : " + queue);
				
				boolean isSuccess = false;
				
				List<Map<String, String>> wuyeIdList = queue.getWuyeIds();
				if (wuyeIdList == null || wuyeIdList.isEmpty()) {
					continue;
				}
				List<Map<String, String>> resendList = new ArrayList<>();
				for (Map<String, String> wuyeIdMap : wuyeIdList) {
					
					User user = null;
					String wuyeId = wuyeIdMap.get("wuyeid");
					if (StringUtils.isEmpty(wuyeId)) {
						logger.warn("wuyeId is empty, will skip. ");
						continue;
					}
					List<User> userList = userRepository.findByWuyeId(wuyeId);
					if (userList!=null && !userList.isEmpty()) {
						user = userList.get(0);
					}else {
						logger.warn("can not find user, wuyeId : " + wuyeId);
					}
					if (user!=null) {
						try {
							queue.setUser(user);
							gotongService.sendPayNotification4HouseBinder(queue);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);	//发送失败的，需要重发
							resendList.add(wuyeIdMap);
							
						}
					}
					
				}
				if (resendList.isEmpty()) {
					isSuccess = true;
				}
				
				if (!isSuccess) {
					queue.setOpenids(resendList);
					String value = objectMapper.writeValueAsString(queue);
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_HOUSE_BINDER_QUEUE, value);
				}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	
		
	}
	
	/**
	 * 给移动端的物业员工推送工单消息
	 */
	@Override
	@Async("taskExecutor")
	public void sendWorkOrderMsgNotificationAsyc() {

		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String orderStr = redisTemplate.opsForList().leftPop(ModelConstant.KEY_WORKORER_MSG_QUEUE, 10, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(orderStr)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				WorkOrderNotification won = objectMapper.readValue(orderStr, new TypeReference<WorkOrderNotification>(){});
				logger.info("start to consume workorder queue : " + won);
				
				//添加消息到消息中心
				saveNotice(won);

				boolean isSuccess = false;
				try {
					logger.info("send workorder msg async, workorder : " + won);
					isSuccess = gotongService.sendWorkOrderNotification(won);
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				
				if (!isSuccess) {
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_WORKORER_MSG_QUEUE, orderStr);
				}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private void saveNotice(WorkOrderNotification won) {
		String title = "";
		String operName = "";
		if ("05".equals(won.getOperation())) {
			title = "您的"+won.getOrderType()+"工单已被受理";
			operName = won.getAcceptor();
		} else if ("02".equals(won.getOperation())) {
			title = "您的"+won.getOrderType()+"工单已被驳回";
			operName = won.getRejector();
		} else if ("07".equals(won.getOperation())) {
			title = "您的"+won.getOrderType()+"工单已完工";
			operName = won.getFinisher();
		}

		String content = won.getContent();
		if(!StringUtils.isEmpty(content)) {
			if(content.length() > 120) {
				content = content.substring(0, 110);
				content += "...";
			}
		}

		if(!StringUtils.isEmpty(title)) {
			StringBuilder sb = new StringBuilder();
			sb.append(title).append("|")
					.append("工单编号：").append(won.getOrderId()).append("|")
					.append("工单内容：").append(content).append("|")
					.append("工单状态：").append(won.getOrderStatus()).append("|")
					.append("工单处理人：").append(operName);

			List<com.yumu.hexie.integration.notify.Operator> operList = won.getOperatorList();
			if(operList != null && !operList.isEmpty()) {
				com.yumu.hexie.integration.notify.Operator operator = operList.get(0);
				if (!StringUtils.isEmpty(operator.getOpenid())) {
					CommunityRequest request = new CommunityRequest();
					request.setTitle(sb.toString());
					request.setContent(sb.toString());
					request.setSummary(sb.toString());
					request.setAppid(operator.getAppid());
					request.setOpenid(operator.getOpenid());
					request.setNoticeType(ModelConstant.NOTICE_TYPE2_ORDER);

					SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
					request.setPublishDate(df1.format(new Date()));
					request.setOutsideKey(Long.parseLong(won.getOrderId()));
					String msgUrl = wechatMsgService.getMsgUrl(MsgCfg.URL_WORK_ORDER_DETAIL) + won.getOrderId();
					String url = AppUtil.addAppOnUrl(msgUrl, operator.getAppid());
					request.setUrl(url);
					noticeService.addOutSidNotice(request);
				}
			}
		}
	}
	
	
	/**
	 * 给移动端的物业员工推送工单消息
	 */
	@Override
	@Async("taskExecutor")
	public void handleConversionAsyc() {

		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String orderStr = redisTemplate.opsForList().leftPop(ModelConstant.KEY_CONVERSION_MSG_QUEUE, 10, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(orderStr)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				ConversionNotification cn = objectMapper.readValue(orderStr, new TypeReference<ConversionNotification>(){});
				logger.info("start to consume conversion queue : " + cn);
				
				String threadId = cn.getSourceId();
				if (StringUtils.isEmpty(threadId)) {
					logger.warn("conversion source id is empty, will skip . orderId : " + cn.getOrderId());
				}
				
				String reversed = cn.getReversed();
				
				com.yumu.hexie.model.community.Thread thread = communityService.getThreadByTreadId(Long.valueOf(threadId));
				thread.setRectified(Boolean.TRUE);
				if ("1".equals(reversed)) {
					thread.setRectified(Boolean.FALSE);
				}
				communityService.updateThread(thread);
				
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	
	/**
	 * 发票开局模板消息通知
	 */
	@Override
	@Async("taskExecutor")
	public void sendInvoiceMsgAsyc() {

		while(true) {
			try {
				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String queue = redisTemplate.opsForList().leftPop(ModelConstant.KEY_INVOICE_NOTIFICATION_QUEUE, 10, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(queue)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				InvoiceNotification in = objectMapper.readValue(queue, new TypeReference<InvoiceNotification>(){});
				
				//查看用户有没有在移动端申请，如果没有，不推送模板消息
				String orderId = in.getOrderId();
				String applied = redisTemplate.opsForValue().get(ModelConstant.KEY_INVOICE_APPLICATIONF_FLAG + orderId);
				if (!"1".equals(applied)) {	//表示用户没有在移动端申请。扔回队列继续轮，直到用户在移动端申请位置
					
					if (System.currentTimeMillis() - Long.valueOf(in.getTimestamp()) > 3600l*24*10*1000) {	//超过10天没申请，出队
						logger.info("user does not apply 4 invoice .. more than 10 days. will remove from the queue! orderId : " + orderId);
					} else {
						redisTemplate.opsForList().rightPush(ModelConstant.KEY_INVOICE_NOTIFICATION_QUEUE, queue);
//						logger.info("user does not apply 4 invoice .. will loop again. orderId: " + orderId);
					}
					continue;
				}
				logger.info("start to consume invoice msg queue : " + in);
				String openid = in.getOpenid();
				User user = null;
				List<User> userList = userRepository.findByOpenid(openid);
				if (userList!=null && !userList.isEmpty()) {
					user = userList.get(0);
				}else {
					logger.warn("can not find user, openid : " + openid);
				}
				boolean isSuccess = false;
				if (user!=null) {
					try {
						in.setUser(user);
						isSuccess = gotongService.sendMsg4FinishInvoice(in);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);	//发送失败的，需要重发
						
					}
					if (!isSuccess) {
						redisTemplate.opsForList().rightPush(ModelConstant.KEY_INVOICE_NOTIFICATION_QUEUE, queue);
					}
				}
				
				
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}

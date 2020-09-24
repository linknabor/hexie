package com.yumu.hexie.service.notify.impl;

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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.customservice.dto.OperatorDTO;
import com.yumu.hexie.integration.customservice.dto.OperatorDTO.Operator;
import com.yumu.hexie.integration.customservice.dto.ServiceCfgDTO;
import com.yumu.hexie.integration.customservice.dto.ServiceCfgDTO.ServiceCfg;
import com.yumu.hexie.integration.notify.PartnerNotification;
import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;
import com.yumu.hexie.integration.notify.PayNotification.ServiceNotification;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.localservice.HomeServiceConstant;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.OrderItemRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.user.Partner;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.eshop.EvoucherService;
import com.yumu.hexie.service.eshop.PartnerService;
import com.yumu.hexie.service.maintenance.MaintenanceService;
import com.yumu.hexie.service.notify.NotifyQueueTask;
import com.yumu.hexie.service.sales.CartService;
import com.yumu.hexie.service.sales.SalePlanService;
import com.yumu.hexie.service.user.UserNoticeService;

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
	private SalePlanService salePlanService;
	@Autowired
	private UserNoticeService userNoticeService;
	@Autowired
	private EvoucherService evoucherService;
	@Autowired
	private PartnerService partnerService;
	@Autowired
	private CartService cartService;
	@Autowired
	private OrderItemRepository orderItemRepository;
	
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
					serviceOperatorRepository.deleteByType(HomeServiceConstant.SERVICE_TYPE_CUSTOM);
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
						serviceOperator.setType(HomeServiceConstant.SERVICE_TYPE_CUSTOM);
						serviceOperator.setUserId(user.getId());
						serviceOperator.setSubTypes(operator.getServiceId());
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
						List <ServiceOperator> opList = serviceOperatorRepository.findByType(HomeServiceConstant.SERVICE_TYPE_CUSTOM);
						opList.forEach(oper->{
							String subTypes = oper.getSubTypes();
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
							oper.setSubTypes(subs);
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
					logger.info("update orderStatus, tradeWaterId : " + tradeWaterId);
					
					ServiceOrder serviceOrder = serviceOrderRepository.findByOrderNo(tradeWaterId);
					if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
						continue;
					}
					logger.info("update orderStatus, orderId : " + serviceOrder.getId());
					logger.info("update orderStatus, orderType : " + serviceOrder.getOrderType());
					logger.info("update orderStatus, orderStatus : " + serviceOrder.getStatus());
					
					if (ModelConstant.ORDER_TYPE_ONSALE == serviceOrder.getOrderType()) {
						List<ServiceOrder> orderList = serviceOrderRepository.findByGroupOrderId(serviceOrder.getGroupOrderId());
						for (ServiceOrder order : orderList) {
							
							if (ModelConstant.ORDER_STATUS_INIT == order.getStatus()) {
								Date date = new Date();
								order.setStatus(ModelConstant.ORDER_STATUS_PAYED);
								order.setConfirmDate(date);
								order.setPayDate(date);
//								serviceOrderRepository.save(order);
								salePlanService.getService(order.getOrderType()).postPaySuccess(order);	//修改orderItems
								
								//发送模板消息和短信
								userNoticeService.orderSuccess(order.getUserId(), order.getTel(),
										order.getId(), order.getOrderNo(), order.getProductName(), order.getPrice());
								//清空购物车中已购买的商品
								List<OrderItem> itemList = orderItemRepository.findByServiceOrder(order);
								cartService.delFromCart(order.getUserId(), itemList);
								//减库存
								for (OrderItem item : itemList) {
									redisTemplate.opsForValue().decrement(ModelConstant.KEY_PRO_STOCK + item.getProductId(), item.getCount());
								}
								
							}
							
						}
					
					}
					
					//核销券、团购、合伙人、saas售卖
					if (ModelConstant.ORDER_TYPE_EVOUCHER == serviceOrder.getOrderType() || 
							ModelConstant.ORDER_TYPE_RGROUP == serviceOrder.getOrderType() ||
							ModelConstant.ORDER_TYPE_PROMOTION == serviceOrder.getOrderType() ||
							ModelConstant.ORDER_TYPE_SAASSALE == serviceOrder.getOrderType()) {
						
						if (ModelConstant.ORDER_STATUS_INIT == serviceOrder.getStatus()) {
							Date date = new Date();
							serviceOrder.setStatus(ModelConstant.ORDER_STATUS_PAYED);
							serviceOrder.setConfirmDate(date);
							serviceOrder.setPayDate(date);
							serviceOrderRepository.save(serviceOrder);
							salePlanService.getService(serviceOrder.getOrderType()).postPaySuccess(serviceOrder);	//修改orderItems
							
							if (ModelConstant.ORDER_TYPE_RGROUP == serviceOrder.getOrderType()) {
								//减库存
								redisTemplate.opsForValue().decrement(ModelConstant.KEY_PRO_STOCK + serviceOrder.getProductId(), serviceOrder.getCount());
							}
							
							if (ModelConstant.ORDER_TYPE_PROMOTION != serviceOrder.getOrderType() && ModelConstant.ORDER_TYPE_SAASSALE != serviceOrder.getOrderType()) {
								//发送模板消息和短信
								userNoticeService.orderSuccess(serviceOrder.getUserId(), serviceOrder.getTel(),
										serviceOrder.getId(), serviceOrder.getOrderNo(), serviceOrder.getProductName(), serviceOrder.getPrice());
								//清空购物车中已购买的商品
								if (ModelConstant.ORDER_TYPE_ONSALE == serviceOrder.getOrderType()) {
									List<OrderItem> itemList = orderItemRepository.findByServiceOrder(serviceOrder);
									cartService.delFromCart(serviceOrder.getUserId(), itemList);
								}
								
							}
							
							if (ModelConstant.ORDER_TYPE_EVOUCHER == serviceOrder.getOrderType() || ModelConstant.ORDER_TYPE_PROMOTION == serviceOrder.getOrderType()) {
								evoucherService.enable(serviceOrder);	//激活核销券
							}
							
						}
						
						if (ModelConstant.ORDER_TYPE_PROMOTION == serviceOrder.getOrderType()) {
							Partner partner = new Partner();
							partner.setTel(serviceOrder.getTel());
							partner.setName(serviceOrder.getReceiverName());
							partner.setUserId(serviceOrder.getUserId());
							partnerService.save(partner);
						}
						
						
					}
					
					//服务
					if (ModelConstant.ORDER_TYPE_SERVICE == serviceOrder.getOrderType()) {
						
						if (StringUtils.isEmpty(serviceOrder.getPayDate())) {
							if (ModelConstant.ORDER_STATUS_INIT == serviceOrder.getStatus()) {
								//do nothing
							}else if (ModelConstant.ORDER_STATUS_ACCEPTED == serviceOrder.getStatus()) {
								serviceOrder.setStatus(ModelConstant.ORDER_STATUS_PAYED);
							}
							serviceOrder.setPayDate(new Date());
							serviceOrderRepository.save(serviceOrder);
						}
					}
					
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
						
					} else if (ModelConstant.ORDER_TYPE_RGROUP == serviceOrder.getOrderType()) {
						
						int operType = ModelConstant.SERVICE_OPER_TYPE_RGROUP_TAKER;
						long agentId = serviceOrder.getAgentId();
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
								gotongService.sendDeliveryNotification(sendUser, serviceOrder);
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
	

}

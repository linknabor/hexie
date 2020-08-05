package com.yumu.hexie.service.notify.impl;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;
import com.yumu.hexie.integration.notify.PayNotification.ServiceNotification;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.localservice.HomeServiceConstant;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.maintenance.MaintenanceService;
import com.yumu.hexie.service.notify.NotifyQueueTask;

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
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				
				isSuccess = true;
				if (!isSuccess) {
					String value = objectMapper.writeValueAsString(json);
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_SERVICE_QUEUE, value);
				}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	
		
	}
	

}

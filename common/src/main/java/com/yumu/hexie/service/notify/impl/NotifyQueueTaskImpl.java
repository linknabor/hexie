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
import com.yumu.hexie.model.maintenance.MaintenanceService;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.notify.NotifyQueueTask;

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
	
	/**
	 * 异步发送到账模板消息
	 */
	@Override
	@Async
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
				
				logger.info("strat to consume to queue : " + queue);
				
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
	@Async
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
				
				logger.info("strat to consume to queue : " + queue);
				
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
							gotongService.sendServiceNotification(queue);
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
	@Async
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
				
				logger.info("strat to consume to opererator queue : " + queue);
				
				List<Operator> operList = queue.getOperatorList();
				if (operList!=null && !operList.isEmpty()) {
					ServiceOperator serviceOperator = new ServiceOperator();
					serviceOperator.setType(HomeServiceConstant.SERVICE_TYPE_CUSTOM);
					serviceOperatorRepository.delete(serviceOperator);
				}
				
				operList.stream().forEach(operator->{
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
						ServiceOperator serviceOperator = new ServiceOperator();
						serviceOperator.setName(user.getName());
						serviceOperator.setOpenId(operator.getOpenid());
						serviceOperator.setTel(operator.getTel());
						serviceOperator.setType(HomeServiceConstant.SERVICE_TYPE_CUSTOM);
						serviceOperator.setUserId(user.getId());
						serviceOperator.setSubTypes(operator.getServiceId());
						serviceOperatorRepository.save(serviceOperator);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				});
				
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	
		
	}
	
	/**
	 * 异步更新服务人员信息
	 */
	@Override
	@Async
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
				logger.info("strat to consume to service cfg queue : " + dto);
				List<ServiceCfg> cfgList = dto.getCfgList();
				//不要循环操作redisTemplate，有TCP成本
				Map<Object, Object> cfgMap = redisTemplate.opsForHash().entries(ModelConstant.KEY_CUSTOM_SERVICE);
				List<String> delList = new ArrayList<>();
				cfgList.stream().forEach(cfg->{
					String operType = cfg.getOperType();
					if ("add".equals(operType) || "edit".equals(operType)) {
						cfgMap.put(cfg.getServiceId(), cfg.getServiceName());
					}else if ("delete".equals(operType)) {
						cfgMap.remove(cfg.getServiceId());
						delList.add(cfg.getServiceId());
					}
				});
				redisTemplate.opsForHash().putAll(ModelConstant.KEY_CUSTOM_SERVICE, cfgMap);
				
				if (!delList.isEmpty()) {
					List <ServiceOperator> opList = serviceOperatorRepository.findByType(HomeServiceConstant.SERVICE_TYPE_CUSTOM);
					opList.forEach(oper->{
						String subTypes = oper.getSubTypes();
						if (StringUtils.isEmpty(subTypes)) {
							return;
						}
						String[]subTypeArr = subTypes.split(",");
						List<String> opSubList = Arrays.asList(subTypeArr);
						opSubList.removeAll(delList);
						StringBuffer bf = new StringBuffer();
						for (String subType : opSubList) {
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
		}
	
		
	}


}

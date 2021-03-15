package com.yumu.hexie.service.user.impl;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.maintenance.MaintenanceService;
import com.yumu.hexie.service.user.UserQueueTask;
import com.yumu.hexie.service.user.UserService;

public class UserQueueTaskImpl implements UserQueueTask {
	
	private Logger logger = LoggerFactory.getLogger(UserQueueTaskImpl.class);
	
	@Autowired
	private MaintenanceService maintenanceService;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private UserService userService;
	
	/**
	 * 微信用户关注事件。更新用户状态
	 */
	@SuppressWarnings("unchecked")
	@Async("taskExecutor")
	@Override
	public void eventSubscribe() {

		while (true) {
			try {

				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String json = (String) stringRedisTemplate.opsForList().leftPop(ModelConstant.KEY_EVENT_SUBSCRIBE_UPDATE_QUEUE,
						10, TimeUnit.SECONDS);

				if (StringUtils.isEmpty(json)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				Map<String, String> map = objectMapper.readValue(json, Map.class);
				logger.info("start to consume user subscribe event queue : " + map);

				String appId = map.get("appId");
				String openid = map.get("openid");
				String createTimeStr = map.get("createTime");
				Long createDate = Long.valueOf(createTimeStr);
				if (createTimeStr.length() == 10) {
					createDate *= 1000;
				}
				
				if ((System.currentTimeMillis() - createDate) > 30*60) {	
					/*
					 * 半小时仍旧没有处理掉的关注事件，直接出队。通常情况用户先关注，访问页面后再产生user,所以没有user的情况下，事件是消耗不掉的
					 */
					logger.info("user subscribe timeout, will skip ! openid: " + openid);
					continue;
				}
				
				User user = new User();
				user.setOpenid(openid);
				user.setAppId(appId);
				user.setSubscribe(ModelConstant.WECHAT_USER_SUBSCRIBED);
				user.setSubscribe_time(new Date(createDate));

				boolean isSuccess = false;
				try {
					isSuccess = userService.eventSubscribe(user);
					logger.info("user subscribe succeeded !");
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
					isSuccess = false;
				}
				if (!isSuccess) {
					logger.info("user subscribe event consume failed !, repush into the queue. json : " + json);
					stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_EVENT_SUBSCRIBE_UPDATE_QUEUE, json);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

	@SuppressWarnings("unchecked")
	@Async("taskExecutor")
	@Override
	public void eventUnsubscribe() {
		
		while (true) {
			try {

				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String json = (String) stringRedisTemplate.opsForList().leftPop(ModelConstant.KEY_EVENT_UNSUBSCRIBE_QUEUE,
						10, TimeUnit.SECONDS);

				if (StringUtils.isEmpty(json)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				Map<String, String> map = objectMapper.readValue(json, Map.class);
				logger.info("start to consume user unsubscribe event queue : " + map);

				String appId = map.get("appId");
				String openid = map.get("openid");
				String createTimeStr = map.get("createTime");
				Long createDate = Long.valueOf(createTimeStr);
				if (createTimeStr.length() == 10) {
					createDate *= 1000;
				}
				User user = new User();
				user.setOpenid(openid);
				user.setAppId(appId);
				user.setSubscribe(ModelConstant.WECHAT_USER_UNSUBSCRIBED);
				user.setUnsubscribeDate(new Date(createDate));

				boolean isSuccess = false;
				try {
					isSuccess = userService.eventUnsubscribe(user);
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
					isSuccess = false;
				}

				if (!isSuccess) {
					logger.info("user unsubscribe event consume failed !, repush into the queue. json : " + json);
					stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_EVENT_UNSUBSCRIBE_QUEUE, json);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

	}
	
}

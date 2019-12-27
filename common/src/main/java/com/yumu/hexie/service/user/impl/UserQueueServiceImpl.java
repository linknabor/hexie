package com.yumu.hexie.service.user.impl;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.wechat.vo.SubscribeVO;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.shequ.impl.WuyeQueueTaskImpl;
import com.yumu.hexie.service.user.UserQueueService;
import com.yumu.hexie.service.user.UserService;

@Service
public class UserQueueServiceImpl implements UserQueueService {

	private static Logger logger = LoggerFactory.getLogger(WuyeQueueTaskImpl.class);

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private SystemConfigService systemConfigService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * 关注事件
	 * 1.发送关注客服消息，推送会员卡
	 * 2.记录推送出去的会员卡到表里
	 */
	@SuppressWarnings("unchecked")
	@Async
	@Override
	public void subscribeEvent() {

		while (true) {
			try {

				String json = (String)stringRedisTemplate.opsForList().leftPop(ModelConstant.KEY_SUBSCRIBE_MSG_QUEUE, 10,
						TimeUnit.SECONDS);
				
				String event = systemConfigService.getSysConfigByKey("SUBSCRIBE_EVENT");
				if ("0".equals(event)) {	//0没活动
					continue;	
				}
				
				if (StringUtils.isEmpty(json)) {
					continue;
				}
				
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				Map<String, String> map = objectMapper.readValue(json, Map.class);

				logger.info("strat to consume subscribe user queue : " + map);

				String appId = map.get("appId");
				String openid = map.get("openid");
				
				User user = new User();
				user.setOpenid(openid);
				user.setAppId(appId);

				SubscribeVO subscribeVO = new SubscribeVO();
				subscribeVO.setUser(user);
				userService.subscribeEvent(subscribeVO);
				

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

}

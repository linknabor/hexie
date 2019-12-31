package com.yumu.hexie.service.card.impl;

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
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.card.dto.EventGetCardDTO;
import com.yumu.hexie.model.card.dto.EventSubscribeDTO;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.card.WechatCardQueueTask;
import com.yumu.hexie.service.card.WechatCardService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.shequ.impl.WuyeQueueTaskImpl;

@Service
public class WechatCardQueueTaskImpl implements WechatCardQueueTask {

	private static Logger logger = LoggerFactory.getLogger(WuyeQueueTaskImpl.class);

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private SystemConfigService systemConfigService;

	@Autowired
	private WechatCardService wechatCardService;

	/**
	 * 关注事件 1.发送关注客服消息，推送会员卡 2.记录推送出去的会员卡到表里
	 */
	@SuppressWarnings("unchecked")
	@Async
	@Override
	public void eventSubscribe() {

		while (true) {
			try {

				String json = (String) stringRedisTemplate.opsForList().leftPop(ModelConstant.KEY_EVENT_SUBSCRIBE_QUEUE,
						10, TimeUnit.SECONDS);

				String event = systemConfigService.getSysConfigByKey("SUBSCRIBE_EVENT");
				if ("0".equals(event)) { // 0没活动
					continue;
				}

				if (StringUtils.isEmpty(json)) {
					continue;
				}

				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				Map<String, String> map = objectMapper.readValue(json, Map.class);

				logger.info("strat to consume subscribe event queue : " + map);

				String appId = map.get("appId");
				String openid = map.get("openid");

				User user = new User();
				user.setOpenid(openid);
				user.setAppId(appId);

				EventSubscribeDTO eventSubscribeDTO = new EventSubscribeDTO();
				eventSubscribeDTO.setUser(user);
				boolean isSuccess = false;
				try {
					wechatCardService.eventSubscribe(eventSubscribeDTO);
					isSuccess = true;
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
				}

				if (!isSuccess) {
					logger.info("subscribe event consume failed !, repush into the queue. json : " + json);
					stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_EVENT_SUBSCRIBE_QUEUE, json);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

	/**
	 * 领卡事件 根据推送的cardCode查看卡是否已经创建，没创建的建新卡
	 */
	@Async
	@Override
	public void eventUserGetCard() {

		while (true) {
			try {

				String json = (String) stringRedisTemplate.opsForList().leftPop(ModelConstant.KEY_EVENT_GETCARD_QUEUE,
						10, TimeUnit.SECONDS);

				if (StringUtils.isEmpty(json)) {
					continue;
				}

				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				EventGetCardDTO eventGetCardDTO = objectMapper.readValue(json, EventGetCardDTO.class);
				logger.info("strat to consume getCad event queue : " + eventGetCardDTO);

				boolean isSuccess = false;
				try {
					wechatCardService.eventGetCard(eventGetCardDTO);
					isSuccess = true;
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
				}

				if (!isSuccess) {
					logger.info("subscribe event failed !, repush into the queue. json : " + json);
					stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_EVENT_GETCARD_QUEUE, json);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}

package com.yumu.hexie.service.subscribemsg.impl;

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
import com.yumu.hexie.model.subscribemsg.dto.EventSubscribeMsg;
import com.yumu.hexie.service.maintenance.MaintenanceService;
import com.yumu.hexie.service.subscribemsg.WechatSubscribeMsgQueueTask;
import com.yumu.hexie.service.subscribemsg.WechatSubscribeMsgService;

@Service
public class WechatSubscribeMsgQueueTaskImpl implements WechatSubscribeMsgQueueTask {

	private static Logger logger = LoggerFactory.getLogger(WechatSubscribeMsgQueueTaskImpl.class);
	
	@Autowired
	private MaintenanceService maintenanceService;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private WechatSubscribeMsgService wechatSubscribeMsgService;
	
	@Async("taskExecutor")
	@Override
	public void eventSubscribeMsg() {

		while (true) {
			try {

				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String json = (String) stringRedisTemplate.opsForList().leftPop(ModelConstant.KEY_EVENT_SUBSCRIBE_MSG_QUEUE,
						10, TimeUnit.SECONDS);

				if (StringUtils.isEmpty(json)) {
					continue;
				}
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				EventSubscribeMsg eventSubscribeMsg = objectMapper.readValue(json, EventSubscribeMsg.class);
				logger.info("strat to consume subscribeMsg event queue : " + eventSubscribeMsg);

				boolean isSuccess = false;
				try {
					wechatSubscribeMsgService.eventSubscribeMsg(eventSubscribeMsg);
					isSuccess = true;
				} catch (Exception e) {
					logger.error(e.getMessage(), e); // 里面有事务，报错自己会回滚，外面catch住处理
				}

				if (!isSuccess) {
					logger.info("subscribeMsg event consume failed !, repush into the queue. json : " + json);
					stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_EVENT_SUBSCRIBE_MSG_QUEUE, json);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

	

	}

}

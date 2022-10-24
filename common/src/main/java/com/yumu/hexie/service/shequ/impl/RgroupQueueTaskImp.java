package com.yumu.hexie.service.shequ.impl;

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
import com.yumu.hexie.service.common.RgroupV3Service;
import com.yumu.hexie.service.maintenance.MaintenanceService;
import com.yumu.hexie.service.shequ.RgroupQueueTask;

public class RgroupQueueTaskImp implements RgroupQueueTask {
	
	private static Logger logger = LoggerFactory.getLogger(RgroupQueueTaskImp.class);
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RgroupV3Service rgroupV3Service;

	@Autowired
	private MaintenanceService maintenanceService;
	
	@SuppressWarnings("unchecked")
	@Async("taskExecutor")
	@Override
	public void groupPubPush() {
		
		while (true) {
			try {

				if (!maintenanceService.isQueueSwitchOn()) {
					logger.info("queue switch off ! ");
					Thread.sleep(60000);
					continue;
				}
				String json = stringRedisTemplate.opsForList().leftPop(ModelConstant.KEY_RGROUP_PUB_QUEUE, 30, TimeUnit.SECONDS);
				if (StringUtils.isEmpty(json)) {
					continue;
				}
				Thread.sleep(10000);
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				Map<String, String> map = objectMapper.readValue(json, Map.class);
				logger.info("strat to consume groupPubPush queue : " + map);
				String ruleId = map.get("ruleId");
				boolean isSuccess = false;
				try {
					rgroupV3Service.sendPubMsg(ruleId);
					isSuccess = true;
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				if (!isSuccess) {
					logger.info("groupPubPush queue consume failed !, repush into the queue. json : " + json);
					stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_RGROUP_PUB_QUEUE, json);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}

}

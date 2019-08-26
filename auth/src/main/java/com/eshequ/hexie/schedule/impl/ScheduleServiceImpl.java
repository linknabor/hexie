package com.eshequ.hexie.schedule.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.eshequ.hexie.common.Constants;
import com.eshequ.hexie.schedule.ScheduleService;
import com.eshequ.hexie.service.AuthService;
import com.eshequ.hexie.vo.ComponentAcessToken;
import com.eshequ.hexie.vo.VerifyTicket;

@Service
public class ScheduleServiceImpl implements ScheduleService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private AuthService authService;
	
	
	/**
	 * 获取componentAccessToken，每隔10分钟一次，如果token未超时，则不更新。
	 * token每2小时超时，大于1小时50分的时候更新
	 */
	@Scheduled(cron = "0 0/10 * * * ?")
	@Override
	public void setComponentAccessToken() {

		ComponentAcessToken cat = (ComponentAcessToken) redisTemplate.opsForValue().get(Constants.COMPONENT_ACCESS_TOKEN);
		Long createTime = cat.getCreateTime();
		Long currentTime = System.currentTimeMillis();
		
		if (currentTime - createTime > ((60+50)*60*1000)) {
			VerifyTicket verifyTicket = (VerifyTicket) redisTemplate.opsForValue().get(Constants.VERIFY_TICKET);
			cat = authService.getComponentAccessToken(verifyTicket.getVerifyTicket());
			cat.setCreateTime(System.currentTimeMillis());
			redisTemplate.opsForValue().set(Constants.COMPONENT_ACCESS_TOKEN, cat);
			
		}
	}

}

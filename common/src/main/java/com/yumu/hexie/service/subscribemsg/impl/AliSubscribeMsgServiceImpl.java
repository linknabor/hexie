package com.yumu.hexie.service.subscribemsg.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yumu.hexie.model.subscribemsg.UserSubscribeMsg;
import com.yumu.hexie.model.subscribemsg.UserSubscribeMsgRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.subscribemsg.AliSubscribeMsgService;
import com.yumu.hexie.service.subscribemsg.dto.SubscribeReq;

@Service
public class AliSubscribeMsgServiceImpl implements AliSubscribeMsgService {

	@Resource
	private UserSubscribeMsgRepository userSubscribeMsgRepository;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	@Transactional
	@Override
	public void addSubscribe(SubscribeReq subscribeReq) {
		
		String lockKey = "lock:alimsg:subscribe:";
		User aliUser = subscribeReq.getUser();
		lockKey += (aliUser.getAliappid() + ":" + aliUser.getAliuserid());
		Boolean exists = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", 1800l, TimeUnit.SECONDS);
		if (!exists) {
			return;
		}
		List<UserSubscribeMsg> subscribeList =userSubscribeMsgRepository.findByAliuseridAndAppid(aliUser.getAliuserid(), aliUser.getAliappid());
		if(subscribeList!= null && !subscribeList.isEmpty()) {
			return;
		}
		UserSubscribeMsg userSubscribeMsg = new UserSubscribeMsg();
		userSubscribeMsg.setAliuserid(aliUser.getAliuserid());
		userSubscribeMsg.setAppid(aliUser.getAliappid());
		userSubscribeMsg.setBizType(0);
		userSubscribeMsg.setStatus(1);
		userSubscribeMsg.setType(20);
		userSubscribeMsg.setUserId(aliUser.getId());
		userSubscribeMsgRepository.save(userSubscribeMsg);
	}
}

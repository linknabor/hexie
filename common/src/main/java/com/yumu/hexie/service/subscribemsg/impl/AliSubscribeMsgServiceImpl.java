package com.yumu.hexie.service.subscribemsg.impl;

import javax.annotation.Resource;

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
	
	@Transactional
	@Override
	public void addSubscribe(SubscribeReq subscribeReq) {
		
		User aliUser = subscribeReq.getUser();
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

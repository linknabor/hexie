package com.yumu.hexie.service.expressdelivery.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yumu.hexie.integration.wechat.service.TemplateMsgService;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.expressdelivery.ExpressDeliveryService;
@Service
public class ExpressDeliveryServiceImpl implements ExpressDeliveryService{

	@Autowired
	private SystemConfigService systemConfigService;
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	public void pullWechat(String wuyeId,String type) {
		List<User> user = userRepository.findByWuyeId(wuyeId);
		String accessToken = systemConfigService.queryWXAToken(user.get(0).getAppId());
		TemplateMsgService.sendExpressDelivery(user.get(0).getOpenid(), accessToken, user.get(0).getAppId(),type);
	}
	
}

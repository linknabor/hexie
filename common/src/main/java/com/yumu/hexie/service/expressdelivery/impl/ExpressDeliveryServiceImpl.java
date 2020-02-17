package com.yumu.hexie.service.expressdelivery.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yumu.hexie.integration.wechat.service.TemplateMsgService;
import com.yumu.hexie.model.express.Express;
import com.yumu.hexie.model.express.ExpressRepository;
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
	
	@Autowired
	ExpressRepository expressRepository;
	
	@Override
	public void pullWechat(Express exr) {
		List<User> user = userRepository.findByWuyeId(exr.getWuyeId());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		exr.setUserId(user.get(0).getId());
		exr.setDate_time(df.format(new Date()));
		expressRepository.save(exr);
		String accessToken = systemConfigService.queryWXAToken(user.get(0).getAppId());
		TemplateMsgService.sendExpressDelivery(user.get(0).getOpenid(), accessToken, user.get(0).getAppId(),exr.getType());
	}

	@Override
	public Express getExpress(long userId) {
		// TODO Auto-generated method stub
		return expressRepository.findByUserId(userId);
	}
	
}

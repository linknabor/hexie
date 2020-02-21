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
		
		String[] wuyeid = exr.getWuyeId().split(",");
		for (int i = 0; i < wuyeid.length; i++) {
			List<User> user = userRepository.findByWuyeId(wuyeid[i]);

			if(user != null && !user.isEmpty()){
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				exr.setUserId(user.get(0).getId());
				exr.setDate_time(df.format(new Date()));
				expressRepository.save(exr);
				String accessToken = systemConfigService.queryWXAToken(user.get(0).getAppId());
				TemplateMsgService.sendExpressDelivery(user.get(0).getOpenid(), accessToken, user.get(0).getAppId(),user.get(0).getId(),exr.getType());
			}
			

		}
		
	}

	@Override
	public List<Express> getExpress(long userId) {
		// TODO Auto-generated method stub
		return expressRepository.findByUserId(userId);
	}
	
}

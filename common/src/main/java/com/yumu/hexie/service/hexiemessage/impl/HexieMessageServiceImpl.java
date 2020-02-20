package com.yumu.hexie.service.hexiemessage.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yumu.hexie.integration.wechat.service.TemplateMsgService;
import com.yumu.hexie.model.express.Express;
import com.yumu.hexie.model.express.ExpressRepository;
import com.yumu.hexie.model.hexiemessage.HexieMessage;
import com.yumu.hexie.model.hexiemessage.HexieMessageRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.SmsService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.hexiemessage.HexieMessageService;
@Service
public class HexieMessageServiceImpl implements HexieMessageService{

	@Autowired
	private SystemConfigService systemConfigService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	HexieMessageRepository hexieMessageRepository;
	
	@Inject
	protected SmsService smsService;
	
	@Override
	public void pullWechat(HexieMessage exr) {
		
		String[] wuyeid = exr.getWuyeId().split(",");
		for (int i = 0; i < wuyeid.length; i++) {
			List<User> user = userRepository.findByWuyeId(wuyeid[i]);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			exr.setUserId(user.get(0).getId());
			exr.setDate_time(df.format(new Date()));
			hexieMessageRepository.save(exr);
			String accessToken = systemConfigService.queryWXAToken(user.get(0).getAppId());
			TemplateMsgService.sendHexieMessage(user.get(0).getOpenid(), accessToken, user.get(0).getAppId(),user.get(0).getId(),exr.getType());
			
			smsService.sendMsg(user.get(0), user.get(0).getTel(), exr.getContent(), 0);//发送短信
		}
		
		
	}
	
	@Override
	public List<HexieMessage> getMessage(long userId) {
		// TODO Auto-generated method stub
		return hexieMessageRepository.findByUserId(userId);
	}


	
}

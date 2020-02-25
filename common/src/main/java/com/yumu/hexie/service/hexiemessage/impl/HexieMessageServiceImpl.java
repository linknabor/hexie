package com.yumu.hexie.service.hexiemessage.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yumu.hexie.common.util.TransactionUtil;
import com.yumu.hexie.integration.wechat.service.TemplateMsgService;
import com.yumu.hexie.model.hexiemessage.HexieMessage;
import com.yumu.hexie.model.hexiemessage.HexieMessageRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.SmsService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.hexiemessage.HexieMessageService;
import com.yumu.hexie.vo.SmsMessage;
@Service
public class HexieMessageServiceImpl<T> implements HexieMessageService{
	
	@Autowired
	private SystemConfigService systemConfigService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	HexieMessageRepository hexieMessageRepository;
	
	@Inject
	protected SmsService smsService;
	
	@Autowired
	private TransactionUtil<T> transactionUtil;
	
	@Override
	public void sendMessage(HexieMessage exr) {

		String[] wuyeid = exr.getWuyeId().split(",");
		
		for (int i = 0; i < wuyeid.length; i++) {
			
			List<User> userList = userRepository.findByWuyeId(wuyeid[i]);
			if (userList == null || userList.isEmpty()) {
				continue;
			}
			User user = userList.get(0);
			if("0".equals(exr.getType())) {
				
				transactionUtil.transact(s -> saveHexieMessage(exr, user));
			
			}else if ("1".equals(exr.getType())) {
				
				SmsMessage smsMessage = new SmsMessage();
				smsMessage.setMessage(exr.getContent());
				smsMessage.setMobile(user.getTel());
				smsMessage.setTitle(exr.getSect_name());
				smsService.sendMsg(user, smsMessage, 0);//发送短信
				
			}
			
		}

	}
	
	private void saveHexieMessage(HexieMessage exr, User user) {
		
		String accessToken = systemConfigService.queryWXAToken(user.getAppId());
		TemplateMsgService.sendHexieMessage(user.getOpenid(), accessToken, user.getAppId(),user.getId(),exr.getContent());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		HexieMessage hexieMessage = new HexieMessage();
		BeanUtils.copyProperties(exr, hexieMessage);
		hexieMessage.setUserId(user.getId());
		hexieMessage.setDate_time(df.format(new Date()));
		hexieMessage.setWuyeId(user.getWuyeId());
		hexieMessageRepository.save(hexieMessage);
	}
	
	@Override
	public List<HexieMessage> getMessage(long userId) {
		return hexieMessageRepository.findByUserId(userId);
	}


	
}

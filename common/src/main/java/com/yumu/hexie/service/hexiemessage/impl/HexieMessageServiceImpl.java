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
public class HexieMessageServiceImpl<T> implements HexieMessageService{


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
		if("0".equals(exr.getType())) {	//公众号只发模板消息，短信的在servplat发
			for (int i = 0; i < wuyeid.length; i++) {
				List<User> userList = userRepository.findByWuyeId(wuyeid[i]);
				if (userList == null || userList.isEmpty()) {
					continue;
				}
				User user = userList.get(0);
				transactionUtil.transact(s -> saveHexieMessage(exr, user));

			}

		}

	}
	
	@Override
	public List<HexieMessage> getMessage(long userId) {
		// TODO Auto-generated method stub
		return hexieMessageRepository.findByUserId(userId);

	}


	
}

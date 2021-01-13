package com.yumu.hexie.service.hexiemessage.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yumu.hexie.model.hexiemessage.HexieMessage;
import com.yumu.hexie.model.hexiemessage.HexieMessageRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.SmsService;
import com.yumu.hexie.service.hexiemessage.HexieMessageService;
@Service
public class HexieMessageServiceImpl<T> implements HexieMessageService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private HexieMessageRepository hexieMessageRepository;
	
	@Autowired
	protected SmsService smsService;
	
	@Autowired
	private GotongService gotongService;
	
	/**
	 * 公众号群发消息通知功能
	 */
	@Override
	@Transactional
	public boolean sendMessage(HexieMessage exr) {
		
		boolean success = false;
		boolean successFlag = false;
		String[] wuyeid = exr.getWuyeId().split(",");
		if("0".equals(exr.getType())) {	//公众号只发模板消息，短信的在servplat发
			for (int i = 0; i < wuyeid.length; i++) {
				List<User> userList = userRepository.findByWuyeId(wuyeid[i]);
				User user = null;
				if (userList == null || userList.isEmpty()) {
					user = new User();
				}else {
					user = userList.get(0);
				}
				success = saveHexieMessage(exr, user);
				if (success) {
					successFlag = true;	//当前这户，有一个绑定者成功就算成功
				}
			}
			
		}
		return successFlag;

	}
	
	/**
	 * 公众号群发消息通知功能
	 * @param exr
	 * @param user
	 */
	@Override
	public boolean saveHexieMessage(HexieMessage exr, User user) {
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		HexieMessage hexieMessage = new HexieMessage();
		BeanUtils.copyProperties(exr, hexieMessage);
		hexieMessage.setUserId(user.getId());
		hexieMessage.setDate_time(df.format(new Date()));
		hexieMessage.setWuyeId(user.getWuyeId());
		boolean success = true;
		if (!com.yumu.hexie.common.util.StringUtil.isEmpty(user.getWuyeId())) {
			success = gotongService.sendGroupMessage(user.getOpenid(), user.getAppId(), hexieMessage.getId(), hexieMessage.getContent());
		}else {
			success = false;
		}
		hexieMessage.setSuccess(success);
		hexieMessage = hexieMessageRepository.save(hexieMessage);
		return success;
	}
	
	@Override
	public HexieMessage getMessage(long messageId) {
		
		return hexieMessageRepository.findById(messageId).get();
	}

}

package com.yumu.hexie.service.hexiemessage.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.yumu.hexie.common.util.TransactionUtil;
import com.yumu.hexie.model.hexiemessage.HexieMessage;
import com.yumu.hexie.model.hexiemessage.HexieMessageRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.SmsService;
import com.yumu.hexie.service.hexiemessage.HexieMessageService;
@Service
public class HexieMessageServiceImpl<T> implements HexieMessageService{
	
	private static Logger logger = LoggerFactory.getLogger(HexieMessageServiceImpl.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private HexieMessageRepository hexieMessageRepository;
	
	@Autowired
	protected SmsService smsService;
	
	@Autowired
	private TransactionUtil<T> transactionUtil;
	
	@Autowired
	private GotongService gotongService;
	
	/**
	 * 公众号群发消息通知功能
	 */
	@Override
	public void sendMessage(HexieMessage exr) {

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
	
	/**
	 * 公众号群发消息通知功能
	 * @param exr
	 * @param user
	 */
	private void saveHexieMessage(HexieMessage exr, User user) {
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		HexieMessage hexieMessage = new HexieMessage();
		BeanUtils.copyProperties(exr, hexieMessage);
		hexieMessage.setUserId(user.getId());
		hexieMessage.setDate_time(df.format(new Date()));
		hexieMessage.setWuyeId(user.getWuyeId());
		hexieMessage = hexieMessageRepository.save(hexieMessage);
		
		gotongService.sendGroupMessage(user.getOpenid(), user.getAppId(), hexieMessage.getId(), hexieMessage.getContent());
	}
	
	@Override
	public HexieMessage getMessage(long messageId) {
		
		return hexieMessageRepository.findOne(messageId);
	}

}

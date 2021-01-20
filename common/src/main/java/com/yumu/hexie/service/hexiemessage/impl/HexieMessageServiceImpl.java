package com.yumu.hexie.service.hexiemessage.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.hexiemessage.HexieMessage;
import com.yumu.hexie.model.hexiemessage.HexieMessageRepository;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.localservice.repair.ServiceOperatorSect;
import com.yumu.hexie.model.localservice.repair.ServiceOperatorSectRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.SmsService;
import com.yumu.hexie.service.exception.BizValidateException;
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
	private GotongService gotongService;
	@Autowired
	private ServiceOperatorRepository serviceOperatorRepository;
	@Autowired
	private ServiceOperatorSectRepository serviceOperatorSectRepository;
	
	
	/**
	 * 公众号群发消息通知功能
	 */
	@Override
	@Transactional
	public boolean sendMessage(HexieMessage exr) {
		
		boolean success = false;
		boolean successFlag = false;
		String[] wuyeid = exr.getWuyeId().split(",");
		for (int i = 0; i < wuyeid.length; i++) {
			List<User> userList = userRepository.findByWuyeId(wuyeid[i]);
			User user = null;
			if (userList == null || userList.isEmpty()) {
				user = new User();
			}else {
				user = userList.get(0);
			}
			logger.info("will sent wuye message to user : " + user);
			success = saveHexieMessage(exr, user);
			if (success) {
				successFlag = true;	//当前这户，有一个绑定者成功就算成功
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
		hexieMessageRepository.save(hexieMessage);
		
		boolean success = true;
		if (!StringUtils.isEmpty(user.getWuyeId())) {
			success = gotongService.sendGroupMessage(user.getOpenid(), user.getAppId(), hexieMessage.getId(), hexieMessage.getContent());
		}else {
			success = false;
		}
		hexieMessage.setSuccess(success);
		hexieMessageRepository.save(hexieMessage);
		return success;
	}
	
	@Override
	public HexieMessage getMessage(long messageId) {
		
		return hexieMessageRepository.findById(messageId).get();
	}
	
	@Override
	@Transactional
	public void authorize(User user, String sectIds, String timestamp) {
		
		Assert.hasText(timestamp, "timestamp is null!");
		
		Long ts = Long.valueOf(timestamp);
		if (System.currentTimeMillis() - ts > 30*60*1000 ) {
			throw new BizValidateException("授权码已失效。");
		}
		
		ServiceOperator so = new ServiceOperator();
		so.setName(user.getName());
		so.setTel(user.getTel());
		so.setUserId(user.getId());
		so.setType(ModelConstant.SERVICE_OPER_TYPE_MSG_SENDER);
		so.setOpenId(user.getOpenid());
		serviceOperatorRepository.save(so);
		
		String[]sectArr = sectIds.split(",");
		for (String sect : sectArr) {
			ServiceOperatorSect sos = new ServiceOperatorSect();
			sos.setOperatorId(so.getId());
			sos.setSectId(sect);
			serviceOperatorSectRepository.save(sos);
		}
		
		
	}

}

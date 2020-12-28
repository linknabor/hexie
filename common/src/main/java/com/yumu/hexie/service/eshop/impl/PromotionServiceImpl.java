package com.yumu.hexie.service.eshop.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.yumu.hexie.integration.eshop.service.EshopUtil;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.SmsService;
import com.yumu.hexie.service.eshop.PromotionService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.UserService;

public class PromotionServiceImpl implements PromotionService {

	private static Logger logger = LoggerFactory.getLogger(PromotionServiceImpl.class);

	@Autowired
	private UserService userService;
	@Autowired
	private EshopUtil eshopUtil;
	@Autowired
	private GotongService gotongService;
	@Autowired
	private SmsService smsService;

	@Override
	public void resetPassword(User user, String vericode) throws Exception {

		logger.info("client vericode : " + vericode);

		User currUser = userService.getById(user.getId());
		if (StringUtils.isEmpty(currUser.getTel())) {
			throw new BizValidateException("当前用户尚未注册。");
		}

		boolean result = smsService.checkVerificationCode(user.getTel(), vericode);
		if (!result) {
			throw new BizValidateException("验证码不正确。");
		}
		String password = eshopUtil.resetPassword(currUser);
		if (StringUtils.isEmpty(password)) {
			return;
		}
		// 发送模板消息
		gotongService.sendResetPasswordMsg(currUser, password);
	}
	
}

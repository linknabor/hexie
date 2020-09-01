package com.yumu.hexie.service.eshop.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.yumu.hexie.integration.eshop.service.EshopUtil;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.eshop.PromotionService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.UserService;

public class PromotionServiceImpl implements PromotionService {
	
	@Autowired
	private UserService userService;
	@Autowired
	private EshopUtil eshopUtil;
	@Autowired
	private GotongService gotongService;
	
	@Override
	public void resetPassword(User user) throws Exception {

		User currUser = userService.getById(user.getId());
		if (StringUtils.isEmpty(user.getTel())) {
			throw new BizValidateException("当前用户尚未注册。"); 
		}
		String password = eshopUtil.resetPassword(currUser);
		if (StringUtils.isEmpty(password)) {
			return;
		}
		//发送模板消息
		gotongService.sendResetPasswordMsg(currUser, password);
	}

}

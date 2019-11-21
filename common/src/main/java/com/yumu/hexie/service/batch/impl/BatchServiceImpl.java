package com.yumu.hexie.service.batch.impl;

import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.batch.BatchService;
import com.yumu.hexie.service.shequ.WuyeService;
import com.yumu.hexie.service.user.UserService;

@Service
public class BatchServiceImpl implements BatchService {

	private static Logger logger = LoggerFactory.getLogger(BatchServiceImpl.class);

	@Autowired
	UserService userService;
	
	@Autowired
	WuyeService wuyeService;

	@Override
	public void updateUserShareCode() {
		List<User> list = userService.getShareCodeIsNull();
		for (User user : list) {
			try {
				String shareCode = DigestUtils.md5Hex("UID[" + user.getId() + "]");
				user.setShareCode(shareCode);
				userService.save(user);
			} catch (Exception e) {
				logger.error("user保存失败：" + user.getId());
			}
		}

	}

	@Override
	public void updateRepeatUserShareCode() {
		List<String> repeatUserList = userService.getRepeatShareCodeUser();
		for (String string : repeatUserList) {
			List<User> uList = userService.getUserByShareCode(string);
			for (User user2 : uList) {
				try {
					String shareCode = DigestUtils.md5Hex("UID[" + user2.getId() + "]");
					user2.setShareCode(shareCode);
					userService.save(user2);
				} catch (Exception e) {
					logger.error("user保存失败：" + user2.getId());
				}
			}
		}

	}

	/**
	 * 手工绑定房屋
	 * @param userId
	 * @param tradeWaterId
	 */
	@Override
	public void fixBindHouse(String userId, String tradeWaterId) {

		User user = userService.getById(Long.valueOf(userId));
		wuyeService.bindHouseByTradeAsync("1", user, tradeWaterId);
	}

}

package com.yumu.hexie.service.cache.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.cache.CacheService;

@Service
public class CacheServiceImpl implements CacheService {

	@Override
	public void clearUserCache(User user) {
		String clearKey = user.getOpenid();
		if ("0".equals(clearKey)) {	//微信小程序用户openid可能为0
			clearKey = "";
		}
		if (StringUtils.isEmpty(clearKey)) {
			if (!StringUtils.isEmpty(user.getAliuserid())) {
				clearKey = user.getAliappid() + "_" + user.getAliuserid();
			}
		}
		if (StringUtils.isEmpty(clearKey)) {
			clearKey = user.getMiniopenid();
		}
		if (!StringUtils.isEmpty(clearKey)) {
			delCache(clearKey);
		}
	}
	
	/**
	 * 删除用户缓存
	 * @param key 对于微信公众号用户，key是openid，对于微信小程序用户,key是miniopenid，对于支付宝用户,key是aliappid_aliuserid
	 */
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_CACHED, key = "#key", condition = "#key != null && #key != ''")
	@Override
	public void delCache(String key) {
	}
}

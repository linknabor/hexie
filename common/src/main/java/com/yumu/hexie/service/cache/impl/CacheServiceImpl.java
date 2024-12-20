package com.yumu.hexie.service.cache.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.cache.CacheService;

@Service
public class CacheServiceImpl implements CacheService {
	
	private static Logger log = LoggerFactory.getLogger(CacheServiceImpl.class);

	@Override
	public String getCacheKey(User user) {
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
		return clearKey;
	}
	
	/**
	 * 删除用户缓存
	 * @param key 对于微信公众号用户，key是openid，对于微信小程序用户,key是miniopenid，对于支付宝用户,key是aliappid_aliuserid
	 */
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_CACHED, key = "#key")
	@Override
	public void clearUserCache(String key) {
		log.info("del key is : " + key);
	}
}

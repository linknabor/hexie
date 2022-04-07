package com.yumu.hexie.integration.wechat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.wechat.entity.UserMiniprogram;
import com.yumu.hexie.service.exception.BizValidateException;

@Service
public class MiniprogramAuthService {
	@Value("${wechat.miniprogramAppId}")
	private String miniprogramAppid;
	
	@Value("${wechat.miniprogramSecret}")
	private String miniprogramSecret;
	
	@Autowired
	private RestUtil restUtil;
	
	public static final String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";

	/**
	 * 通过code获取微信小程序用户信息
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public UserMiniprogram getMiniUserSessionKey(String code) throws Exception {
		
		String requestUrl = CODE2SESSION_URL.replaceAll("APPID", miniprogramAppid).replaceAll("SECRET", miniprogramSecret)
		.replaceAll("JSCODE", code);
		
		TypeReference<UserMiniprogram> typeReference = new TypeReference<UserMiniprogram>() {};
		UserMiniprogram userMiniprogram = restUtil.exchangeOnUri(requestUrl, null, typeReference);
		if (!StringUtils.isEmpty(userMiniprogram.getErrcode())) {
			throw new BizValidateException(userMiniprogram.getErrcode() + ", " + userMiniprogram.getErrmsg());
		}
		return userMiniprogram;
	}
}

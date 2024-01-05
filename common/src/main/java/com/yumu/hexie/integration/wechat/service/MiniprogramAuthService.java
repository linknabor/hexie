package com.yumu.hexie.integration.wechat.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.integration.wechat.entity.MiniAccessToken;
import com.yumu.hexie.integration.wechat.entity.MiniUserPhone;
import com.yumu.hexie.integration.wechat.entity.UserMiniprogram;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.exception.BizValidateException;

@Service
public class MiniprogramAuthService {
	
	@Autowired
	private SystemConfigService systemConfigService;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private RestUtil restUtil;
	
	public static final String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";

	public static final String CODE4PHONE_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=ACCESS_TOKEN";

	public static final String MINI_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

	public static final String MINI_QRCODE_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=ACCESS_TOKEN";
	
	public static final String MINI_APP_KEY_PREFIX = "MP_APP_";

	/**
	 * 通过code获取微信小程序用户信息
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public UserMiniprogram getMiniUserSessionKey(String code) throws Exception {
		
		String requestUrl = CODE2SESSION_URL.replaceAll("APPID", ConstantWeChat.MINIPROGRAM_APPID).replaceAll("SECRET", ConstantWeChat.MINIPROGRAM_SECRET)
		.replaceAll("JSCODE", code);
		
		TypeReference<UserMiniprogram> typeReference = new TypeReference<UserMiniprogram>() {};
		UserMiniprogram userMiniprogram = restUtil.exchangeOnUri(requestUrl, null, typeReference);
		if (!StringUtils.isEmpty(userMiniprogram.getErrcode())) {
			throw new BizValidateException(userMiniprogram.getErrcode() + ", " + userMiniprogram.getErrmsg());
		}
		return userMiniprogram;
	}
	
	/**
	 * 通过appid和code获取微信小程序用户信息
	 * @param miniAppid
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public UserMiniprogram getMiniUserSessionKeyByAppid(String miniAppid, String code) throws Exception {
		
		if (StringUtils.isEmpty(miniAppid)) {
			throw new BizValidateException("miniappid is empty ! ");
		}
		
		String appKey = MINI_APP_KEY_PREFIX + miniAppid;
		String appSecret = systemConfigService.getSysConfigByKey(appKey);
		
		String requestUrl = CODE2SESSION_URL.replaceAll("APPID", miniAppid).replaceAll("SECRET", appSecret).replaceAll("JSCODE", code);
		
		TypeReference<UserMiniprogram> typeReference = new TypeReference<UserMiniprogram>() {};
		UserMiniprogram userMiniprogram = restUtil.exchangeOnUri(requestUrl, null, typeReference);
		if (!StringUtils.isEmpty(userMiniprogram.getErrcode())) {
			throw new BizValidateException(userMiniprogram.getErrcode() + ", " + userMiniprogram.getErrmsg());
		}
		userMiniprogram.setAppid(miniAppid);
		return userMiniprogram;
		
	}
	
	/**
	 * 获取小程序access_token
	 * @return
	 * @throws Exception
	 */
	public MiniAccessToken getMiniAccessToken(String miniAppid) throws Exception {
		
		String appKey = MINI_APP_KEY_PREFIX + miniAppid;
		String appSecret = systemConfigService.getSysConfigByKey(appKey);
		
		String requestUrl = MINI_ACCESS_TOKEN_URL.replaceAll("APPID", miniAppid).replaceAll("APPSECRET", appSecret);
		TypeReference<MiniAccessToken> typeReference = new TypeReference<MiniAccessToken>() {};
		MiniAccessToken miniAccessToken = restUtil.exchangeOnUri(requestUrl, null, typeReference);
		return miniAccessToken;
	}
	
	/**
	 * 获取小程序用户手机
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public MiniUserPhone getPhoneNumber(String code, String miniprogramAppid) throws Exception {
		
		if (StringUtils.isEmpty(miniprogramAppid)) {
			miniprogramAppid = ConstantWeChat.MINIPROGRAM_APPID;
		}
		String key = ModelConstant.KEY_MINI_ACCESS_TOKEN + miniprogramAppid;
		String accessToken = stringRedisTemplate.opsForValue().get(key);
		String requestUrl = CODE4PHONE_URL.replaceAll("ACCESS_TOKEN", accessToken);
		TypeReference<MiniUserPhone> typeReference = new TypeReference<MiniUserPhone>() {};
		Map<String, String> map = new HashMap<>();
		map.put("code", code);
		MiniUserPhone miniUserPhone = restUtil.exchangeOnBody(requestUrl, map, typeReference);
		return miniUserPhone;
	}

	/**
	 * 获取小程序二维码
	 * @param miniprogramAppid
	 * @param page
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String getUnlimitedQrcode(String page, String param, String miniprogramAppid) throws Exception {
		
		if (StringUtils.isEmpty(miniprogramAppid)) {
			miniprogramAppid = ConstantWeChat.MINIPROGRAM_APPID;
		}
		String key = ModelConstant.KEY_MINI_ACCESS_TOKEN + miniprogramAppid;
		String accessToken = stringRedisTemplate.opsForValue().get(key);
		if(ObjectUtils.isEmpty(accessToken)) {
			accessToken = getMiniAccessToken(miniprogramAppid).getAccess_token();
		}

		String requestUrl = MINI_QRCODE_URL.replaceAll("ACCESS_TOKEN", accessToken);
		Map<String, String> map = new HashMap<>();
		map.put("page", page);
		map.put("scene", param);
		return restUtil.exchange4Base64StrOnUri(requestUrl, map);
	}
	
}

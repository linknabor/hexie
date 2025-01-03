package com.yumu.hexie.integration.alipay.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.yumu.hexie.integration.wechat.constant.ConstantAlipay;
import com.yumu.hexie.integration.wechat.entity.AccessTokenOAuth;
import com.yumu.hexie.service.exception.BizValidateException;

@Service(value = "alipayAuthService")
public class AuthService {
	
	private static Logger logger = LoggerFactory.getLogger(AuthService.class);
	
	@Resource
	private AliSDKService aliSDKService;
	
	/**
	 * 根据appid和authCode获取支付宝用户授权
	 * @param appid
	 * @param code
	 * @return
	 */
	public AccessTokenOAuth getAlipayAuth(String appid, String code) {
		
		Assert.hasText(code, "code不能为空。");
		logger.info("getAlipayAuth, appid is : " + appid);
		
		AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
		request.setCode(code);
		request.setGrantType(ConstantAlipay.AUTHORIZATION_TYPE);
		AccessTokenOAuth oAuth = new AccessTokenOAuth();
		try {
			AlipayClient execClient = aliSDKService.getClient(appid);
			logger.info("grantType: " + ConstantAlipay.AUTHORIZATION_TYPE);
		    AlipaySystemOauthTokenResponse oauthTokenResponse = execClient.execute(request);
		    oAuth.setOpenid(oauthTokenResponse.getUserId());
		    oAuth.setAccessToken(oauthTokenResponse.getAccessToken());
		    oAuth.setExpiresIn(Integer.valueOf(oauthTokenResponse.getExpiresIn()));
		    if (StringUtils.isEmpty(appid)) {
				appid = ConstantAlipay.APPID;
			}
		    oAuth.setAppid(appid);
		    logger.info("oAuth is : " + oAuth);
		} catch (AlipayApiException e) {
			throw new BizValidateException(e.getMessage(), e);
		}
		return oAuth;
		
	}
	
}

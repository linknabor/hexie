package com.yumu.hexie.common.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.wechat.constant.ConstantAlipay;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;

@Configuration
public class ConstantConfig {

	@Value("${mainServer}")
	private Boolean mainServer;
	
	/*微信参数begin#################################*/
	@Value("${wechat.appId}")
	private String wechatAppId;
	
	@Value("${wechat.appSecret}")
	private String wechatAppSecret;
	
	@Value("${wechat.componentAppId}")
	private String wechatComponentAppId;
	
	@Value("${wechat.mchId}")
	private String wechatMchId;
	
	@Value("${wechat.mchKey}")
	private String wechatMchKey;
	
	@Value("${wechat.certPath}")
	private String wechatCertPath;
	
	@Value("${wechat.unifiedUrl}")
	private String wechatUnifiedUrl;
	
	@Value("${wechat.notifyUrl}")
	private String wechatNotifyUrl;
	/*微信参数end#################################*/
	
	/*支付宝参数begin#################################*/
	@Value("${alipay.appId}")
	private String alipayAppId;
	
	@Value("${alipay.appSecret}")
	private String alipayAppSecret;
	
	@Value("${alipay.gateway}")
	private String alipayGateway;
	
	@Value("${alipay.appPrivateKey}")
	private String alipayAppPrivateKey;
	
	@Value("${alipay.publicKey}")
	private String alipayPublicKey;
	/*支付宝参数end#################################*/
	
	@PostConstruct
	public void init()	{
		
		Constants.MAIN_SERVER = mainServer;
		
		ConstantWeChat.APPID = wechatAppId;
		ConstantWeChat.APPSECRET = wechatAppSecret;
		ConstantWeChat.MERCHANT_ID = wechatMchId;
		ConstantWeChat.MERCHANT_KEY = wechatMchKey;
		ConstantWeChat.KEYSTORE = wechatCertPath;
		ConstantWeChat.UNIFIEDURL = wechatUnifiedUrl;
		ConstantWeChat.NOTIFYURL = wechatNotifyUrl;
		ConstantWeChat.COMPONENT_APPID = wechatComponentAppId;
		
		ConstantAlipay.APPID = alipayAppId;
		ConstantAlipay.SECRET = alipayAppSecret;
		ConstantAlipay.APP_PRIVATE_KEY = alipayAppPrivateKey;
		ConstantAlipay.PUBLIC_KEY = alipayPublicKey;
		ConstantAlipay.GATEWAY = alipayGateway;
	}
}
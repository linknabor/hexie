package com.yumu.hexie.integration.wechat.constant;

import com.yumu.hexie.common.util.AliConfigUtil;

public class ConstantAlipay {
	
	public static final String CHARSET = "utf-8";
	
	public static final String SIGNTYPE = "RSA2";
	
	public static final String DATAFORMAT = "json";
	
	public static final String AUTHORIZATION_TYPE = "authorization_code";

	/**
	 * 第三方用户唯一凭证
	 */
	public static String APPID = AliConfigUtil.get("appId");
	/**
	 * 与接口配置信息中的Token要一致
	 */
	public static String SECRET = AliConfigUtil.get("appSecret");
	
	public static String APP_PRIVATE_KEY = AliConfigUtil.get("appPrivateKey");	//商户私钥
	
	public static String ALIPAY_PUBLIC_KEY = AliConfigUtil.get("alipayPublicKey");	//alipay公钥
	
	public static String ALIPAY_GATEWAY = AliConfigUtil.get("alipayGetway");	//alipay支付网关
	
}

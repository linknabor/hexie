package com.yumu.hexie.integration.alipay.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.yumu.hexie.integration.wechat.constant.ConstantAlipay;
import com.yumu.hexie.service.common.SystemConfigService;

/**
 * 获取alipay提供的SDK
 * @author david
 *
 */
@Component
public class AliSDKService {

	private static Logger log = LoggerFactory.getLogger(AliSDKService.class);
	
	private static final String KEY_ALIPAY_PUB_KEY = "ALIPAY_MP_PUBKEY_";
	private static final String KEY_ALIPAY_APP_PRI_KEY = "ALIPAY_MP_APP_PRIKEY_";
	
	private Map<String, AlipayClient> clientMap;
	
	@Resource
	private AlipayClient alipayClient;
	@Resource
	private SystemConfigService systemConfigService;
	
	/**
	 * 根据appid和authCode获取支付宝用户授权
	 * @param appid
	 * @param code
	 * @return
	 */
	public AlipayClient getClient(String appid) {
		
		log.info("getAlipayAuth, appid is : " + appid);
		
		AlipayClient execClient = null;
		if (StringUtils.isEmpty(appid)) {
			execClient = alipayClient;
		} else {
			if (clientMap == null) {
				clientMap = new HashMap<String, AlipayClient>();
			}
			execClient = clientMap.get(appid);
			if (execClient == null) {
				String pubSysKey = KEY_ALIPAY_PUB_KEY + appid;
				String priSysKey = KEY_ALIPAY_APP_PRI_KEY + appid;
				String alipayPublicKey = systemConfigService.getSysConfigByKey(pubSysKey);
				String alipayAppPrivateKey = systemConfigService.getSysConfigByKey(priSysKey);
				
				execClient = new DefaultAlipayClient(ConstantAlipay.GATEWAY, appid, 
						alipayAppPrivateKey, ConstantAlipay.DATAFORMAT, ConstantAlipay.CHARSET, 
						alipayPublicKey, ConstantAlipay.SIGNTYPE);
				
				clientMap.put(appid, execClient);
			}
		}
		return execClient;
		
	}

}

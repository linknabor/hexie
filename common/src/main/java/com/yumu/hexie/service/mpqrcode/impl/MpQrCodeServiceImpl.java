package com.yumu.hexie.service.mpqrcode.impl;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.mpqrcode.MpQrCodeService;
import com.yumu.hexie.service.mpqrcode.req.CreateMpQrCodeReq;
import com.yumu.hexie.service.mpqrcode.req.GenMpQrCodeRequest;
import com.yumu.hexie.service.mpqrcode.req.GenMpQrCodeRequest.ActionInfo;
import com.yumu.hexie.service.mpqrcode.req.GenMpQrCodeRequest.Scene;
import com.yumu.hexie.service.mpqrcode.resp.GenMpQrCodeResponse;

public class MpQrCodeServiceImpl implements MpQrCodeService {
	
	private static Logger logger = LoggerFactory.getLogger(MpQrCodeServiceImpl.class);
	
	private static final String CREATE_QR_CODE_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";
	private static final String QR_STR_SCENE = "QR_STR_SCENE";
	private static final String SEPERATOR = "|";
	private static final String ELLIPSIS = "…";
	
	@Autowired
	private SystemConfigService systemConfigService;
	
	@Autowired
	private RestUtil restUtil;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Override
	public String createQrCode(CreateMpQrCodeReq createQrCodeReq) throws Exception {

		logger.info("createQrCodeReq : " + createQrCodeReq);
		
		String orderId = createQrCodeReq.getOrderId();
		String tranAmt = createQrCodeReq.getTranAmt();
		String shopName = createQrCodeReq.getShopName();
		String appid = createQrCodeReq.getAppid();
		
		Assert.hasText(orderId, "交易ID不能为空");
		Assert.hasText(tranAmt, "交易金额不能未空");
		Assert.hasText(shopName, "商户名称不能为空");
		Assert.hasText(appid, "appid不能为空");
		
		String keyStr = "01"+"_"+orderId+"_"+appid;
		String key = ModelConstant.KEY_MP_QRCODE_CACHED + keyStr;
		String qrCodeUrl = stringRedisTemplate.opsForValue().get(key);
		if (!StringUtils.isEmpty(qrCodeUrl)) {	//同一场景下，同一个公众号的同一笔流水申请二维码，只从腾讯生成一次。后面取缓存里的
			return qrCodeUrl;
		}
		
		//01表示场景
		String sceneStr = "01" +  SEPERATOR + orderId + SEPERATOR + tranAmt + SEPERATOR + shopName;	//01代表场景
		if (sceneStr.length()>64) {	//二维码参数最大长度为64
			sceneStr = sceneStr.substring(0, 63) + ELLIPSIS;
		}
		GenMpQrCodeRequest genQrCodeRequest = new GenMpQrCodeRequest();
		genQrCodeRequest.setExpireSeconds(3600l*24*30);
		genQrCodeRequest.setActionName(QR_STR_SCENE);
		Scene scene = new Scene();
		scene.setSceneStr(sceneStr);
		ActionInfo actionInfo = new ActionInfo();
		actionInfo.setScene(scene);
		genQrCodeRequest.setActionInfo(actionInfo);
		
		logger.info("genQrCodeRequest : " + JacksonJsonUtil.getMapperInstance(false).writeValueAsString(genQrCodeRequest));
		
		String accessToken = systemConfigService.queryWXAToken(appid);
		String requestUrl = CREATE_QR_CODE_URL.replaceAll("ACCESS_TOKEN", accessToken);
		
		TypeReference<GenMpQrCodeResponse> typeReference = new TypeReference<GenMpQrCodeResponse>() {};
		GenMpQrCodeResponse genQrCodeResponse = restUtil.exchangeOnBody(requestUrl, genQrCodeRequest, typeReference);
		
		stringRedisTemplate.opsForValue().set(key, genQrCodeResponse.getUrl(), 24, TimeUnit.HOURS);	//一天过期
		return genQrCodeResponse.getUrl();
	}
	
	public static void main(String[] args) {
		
		String str = "…";
		System.out.println(str.length());
	}

}

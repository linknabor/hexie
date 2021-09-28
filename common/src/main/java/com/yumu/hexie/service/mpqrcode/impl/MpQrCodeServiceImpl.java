package com.yumu.hexie.service.mpqrcode.impl;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.QRCodeUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.wuye.WuyeUtil2;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.resp.MpQrCodeParam;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;
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
	@Autowired
	private WuyeUtil2 wuyeUtil2;
	
	@Value(value="qrcode.image.dir")
	private String qrcodeImgPath;

	@Override
	public String createQrCode(CreateMpQrCodeReq createQrCodeReq) throws Exception {

		logger.info("createQrCodeReq : " + createQrCodeReq);
		
		String orderId = createQrCodeReq.getOrderId();
		String tranAmt = createQrCodeReq.getTranAmt();
		String shopName = createQrCodeReq.getShopName();
		String appid = createQrCodeReq.getAppid();
		
		Assert.hasText(orderId, "交易ID不能为空");
		Assert.hasText(tranAmt, "交易金额不能为空");
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
	
	@Override
	public String getQrCode(String tradeWaterId) throws Exception {

		logger.info("getQrCode, tradeWaterId : " + tradeWaterId);
		Assert.hasText(tradeWaterId, "交易ID不能为空");
		User user = new User();
		String qrCodeStr = "";
		BaseResult<MpQrCodeParam> baseResult = wuyeUtil2.queryMpQrCodeParam(user, tradeWaterId);
		if (baseResult.isSuccess()) {
			MpQrCodeParam mpQrCodeParam = baseResult.getData();
			CreateMpQrCodeReq req = new CreateMpQrCodeReq();
			req.setAppid(mpQrCodeParam.getAppid());
			req.setOrderId(mpQrCodeParam.getTrade_water_id());
			req.setShopName(mpQrCodeParam.getShop_name());
			req.setTranAmt(mpQrCodeParam.getTran_amt());
			String mpUrl = createQrCode(req);
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				String logoPath = qrcodeImgPath;
				QRCodeUtil.createQRCodeToIO(mpUrl, logoPath, os);
				String codeStr = new String (Base64.getEncoder().encode(os.toByteArray()));
				qrCodeStr = "data:image/jpg;base64," + codeStr;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			
		} else {
			logger.error("can't not gen qrcode, msg : " + baseResult.getMessage());
		}
		return qrCodeStr;
	}
}

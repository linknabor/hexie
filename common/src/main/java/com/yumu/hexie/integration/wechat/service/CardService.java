package com.yumu.hexie.integration.wechat.service;

import java.util.Map;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.wechat.entity.card.ActivateReq;
import com.yumu.hexie.integration.wechat.entity.card.ActivateResp;
import com.yumu.hexie.integration.wechat.entity.card.ActivateTempInfoResp;
import com.yumu.hexie.integration.wechat.entity.card.ActivateUrlReq;
import com.yumu.hexie.integration.wechat.entity.card.ActivateUrlResp;
import com.yumu.hexie.integration.wechat.entity.card.DecryptCodeResp;
import com.yumu.hexie.integration.wechat.entity.card.GetUserCardResp;
import com.yumu.hexie.integration.wechat.entity.card.UpdateUserCardReq;
import com.yumu.hexie.integration.wechat.entity.card.UpdateUserCardResp;

/**
 * 微信会员卡
 * @author david
 *
 */
@Component
public class CardService {

	private static Logger logger = LoggerFactory.getLogger(CardService.class);
	
	//获取开卡组件的链接，用于预激活
	private static final String GET_PREACTIVATE_URL = "https://api.weixin.qq.com/card/membercard/activate/geturl?access_token= ACCESS_TOKEN";
	//获取activate_ticket，这个ticket是最终激活用的
	private static String ACTIVATE_URL = "https://api.weixin.qq.com/card/membercard/activate?access_token=ACCESS_TOKEN";
	//CODE解码
	private static String CODE_DECRYPT_URL ="https://api.weixin.qq.com/card/code/decrypt?access_token=ACCESS_TOKEN";
	//开卡个人信息
	private static String ACTIVATE_TEMP_INFO_URL = "https://api.weixin.qq.com/card/membercard/activatetempinfo/get?access_token=ACCESS_TOKEN";
	//更新卡券积分
	private static String UPDATE_USER_CARD_URL = "https://api.weixin.qq.com/card/membercard/updateuser?access_token=ACCESS_TOKEN";
	//查询用户卡券积分
	private static String GET_USER_CARD_INFO_URL = "https://api.weixin.qq.com/card/membercard/userinfo/get?access_token=ACCESS_TOKEN";
	
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * 获取会员卡激活链接（使用微信开卡组件）
	 * @param wechatCard	微信会员卡
	 * @param accessToken
	 * @param outerStr	投放渠道，便于统计
	 */
	public ActivateUrlResp getMemberCardActivateUrl(ActivateUrlReq activateUrlReq, String accessToken) {
	
		String reqUrl = GET_PREACTIVATE_URL.replaceAll("ACCESS_TOKEN", accessToken);
		ActivateUrlResp activateUrlResp = (ActivateUrlResp) restRequest(activateUrlReq, ActivateUrlResp.class, reqUrl, accessToken);
		return activateUrlResp;
	}
	
	/**
	 * 会员卡激活
	 * @param activateReq
	 * @param accessToken
	 * @return
	 */
	public ActivateResp activateMemberCard(ActivateReq activateReq, String accessToken) {
		
		String reqUrl = ACTIVATE_URL.replaceAll("ACCESS_TOKEN", accessToken);
		ActivateResp activateResp = (ActivateResp) restRequest(activateReq, ActivateResp.class, reqUrl, accessToken);
		return activateResp;
		
	}
	
	/**
	 * 会员卡code解码
	 * @param map
	 * @param accessToken
	 * @return
	 */
	public DecryptCodeResp decryptMemberCardCode(Map<String, String> map, String accessToken) {
		
		String reqUrl = CODE_DECRYPT_URL.replaceAll("ACCESS_TOKEN", accessToken);
		DecryptCodeResp decryptCodeResp = (DecryptCodeResp) restRequest(map, DecryptCodeResp.class, reqUrl, accessToken);
		return decryptCodeResp;
	}
	
	/**
	 * 获取微信用户开卡时填写的用户信息
	 * @param map
	 * @param accessToken
	 * @return
	 */
	public ActivateTempInfoResp getActivateTempInfo(Map<String, String> map, String accessToken) {
		
		String reqUrl = ACTIVATE_TEMP_INFO_URL.replaceAll("ACCESS_TOKEN", accessToken);
		ActivateTempInfoResp activateTempInfoResp = (ActivateTempInfoResp) restRequest(map, ActivateTempInfoResp.class, reqUrl, accessToken);
		return activateTempInfoResp;
	}
	
	/**
	 * 更新用户卡券信息，一般为更新积分或者余额操作，此操作需要具有幂等性
	 */
	public UpdateUserCardResp updateUserMemeberCard(UpdateUserCardReq updateUserCardReq, String accessToken) {
		
		String reqUrl = UPDATE_USER_CARD_URL.replaceAll("ACCESS_TOKEN", accessToken);
		UpdateUserCardResp updateUserCardResp = (UpdateUserCardResp) restRequest(updateUserCardReq, UpdateUserCardResp.class, reqUrl, accessToken);
		return updateUserCardResp;
		
	}
	
	/**
	 * 查询用户卡券信息
	 * @param getMap, map中2个key:card_id, code
	 * @param accessToken
	 * @return
	 */
	public GetUserCardResp getUserCardInfo(Map<String ,String> getMap, String accessToken) {
		
		String reqUrl = GET_USER_CARD_INFO_URL.replaceAll("ACCESS_TOKEN", accessToken);
		GetUserCardResp getUserCardResp = (GetUserCardResp) restRequest(getMap, GetUserCardResp.class, reqUrl, accessToken);
		return getUserCardResp;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public Object restRequest(Object requestObj, Class respClazz, String reqUrl, String accessToken) {
		
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String reqData = "";
        try {
        	reqData = JacksonJsonUtil.beanToJson(requestObj);
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
        HttpEntity<Object> httpEntity = new HttpEntity<>(reqData, headers);
		reqUrl = reqUrl.replaceAll("ACCESS_TOKEN", accessToken);
		
		logger.info("wechat card request url : " + reqUrl + ", request data : " + reqData);
		ResponseEntity<String> responseEntity = restTemplate.exchange(reqUrl, HttpMethod.POST, httpEntity, String.class);
		logger.info("wechat card response: " + responseEntity);
		Object returnObject = null;
		try {
			returnObject = JacksonJsonUtil.jsonToBean(responseEntity.getBody(), respClazz);
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
		return returnObject;
		
	}
	
}

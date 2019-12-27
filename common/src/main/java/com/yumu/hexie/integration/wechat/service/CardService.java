package com.yumu.hexie.integration.wechat.service;

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
import com.yumu.hexie.integration.wechat.entity.card.ActivateUrlReq;
import com.yumu.hexie.integration.wechat.entity.card.ActivateUrlResp;

/**
 * 微信会员卡
 * @author david
 *
 */
@Component
public class CardService {

	private static Logger logger = LoggerFactory.getLogger(CardService.class);
	
	private static final String MEMBERCARD_ACTIVATE_URL = "https://api.weixin.qq.com/card/membercard/activate/geturl?access_token= ACCESS_TOKEN";
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * 获取会员卡激活链接（使用微信开卡组件）
	 * @param wechatCard	微信会员卡
	 * @param accessToken
	 * @param outerStr	投放渠道，便于统计
	 */
	public ActivateUrlResp getMemberCardActivateUrl(ActivateUrlReq activateUrlReq, String accessToken) {
	
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String reqData = "";
        try {
        	reqData = JacksonJsonUtil.beanToJson(activateUrlReq);
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
        HttpEntity<Object> httpEntity = new HttpEntity<>(reqData, headers);
		String reqUrl = MEMBERCARD_ACTIVATE_URL.replaceAll("ACCESS_TOKEN", accessToken);
		
		logger.info("获获取会员卡开卡组件， req : " + activateUrlReq);
		ResponseEntity<ActivateUrlResp> responseEntity = restTemplate.exchange(reqUrl, HttpMethod.POST, httpEntity, ActivateUrlResp.class);
		logger.info("获取会员卡开卡组件， resp : " + responseEntity);
		return responseEntity.getBody();
	}
	
}

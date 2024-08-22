package com.yumu.hexie.integration.beyondsoft;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.common.util.DESUtil;
import com.yumu.hexie.integration.beyondsoft.resp.BeyondSoftResp;
import com.yumu.hexie.integration.beyondsoft.resp.BeyondSoftToken;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.statistic.StatisticData;
import com.yumu.hexie.service.exception.BizValidateException;

@Service
public class BeyondSoftUtil {
	
	private static Logger log = LoggerFactory.getLogger(BeyondSoftUtil.class);

	@Autowired
	private RestUtil restUtil;
	@Autowired
	private BeyondSoftConfig beyondSoftConfig;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	/**
	 * 获取token
	 * @return
	 * @throws Exception
	 */
	public BeyondSoftToken getAccessToken() throws Exception {
		
		String requestUrl = beyondSoftConfig.getBaseUrl() + beyondSoftConfig.getLoginUri();
		String password = beyondSoftConfig.getPassword();
		String appkey = beyondSoftConfig.getAppkey();
		String pwd = DESUtil.encryptByKey(password, appkey);
		
		Map<String, String> jsonMap = new HashMap<>();
		jsonMap.put("username", beyondSoftConfig.getUsername());
		jsonMap.put("pwd", pwd);
		
		BeyondSoftToken beyondSoftToken = null;
		TypeReference<BeyondSoftResp<Map<String, BeyondSoftToken>>> typeReference = new TypeReference<BeyondSoftResp<Map<String,BeyondSoftToken>>>() {};
		BeyondSoftResp<Map<String, BeyondSoftToken>> byResp = restUtil.postOnBodyWithHeader(requestUrl, jsonMap, typeReference, null);
		if ("200".equals(byResp.getCode())) {
			Map<String, BeyondSoftToken> respData = byResp.getData();
			beyondSoftToken = respData.get("content");
		} else {
			throw new BizValidateException(byResp.getMsg());
		}
		return beyondSoftToken;
		
	}
	
	
	/**
	 * 同步西部小程序数据
	 * @return
	 * @throws Exception
	 */
	public void syncMiniProgramData(StatisticData statisticData) throws Exception {
		
		String requestUrl = beyondSoftConfig.getBaseUrl() + beyondSoftConfig.getStatisticUri();
		Map<String, String> jsonMap = new HashMap<>();
		jsonMap.put("clickVolume", String.valueOf(statisticData.getClickCount()));
		jsonMap.put("amountOfAttention", String.valueOf(statisticData.getRegisterCount()));
		jsonMap.put("bindingQuantity", String.valueOf(statisticData.getBindCount()));
		
		String accessToken = getWestAccessToken();
		if (StringUtils.isEmpty(accessToken)) {
			throw new BizValidateException("未获取到token");
		}
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Authorization", "bearer " + accessToken);
		TypeReference<BeyondSoftResp<Map<String, Object>>> typeReference = new TypeReference<BeyondSoftResp<Map<String,Object>>>() {};
		BeyondSoftResp<Map<String, Object>> byResp = restUtil.postOnBodyWithHeader(requestUrl, jsonMap, typeReference, headerMap);
		if (!"200".equals(byResp.getCode())) {
			log.error(byResp.getMsg());
			throw new BizValidateException(byResp.getMsg());
		}
	}
	
	/**
	 * 获取博彦-西部集团的token
	 * @return
	 */
	private String getWestAccessToken() {
		String accessToken = stringRedisTemplate.opsForValue().get(ModelConstant.BEYONDSOFT_TOKEN_WEST);
		if (StringUtils.isEmpty(accessToken)) {
			try {
				BeyondSoftToken beyondSoftToken = getAccessToken();
				if (beyondSoftToken != null) {
					stringRedisTemplate.opsForValue().setIfAbsent(ModelConstant.BEYONDSOFT_TOKEN_WEST, beyondSoftToken.getAccess_token(), beyondSoftToken.getExpires_in().longValue(), TimeUnit.SECONDS);
					accessToken = beyondSoftToken.getAccess_token();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return accessToken;
	}
}

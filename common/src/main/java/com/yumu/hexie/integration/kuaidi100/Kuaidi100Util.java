package com.yumu.hexie.integration.kuaidi100;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.kuaidi100.resp.LogisticCompanyQueryResp;

@Component
public class Kuaidi100Util {
	
	private Logger logger = LoggerFactory.getLogger(Kuaidi100Util.class);
	
	@Value("${kuaidi100.autonumber.url}")
	private String requestUrl;
	@Value("${kuaidi100.key}")
	private String key;

	@Autowired
	private RestUtil restUtil;
	
	public List<LogisticCompanyQueryResp> queryByTrackingNo(String trackingNo) {
		
		TypeReference<List<LogisticCompanyQueryResp>> typeReference = new TypeReference<List<LogisticCompanyQueryResp>>() {};
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("key", key);
		paramMap.put("num", trackingNo);
		List<LogisticCompanyQueryResp> list = null;
		try {
			list = restUtil.exchangeOnUri(requestUrl, paramMap, typeReference);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return list;
	}
}

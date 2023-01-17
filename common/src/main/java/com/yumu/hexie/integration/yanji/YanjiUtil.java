package com.yumu.hexie.integration.yanji;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.integration.common.RestUtil;

@Service
public class YanjiUtil {

	@Value("${sysName}")
	private String sysName;
	@Autowired
	private RestUtil restUtil;
	
	private static final String YANJI_BASE_URL = "https://tg.shenghuocang.com.cn/wechat/hexie/wechat";
//	private static final String YANJI_BASE_URL = "http://localhost:89";
	private static final String SUBSCRIBE_EVENT_URL = "/yanji/envent/subscribe";
	
	/**
	 * 通知延吉公众号关注时间
	 * @param baseEventDTO
	 * @return
	 * @throws Exception
	 */
	public String subsribeEventRequest(String requestJson) throws Exception {
		
		String requestUrl = YANJI_BASE_URL + SUBSCRIBE_EVENT_URL;
		TypeReference<String> typeReference = new TypeReference<String>(){};
		String response = restUtil.exchangeOnBody(requestUrl, requestJson, typeReference);
		return response;
		
	}
}

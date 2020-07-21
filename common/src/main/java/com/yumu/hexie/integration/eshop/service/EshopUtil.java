package com.yumu.hexie.integration.eshop.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yumu.hexie.integration.common.CommonPayRequest;
import com.yumu.hexie.integration.common.CommonPayResponse;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.RequestUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.common.ServiceOrderRequest;
import com.yumu.hexie.integration.eshop.req.NotifyConsumeRequest;
import com.yumu.hexie.model.user.User;

@Component
public class EshopUtil {

	private static final String REQUEST_PAY_URL = "/eshop/requestPaySDO.do";
	private static final String CANCEL_PAY_URL = "/eshop/cancelPaySDO.do";
	private static final String NOTIFY_CONSUME_URL = "/eshop/notifyConsumeSDO.do";
	
	@Autowired
	private RequestUtil requestUtil;
	@Autowired
	private RestUtil restUtil;
	
	public CommonPayResponse requestPay(User user, CommonPayRequest request) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += REQUEST_PAY_URL;
		
		TypeReference<CommonResponse<CommonPayResponse>> typeReference = new TypeReference<CommonResponse<CommonPayResponse>>(){};
		CommonResponse<CommonPayResponse> commonResponse = restUtil.exchange(requestUrl, request, typeReference);
		return commonResponse.getData();
	}
	
	/**
	 * 支付订单取消
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	public void cancelPay(User user, String tradeWaterId) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += CANCEL_PAY_URL;
		
		ServiceOrderRequest serviceOrderRequest = new ServiceOrderRequest();
		serviceOrderRequest.setTradeWaterId(tradeWaterId);
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		restUtil.exchange(requestUrl, serviceOrderRequest, typeReference);
		
	}
	
	/**
	 * 通知核销
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public void notifyConsume(User user, String orderNo, String evouchers) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += NOTIFY_CONSUME_URL;
		
		NotifyConsumeRequest notifyConsumeRequest = new NotifyConsumeRequest();
		notifyConsumeRequest.setTradeWaterId(orderNo);
		notifyConsumeRequest.setEvouchers(evouchers);
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		restUtil.exchange(requestUrl, notifyConsumeRequest, typeReference);
		
	}
	
}

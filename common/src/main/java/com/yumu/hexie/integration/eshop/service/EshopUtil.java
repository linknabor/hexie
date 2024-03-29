package com.yumu.hexie.integration.eshop.service;

import java.io.IOException;
import java.util.List;

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
import com.yumu.hexie.integration.eshop.req.CreateRgroupRequest;
import com.yumu.hexie.integration.eshop.req.EshopServiceRequest;
import com.yumu.hexie.integration.eshop.req.NotifyConsumeRequest;
import com.yumu.hexie.integration.eshop.req.QueryRgroupSect;
import com.yumu.hexie.integration.eshop.resp.RgroupRegionsVO;
import com.yumu.hexie.model.user.User;

@Component
public class EshopUtil {
	
	private static final String REQUEST_PAY_URL = "/eshop/requestPaySDO.do";		//商品支付
	private static final String CANCEL_PAY_URL = "/eshop/cancelPaySDO.do";			//取消支付
	private static final String NOTIFY_CONSUME_URL = "/eshop/notifyConsumeSDO.do";	//通知卡券消费
	private static final String REQUEST_REFUND_URL = "/eshop/doRefundSDO.do";	//退款
	private static final String RESET_PASSWORD_URL = "resetPwdSDO.do";	//重置密码
	private static final String QUERY_SECT_URL = "/eshop/getSectInfoSDO.do";		//查询小区信息
	private static final String CREATE_RGROUP_URL = "/eshop/createRgroupSDO.do";		//开团创建团长和小区信息

	@Autowired
	private RequestUtil requestUtil;
	@Autowired
	private RestUtil restUtil;
	
	public CommonPayResponse requestPay(User user, CommonPayRequest request) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += REQUEST_PAY_URL;
		
		TypeReference<CommonResponse<CommonPayResponse>> typeReference = new TypeReference<CommonResponse<CommonPayResponse>>(){};
		CommonResponse<CommonPayResponse> commonResponse = restUtil.exchangeOnUri(requestUrl, request, typeReference);
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
		restUtil.exchangeOnUri(requestUrl, serviceOrderRequest, typeReference);
		
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
		restUtil.exchangeOnUri(requestUrl, notifyConsumeRequest, typeReference);
		
	}
	
	/**
	 * 申请退款
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public void requestRefund(User user, String orderId) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += REQUEST_REFUND_URL;
		
		ServiceOrderRequest serviceOrderRequest = new ServiceOrderRequest();
		serviceOrderRequest.setTradeWaterId(orderId);
		
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		restUtil.exchangeOnUri(requestUrl, serviceOrderRequest, typeReference);
	}
	
	/**
	 * 申请退款
	 * @param user
	 * @param serviceOrderRequest
	 * @return
	 * @throws Exception
	 */
	public void requestPartRefund(User user, ServiceOrderRequest serviceOrderRequest) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += REQUEST_REFUND_URL;
		
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		restUtil.exchangeOnUri(requestUrl, serviceOrderRequest, typeReference);
	}
	
	
	/**
	 * 申请退款
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public String resetPassword(User user) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += RESET_PASSWORD_URL;
		
		EshopServiceRequest request = new EshopServiceRequest();
		request.setTel(user.getTel());
		
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		CommonResponse<String> commonResponse = restUtil.exchangeOnUri(requestUrl, request, typeReference);
		return commonResponse.getData();
	}
	
	/**
	 * 获取团购小区信息
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public List<RgroupRegionsVO> querySectInfo(User user, String sectIds) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_SECT_URL;
		
		QueryRgroupSect queryRgroupSect = new QueryRgroupSect();
		queryRgroupSect.setSectIds(sectIds);
		
		TypeReference<CommonResponse<List<RgroupRegionsVO>>> typeReference = new TypeReference<CommonResponse<List<RgroupRegionsVO>>>(){};
		CommonResponse<List<RgroupRegionsVO>> commonResponse = restUtil.exchangeOnUri(requestUrl, queryRgroupSect, typeReference);
		return commonResponse.getData();
	}
	
	/**
	 * 新建团购->创建团长和小区地址
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public CreateRgroupRequest createRgroup(String regionName, CreateRgroupRequest request) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(new User(), regionName);
		requestUrl += CREATE_RGROUP_URL;
		
		TypeReference<CommonResponse<CreateRgroupRequest>> typeReference = new TypeReference<CommonResponse<CreateRgroupRequest>>(){};
		CommonResponse<CreateRgroupRequest> commonResponse = restUtil.exchangeOnBody(requestUrl, request, typeReference);
		return commonResponse.getData();
	}
	
}

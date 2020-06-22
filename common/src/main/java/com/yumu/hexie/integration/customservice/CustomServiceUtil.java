package com.yumu.hexie.integration.customservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.RequestUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.customservice.dto.CustomerServiceOrderDTO;
import com.yumu.hexie.integration.customservice.req.CreateOrderRequest;
import com.yumu.hexie.integration.customservice.req.GetServiceRequest;
import com.yumu.hexie.integration.customservice.req.OperOrderRequest;
import com.yumu.hexie.integration.customservice.req.ServiceOrderRequest;
import com.yumu.hexie.integration.customservice.resp.CreateOrderResponseVO;
import com.yumu.hexie.integration.customservice.resp.CustomServiceVO;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.model.user.User;

@Component
public class CustomServiceUtil {

	@Autowired
	private RequestUtil requestUtil;
	
	
	@Autowired
	private RestUtil restUtil;
	
	private static final String GET_SERVICE_URL = "getCustomServiceSDO.do"; //获取自定义服务
	private static final String CREATE_ORDER_URL = "createCustomServiceSDO.do"; //订单创建
	private static final String CONFIRM_ORDER_URL = "setCustomReceiverSDO.do"; //确认订单
	private static final String CANCEL_PAY_URL = "cancelCustomOrderSDO.do"; //支付取消
	
	public BaseResult<List<CustomServiceVO>> getCustomService(User user) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += GET_SERVICE_URL;
		
		GetServiceRequest getServiceRequest = new GetServiceRequest();
		getServiceRequest.setSectId(user.getSectId());
		getServiceRequest.setUserId(user.getWuyeId());
		
		TypeReference<CommonResponse<List<CustomServiceVO>>> typeReference = new TypeReference<CommonResponse<List<CustomServiceVO>>>(){};
		CommonResponse<List<CustomServiceVO>> commonResponse = restUtil.exchange(requestUrl, getServiceRequest, typeReference);
		BaseResult<List<CustomServiceVO>> baseResult = new BaseResult<>();
		baseResult.setData(commonResponse.getData());
		return baseResult;
		
	}
	
	/**
	 * 服务订单创建
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	public BaseResult<CreateOrderResponseVO> createOrder(CustomerServiceOrderDTO dto) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(dto.getUser(), "");
		requestUrl += CREATE_ORDER_URL;
		
		CreateOrderRequest createOrderRequest = new CreateOrderRequest(dto);
		TypeReference<CommonResponse<CreateOrderResponseVO>> typeReference = new TypeReference<CommonResponse<CreateOrderResponseVO>>(){};
		CommonResponse<CreateOrderResponseVO> commonResponse = restUtil.exchange(requestUrl, createOrderRequest, typeReference);
		BaseResult<CreateOrderResponseVO> baseResult = new BaseResult<>();
		baseResult.setData(commonResponse.getData());
		return baseResult;
		
	}
	
	/**
	 * 服务订单创建
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	public void operatorOrder(User user, OperOrderRequest operOrderRequest) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += CONFIRM_ORDER_URL;
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		restUtil.exchange(requestUrl, operOrderRequest, typeReference);
		
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
	
}

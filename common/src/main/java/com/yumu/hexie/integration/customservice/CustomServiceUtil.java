package com.yumu.hexie.integration.customservice;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.RequestUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.customservice.dto.CustomerServiceOrderDTO;
import com.yumu.hexie.integration.customservice.req.CreateOrderRequest;
import com.yumu.hexie.integration.customservice.req.GetServiceRequest;
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
	
	private static final String GET_SERVICE_URL = "getCustomServiceSDO.do"; // 快捷支付
	private static final String CREATE_ORDER_URL = "createCustomServiceSDO.do"; // 微信支付请求
	
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
		
		CreateOrderRequest createOrderRequest = new CreateOrderRequest();
		BeanUtils.copyProperties(dto, createOrderRequest);
		TypeReference<CommonResponse<CreateOrderResponseVO>> typeReference = new TypeReference<CommonResponse<CreateOrderResponseVO>>(){};
		CommonResponse<CreateOrderResponseVO> commonResponse = restUtil.exchange(requestUrl, createOrderRequest, typeReference);
		BaseResult<CreateOrderResponseVO> baseResult = new BaseResult<>();
		baseResult.setData(commonResponse.getData());
		return baseResult;
		
	}
	
}

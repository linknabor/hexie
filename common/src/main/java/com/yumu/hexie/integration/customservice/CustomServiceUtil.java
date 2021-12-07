package com.yumu.hexie.integration.customservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.integration.common.CommonPayRequest;
import com.yumu.hexie.integration.common.CommonPayResponse;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.RequestUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.common.ServiceOrderRequest;
import com.yumu.hexie.integration.customservice.dto.CustomerServiceOrderDTO;
import com.yumu.hexie.integration.customservice.dto.OrderQueryDTO;
import com.yumu.hexie.integration.customservice.req.GetServiceRequest;
import com.yumu.hexie.integration.customservice.req.OperOrderRequest;
import com.yumu.hexie.integration.customservice.resp.CustomServiceVO;
import com.yumu.hexie.integration.customservice.resp.ServiceOrderQueryVO;
import com.yumu.hexie.model.user.User;

@Service
public class CustomServiceUtil {

	@Autowired
	private RequestUtil requestUtil;
	
	
	@Autowired
	private RestUtil restUtil;
	
	private static final String GET_SERVICE_URL = "getCustomServiceSDO.do"; //获取自定义服务
	private static final String CREATE_ORDER_URL = "createCustomServiceSDO.do"; //订单创建
	private static final String CONFIRM_ORDER_URL = "setCustomReceiverSDO.do"; //确认订单
	private static final String CANCEL_PAY_URL = "cancelCustomOrderSDO.do"; //支付取消
	private static final String QUERY_ORDER_URL = "queryServiceOrderSDO.do";//订单查询 
	
	public List<CustomServiceVO> getCustomService(User user) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += GET_SERVICE_URL;
		
		GetServiceRequest getServiceRequest = new GetServiceRequest();
		String sectId = user.getSectId();
		if ("0".equals(sectId)) {
			sectId = "";
		}
		getServiceRequest.setSectId(sectId);
		getServiceRequest.setUserId(user.getWuyeId());
		
		TypeReference<CommonResponse<List<CustomServiceVO>>> typeReference = new TypeReference<CommonResponse<List<CustomServiceVO>>>(){};
		CommonResponse<List<CustomServiceVO>> commonResponse = restUtil.exchangeOnUri(requestUrl, getServiceRequest, typeReference);
		return commonResponse.getData();
		
	}
	
	/**
	 * 服务订单创建
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	public CommonPayResponse createOrder(CustomerServiceOrderDTO dto) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(dto.getUser(), "");
		requestUrl += CREATE_ORDER_URL;
		
		CommonPayRequest createOrderRequest = new CommonPayRequest(dto);
		TypeReference<CommonResponse<CommonPayResponse>> typeReference = new TypeReference<CommonResponse<CommonPayResponse>>(){};
		CommonResponse<CommonPayResponse> commonResponse = restUtil.exchangeOnUri(requestUrl, createOrderRequest, typeReference);
		return commonResponse.getData();
		
	}
	
	/**
	 * 服务订单创建
	 * @param user
	 * @param operOrderRequest
	 * @return
	 * @throws Exception
	 */
	public void operatorOrder(User user, OperOrderRequest operOrderRequest) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += CONFIRM_ORDER_URL;
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		restUtil.exchangeOnUri(requestUrl, operOrderRequest, typeReference);
		
	}
	
	/**
	 * 支付订单取消
	 * @param user
	 * @param tradeWaterId
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
	 * 二维码 服务订单查询
	 * @param orderQueryDTO
	 * @return
	 * @throws Exception
	 */
	public ServiceOrderQueryVO queryOrder(OrderQueryDTO orderQueryDTO) throws Exception {
		
		User user = orderQueryDTO.getUser();
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_ORDER_URL;
		
		ServiceOrderRequest serviceOrderRequest = new ServiceOrderRequest();
		serviceOrderRequest.setSectId(orderQueryDTO.getSectId());
		serviceOrderRequest.setFeeId(orderQueryDTO.getFeeId());
		serviceOrderRequest.setTotalCount(orderQueryDTO.getTotalCount());
		serviceOrderRequest.setCurrentPage(orderQueryDTO.getCurrentPage());
		
		TypeReference<CommonResponse<ServiceOrderQueryVO>> typeReference = new TypeReference<CommonResponse<ServiceOrderQueryVO>>(){};
		CommonResponse<ServiceOrderQueryVO> commonResponse = restUtil.exchangeOnUri(requestUrl, serviceOrderRequest, typeReference);
		return commonResponse.getData();
	}
	
	
}

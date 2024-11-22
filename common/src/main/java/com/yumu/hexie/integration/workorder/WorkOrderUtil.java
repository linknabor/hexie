package com.yumu.hexie.integration.workorder;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.RequestUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.workorder.req.QueryOrderRequest;
import com.yumu.hexie.integration.workorder.req.QueryUserOrderRequest;
import com.yumu.hexie.integration.workorder.req.ReverseOrderRequest;
import com.yumu.hexie.integration.workorder.req.SaveWorkOrderRequest;
import com.yumu.hexie.integration.workorder.resp.OrderDetailVO;
import com.yumu.hexie.integration.workorder.resp.WorkOrderServiceVO;
import com.yumu.hexie.integration.workorder.resp.WorkOrdersVO;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.common.impl.SystemConfigServiceImpl;
import com.yumu.hexie.service.common.pojo.dto.ActiveApp;
import com.yumu.hexie.service.workorder.req.WorkOrderReq;

@Service
public class WorkOrderUtil {
	
	@Value("${sysName}")
	private String sysName;
	@Autowired
	private RestUtil restUtil;
	@Autowired
	private RequestUtil requestUtil;
	@Autowired
	private SystemConfigService systemConfigService;
	
	
	private static final String ORDER_DETAIL_URL = "workorder/getOrderDetailSDO.do";
	private static final String ADD_WORKORDER_URL = "workorder/addOrderSDO.do";//合协社区物业缴费的小区级联 模糊查询小区
	private static final String QUERY_WORKORDER_URL = "workorder/queryOrderSDO.do";//查询工单列表
	private static final String REVERSE_WORKORDER_URL = "workorder/reverseOrderSDO.do";//工单撤消
	private static final String QUERY_SERVICE_URL = "workorder/queryServiceSDO.do";
	
	/**
	 * 标准版查询账单
	 * @param workOrderReq
	 * @return
	 * @throws Exception 
	 */
	public CommonResponse<String> addWorkOrder(User user, WorkOrderReq workOrderReq) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += ADD_WORKORDER_URL;
		
		SaveWorkOrderRequest request = new SaveWorkOrderRequest(workOrderReq);
		String name = user.getRealName();
		if (StringUtils.isEmpty(name)) {
			name = user.getName();
		}
		if (StringUtils.isEmpty(name)) {
			name = user.getTel();
		}
		ActiveApp activeApp = systemConfigService.getActiveApp(user);
		request.setCreator(name);
		request.setCreatorAppid(activeApp.getActiveAppid());
		request.setCreatorContact(user.getTel());
		request.setCreatorOpenid(activeApp.getActiveOpenid());
		request.setCreatorUserId(String.valueOf(user.getId()));
		request.setWuyeId(user.getWuyeId());
		String sectId = workOrderReq.getSectId();
		if (StringUtils.isEmpty(sectId)) {
			sectId = user.getSectId();
		}
		request.setSectId(sectId);
		request.setCspId(user.getCspId());
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		CommonResponse<String> hexieResponse = restUtil.exchangeOnBody(requestUrl, request, typeReference);
		return hexieResponse;
		
	}
	
	/**
	 * 标准版查询账单
	 * @param user
	 * @return
	 * @throws Exception 
	 */
	public CommonResponse<WorkOrdersVO> queryWorkOrder(User user) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_WORKORDER_URL;
		
		QueryUserOrderRequest request = new QueryUserOrderRequest();
		request.setCurrPage("0");
		request.setQueryUserId(String.valueOf(user.getId()));
		
		TypeReference<CommonResponse<WorkOrdersVO>> typeReference = new TypeReference<CommonResponse<WorkOrdersVO>>(){};
		CommonResponse<WorkOrdersVO> hexieResponse = restUtil.exchangeOnUri(requestUrl, request, typeReference);
		return hexieResponse;
		
	}
	
	public CommonResponse<OrderDetailVO> getOrderDetail(User user, String orderId) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += ORDER_DETAIL_URL;
		
		QueryOrderRequest request = new QueryOrderRequest();
		request.setOrderId(orderId);

		TypeReference<CommonResponse<OrderDetailVO>> typeReference = new TypeReference<CommonResponse<OrderDetailVO>>(){};
		CommonResponse<OrderDetailVO> hexieResponse = restUtil.exchangeOnUri(requestUrl, request, typeReference);
		return hexieResponse;
		
	}
	
	public CommonResponse<String> reverseOrder(User user, String orderId, String reason) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, null);
		requestUrl += REVERSE_WORKORDER_URL;
		
		ReverseOrderRequest request = new ReverseOrderRequest();
		request.setOrderId(orderId);
		request.setReason(reason);
		request.setOperOpenid(user.getOpenid());
		
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		CommonResponse<String> hexieResponse = restUtil.exchangeOnBody(requestUrl, request, typeReference);
		return hexieResponse;
		
	}
	
	/**
	 * 获取工单服务
	 * 1)如果在接单时间内，则返回房屋地址
	 * 2)如果超出接单时间，则返回报修电话
	 * @param user
	 * @param sectId
	 * @return
	 * @throws Exception 
	 */
	public CommonResponse<WorkOrderServiceVO> getService(User user, String sectId) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, null);
		requestUrl += QUERY_SERVICE_URL;
		
		String userSysCode = SystemConfigServiceImpl.getSysMap().get(user.getAppId());	//获取用户所属的公众号
		String city_db = userSysCode;
		if (!"_guizhou".equals(userSysCode) && !"_hebei".equals(userSysCode)) {
			city_db = "_sh";
		}
		Map<String, String> request = new HashMap<>();
		request.put("user_id", user.getWuyeId());
		request.put("sect_id", StringUtils.isEmpty(sectId)?user.getSectId():sectId);
		request.put("city_db", city_db);
		
		TypeReference<CommonResponse<WorkOrderServiceVO>> typeReference = new TypeReference<CommonResponse<WorkOrderServiceVO>>(){};
		CommonResponse<WorkOrderServiceVO> hexieResponse = restUtil.exchangeOnUri(requestUrl, request, typeReference);
		return hexieResponse;
	}
}

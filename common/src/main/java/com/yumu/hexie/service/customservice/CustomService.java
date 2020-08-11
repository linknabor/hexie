package com.yumu.hexie.service.customservice;

import java.util.List;

import com.yumu.hexie.integration.common.CommonPayResponse;
import com.yumu.hexie.integration.customservice.dto.CustomerServiceOrderDTO;
import com.yumu.hexie.integration.customservice.dto.OperatorDTO;
import com.yumu.hexie.integration.customservice.dto.OrderQueryDTO;
import com.yumu.hexie.integration.customservice.dto.ServiceCfgDTO;
import com.yumu.hexie.integration.customservice.dto.ServiceCommentDTO;
import com.yumu.hexie.integration.customservice.resp.CustomServiceVO;
import com.yumu.hexie.integration.customservice.resp.ServiceOrderPrepayVO;
import com.yumu.hexie.integration.customservice.resp.ServiceOrderQueryVO;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.user.User;

public interface CustomService {
	
	List<CustomServiceVO> getService(User user) throws Exception;

	CommonPayResponse createOrder(CustomerServiceOrderDTO customerServiceOrderDTO) throws Exception;
	
	void confirmOrder(User user, String orderId, String operType) throws Exception;
	
	ServiceOrder queryOrder(User user, String orderId);
	
	List<ServiceOrder> queryOrderByStatus(User user, String status, String serviceId);
	
	void acceptOrder(User user, String orderId) throws Exception;

	void reverseOrder(User user, String orderId) throws Exception;

	void notifyPay(User user, String orderId) throws Exception;

	List<ServiceOrder> queryOrderByUser(User user);

	ServiceOrderPrepayVO orderPay(User user, String orderId, String amount) throws Exception;
	
	void comment(ServiceCommentDTO serviceCommentDTO);

	void cancelPay(User user, String orderId) throws Exception;

	void operator(OperatorDTO operatorDTO);

	void updateServiceCfg(ServiceCfgDTO serviceCfgDTO);

	void assginOrder(CommonPayResponse data);

	void saveServiceImages(String appId, long orderId, List<String> imgUrls);

	void saveCommentImages(String appId, long orderId, List<String> imgUrls);

	ServiceOrderQueryVO queryOrderByFeeType(OrderQueryDTO orderQueryDTO) throws Exception;


}

package com.yumu.hexie.service.customservice;

import java.util.List;

import com.yumu.hexie.integration.customservice.dto.CustomerServiceOrderDTO;
import com.yumu.hexie.integration.customservice.resp.CreateOrderResponseVO;
import com.yumu.hexie.integration.customservice.resp.CustomServiceVO;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.user.User;

public interface CustomService {
	
	List<CustomServiceVO> getService(User user) throws Exception;

	CreateOrderResponseVO createOrder(CustomerServiceOrderDTO customerServiceOrderDTO) throws Exception;
	
	void confirmOrder(User user, String orderId, String operType) throws Exception;
	
	ServiceOrder queryOrder(User user, String orderId);
	
	List<ServiceOrder> queryOrderByStatus(User user, String status);
	
	void acceptOrder(User user, String orderId) throws Exception;

	void reverseOrder(User user, String orderId) throws Exception;

	void notifyPay(User user, String orderId) throws Exception;

	void notifyPayByServplat(String tradeWaterId);

	List<ServiceOrder> queryOrderByUser(User user);

	CreateOrderResponseVO orderPay(User user, String orderId, String amount) throws Exception;
}

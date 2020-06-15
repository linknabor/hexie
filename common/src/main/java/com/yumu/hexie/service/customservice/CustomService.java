package com.yumu.hexie.service.customservice;

import java.util.List;

import com.yumu.hexie.integration.customservice.dto.CustomerServiceOrderDTO;
import com.yumu.hexie.integration.customservice.resp.CreateOrderResponseVO;
import com.yumu.hexie.integration.customservice.resp.CustomServiceVO;
import com.yumu.hexie.model.user.User;

public interface CustomService {
	
	List<CustomServiceVO> getService(User user) throws Exception;

	CreateOrderResponseVO createOrder(CustomerServiceOrderDTO customerServiceOrderDTO) throws Exception;
}

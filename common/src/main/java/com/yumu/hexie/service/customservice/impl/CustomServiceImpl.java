package com.yumu.hexie.service.customservice.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.integration.customservice.CustomServiceUtil;
import com.yumu.hexie.integration.customservice.dto.CustomerServiceOrderDTO;
import com.yumu.hexie.integration.customservice.req.OperOrderRequest;
import com.yumu.hexie.integration.customservice.resp.CreateOrderResponseVO;
import com.yumu.hexie.integration.customservice.resp.CustomServiceVO;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.customservice.CustomService;
import com.yumu.hexie.service.exception.BizValidateException;

@Service
public class CustomServiceImpl implements CustomService {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CustomServiceUtil customServiceUtil;
	@Autowired
	private ServiceOrderRepository serviceOrderRepository;
	@Autowired
	private RegionRepository regionRepository;
	
	
	@Override
	public List<CustomServiceVO> getService(User user) throws Exception {
		return customServiceUtil.getCustomService(user).getData();
	}

	@Transactional
	@Override
	public CreateOrderResponseVO createOrder(CustomerServiceOrderDTO customerServiceOrderDTO) throws Exception {
		
		CreateOrderResponseVO data = customServiceUtil.createOrder(customerServiceOrderDTO).getData();
		
		User currUser = userRepository.findById(customerServiceOrderDTO.getUser().getId());
		ServiceOrder serviceOrder = new ServiceOrder();
		serviceOrder.setOrderType(ModelConstant.ORDER_TYPE_SERVICE);
		serviceOrder.setProductId(Long.valueOf(customerServiceOrderDTO.getServiceId()));
		serviceOrder.setUserId(currUser.getId());
		serviceOrder.setPrice(Float.valueOf(customerServiceOrderDTO.getTranAmt()));
		serviceOrder.setCount(1);
		serviceOrder.setStatus(ModelConstant.ORDER_STATUS_INIT);
		serviceOrder.setPingjiaStatus(ModelConstant.ORDER_PINGJIA_TYPE_N);
		serviceOrder.setOpenId(currUser.getOpenid());
		serviceOrder.setAddress(customerServiceOrderDTO.getServiceAddr());
		serviceOrder.setTel(customerServiceOrderDTO.getLinktel());
		serviceOrder.setReceiverName(customerServiceOrderDTO.getLinkman());
		serviceOrder.setProductName(customerServiceOrderDTO.getServiceName());
		serviceOrder.setProductPic(customerServiceOrderDTO.getImage());
		serviceOrder.setOrderNo(data.getTradeWaterId());
		serviceOrder.setAppid(currUser.getAppId());
		List<Region> regionList = regionRepository.findAllBySectId(currUser.getSectId());
		if (regionList!=null && !regionList.isEmpty()) {
			serviceOrder.setXiaoquId(regionList.get(0).getId());
		}
		serviceOrderRepository.save(serviceOrder);
		data.setOrderId(String.valueOf(serviceOrder.getId()));
		return data;
		
	}

	/**
	 * 确认订单
	 * @throws Exception 
	 */
	@Override
	@Transactional
	public void confirmOrder(User user, String orderId, String operType) throws Exception {
		
		Assert.hasText(orderId, "订单ID不能为空。");
		ServiceOrder serviceOrder = serviceOrderRepository.findOne(Long.valueOf(orderId));
		if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
			throw new BizValidateException("未查询到订单, orderId : " + orderId);
		}

		Date date = new Date();
		OperOrderRequest operOrderRequest = new OperOrderRequest();
		operOrderRequest.setOperDate(DateUtil.dtFormat(date, "yyyyMMddHHmmss"));
		operOrderRequest.setOpenid(user.getOpenid());
		operOrderRequest.setTradeWaterId(serviceOrder.getOrderNo());
		operOrderRequest.setOperType("1");
		customServiceUtil.operatorOrder(user, operOrderRequest);
		
		if (serviceOrder.getUserId()!=user.getId() && serviceOrder.getOperatorUserId() != user.getId()) {
			throw new BizValidateException("当前用户无法查看此订单。orderId : " + orderId + ", userId : " + user.getId());
		}
		
		serviceOrder.setConfirmDate(date);
		serviceOrder.setConfirmer(user.getName());
		serviceOrder.setStatus(ModelConstant.ORDER_STATUS_CONFIRM);
		serviceOrderRepository.save(serviceOrder);
		
	}
	
	/**
	 * 订单查询
	 */
	@Override
	public ServiceOrder queryOrder(User user, String orderId) {
		
		Assert.hasText(orderId, "订单ID不能为空。");
		ServiceOrder serviceOrder = serviceOrderRepository.findOne(Long.valueOf(orderId));
		if (serviceOrder!=null) {
			if (serviceOrder.getUserId()!=user.getId() && serviceOrder.getOperatorUserId() != user.getId()) {
				throw new BizValidateException("当前用户无法查看此订单。orderId : " + orderId + ", userId : " + user.getId());
			}
		}
		return serviceOrder;
	}

	/**
	 * 接单
	 * @throws Exception 
	 */
	@Override
	@Transactional
	public void acceptOrder(User user, String orderId) throws Exception {
		
		//TODO redis抢单
		Assert.hasText(orderId, "订单ID不能为空。");
		ServiceOrder serviceOrder = serviceOrderRepository.findOne(Long.valueOf(orderId));
		if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
			throw new BizValidateException("未查询到订单, orderId : " + orderId);
		}
		
		Date date = new Date();
		OperOrderRequest operOrderRequest = new OperOrderRequest();
		operOrderRequest.setOperDate(DateUtil.dtFormat(date, "yyyyMMddHHmmss"));
		operOrderRequest.setOpenid(user.getOpenid());
		operOrderRequest.setTradeWaterId(serviceOrder.getOrderNo());
		operOrderRequest.setOperType("0");
		customServiceUtil.operatorOrder(user, operOrderRequest);
		
		serviceOrder.setOperatorName(user.getName());
		serviceOrder.setOperatorOpenId(user.getOpenid());
		serviceOrder.setOperatorTel(user.getTel());
		serviceOrder.setOperatorUserId(user.getId());
		serviceOrder.setAcceptedDate(date);
		serviceOrder.setStatus(ModelConstant.ORDER_STATUS_ACCEPTED);
		serviceOrderRepository.save(serviceOrder);
		
	}

	/**
	 * 根据状态查询订单
	 */
	@Override
	public List<ServiceOrder> queryOrderByStatus(User user, String status) {

		Assert.notNull(user, "用户信息不能为空。");
		Assert.hasText(status, "订单状态不能为空");
		
		List<Integer> statusList = new ArrayList<>();
		statusList.add(Integer.valueOf(status));
		
		List<ServiceOrder> orderList = serviceOrderRepository.findByOperAndStatusAndOrderType(user.getId(), statusList, ModelConstant.ORDER_TYPE_SERVICE);
		return orderList;
	}

}

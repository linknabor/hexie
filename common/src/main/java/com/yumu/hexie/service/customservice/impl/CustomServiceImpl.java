package com.yumu.hexie.service.customservice.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.integration.customservice.CustomServiceUtil;
import com.yumu.hexie.integration.customservice.dto.CustomerServiceOrderDTO;
import com.yumu.hexie.integration.customservice.dto.ServiceCommentDTO;
import com.yumu.hexie.integration.customservice.req.OperOrderRequest;
import com.yumu.hexie.integration.customservice.resp.CreateOrderResponseVO;
import com.yumu.hexie.integration.customservice.resp.CustomServiceVO;
import com.yumu.hexie.integration.notify.PayNotifyDTO.ServiceNotification;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.customservice.CustomService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.notify.NotifyService;

@Service
public class CustomServiceImpl implements CustomService {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CustomServiceUtil customServiceUtil;
	@Autowired
	private ServiceOrderRepository serviceOrderRepository;
	@Autowired
	private NotifyService notifyService;
	@Autowired
	private RegionRepository regionRepository;
	
	
	@Override
	public List<CustomServiceVO> getService(User user) throws Exception {
		
		User currUser = userRepository.findOne(user.getId());
		return customServiceUtil.getCustomService(currUser).getData();
	}

	/**
	 * 创建订单交易
	 */
	@Transactional
	@Override
	public CreateOrderResponseVO createOrder(CustomerServiceOrderDTO customerServiceOrderDTO) throws Exception {
		
		//1.调用API创建接口
		CreateOrderResponseVO data = customServiceUtil.createOrder(customerServiceOrderDTO).getData();
		
		//2.保存本地订单
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
		serviceOrder.setMemo(customerServiceOrderDTO.getMemo());
		serviceOrder.setXiaoquName(customerServiceOrderDTO.getSectName());
		String xiaoquId = customerServiceOrderDTO.getSectId();
		List<Region> regionList = regionRepository.findAllBySectId(xiaoquId);
		if (regionList != null && !regionList.isEmpty()) {
			Region region = regionList.get(0);
			serviceOrder.setXiaoquId(region.getId());
		}
		serviceOrder = serviceOrderRepository.save(serviceOrder);
		
		//3.如果是非一口价的订单，需要分发抢单的信息给操作员,异步
		ServiceNotification serviceNotification = data.getServiceNotification();
		if (serviceNotification != null) {
			serviceNotification.setOrderId(String.valueOf(serviceOrder.getId()));
			notifyService.sendServiceNotificationAsync(data.getServiceNotification());
		}
		//单列字段，前端需要。这里就不单独弄一个VO了
		data.setOrderId(String.valueOf(serviceOrder.getId()));
		return data;
		
	}
	
	/**
	 * 非一口价支付
	 */
	@Override
	public CreateOrderResponseVO orderPay(User user, String orderId, String amount) throws Exception {
		
		Assert.hasText(orderId, "订单ID不能为空。");
		
		ServiceOrder serviceOrder = serviceOrderRepository.findOne(Long.valueOf(orderId));
		if (serviceOrder == null) {
			throw new BizValidateException("未查询到订单，orderId: " + orderId);
		}
		
		//1.调用API创建接口
		CustomerServiceOrderDTO dto = new CustomerServiceOrderDTO();
		dto.setLinkman(serviceOrder.getReceiverName());
		dto.setLinktel(serviceOrder.getTel());
		Region region = regionRepository.findOne(serviceOrder.getXiaoquId());
		if (region == null) {
			throw new BizValidateException("为查询到小区, region id : " + serviceOrder.getXiaoquId());
		}
		dto.setSectId(String.valueOf(region.getSectId()));
		dto.setServiceAddr(serviceOrder.getAddress());
		dto.setServiceId(String.valueOf(serviceOrder.getProductId()));
		dto.setTradeWaterId(serviceOrder.getOrderNo());
		dto.setTranAmt(amount);
		dto.setUser(user);
		CreateOrderResponseVO data = customServiceUtil.createOrder(dto).getData();
		
		//单列字段，前端需要。这里就不单独弄一个VO了
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
	 * @param status 0可接单，9确认完工，15已接单
	 */
	@Override
	public List<ServiceOrder> queryOrderByStatus(User user, String status) {

		Assert.notNull(user, "用户信息不能为空。");
		Assert.hasText(status, "订单状态不能为空");

		
		List<Integer> statusList = new ArrayList<>();
		statusList.add(Integer.valueOf(status));
		List<ServiceOrder> orderList  = null;
		if ("0".equals(status)) {
			orderList = serviceOrderRepository.findByOrderStatusAndOrderType(statusList, ModelConstant.ORDER_TYPE_SERVICE);
		}else {
			orderList = serviceOrderRepository.findByOperAndStatusAndOrderType(user.getId(), statusList, ModelConstant.ORDER_TYPE_SERVICE);
		}
		return orderList;
	}
	
	/**
	 * 根据状态查询订单
	 * @param status 0可接单，9确认完工，15已接单
	 */
	@Override
	public List<ServiceOrder> queryOrderByUser(User user) {

		Assert.notNull(user, "用户信息不能为空。");

		List<Integer> orderTypes = new ArrayList<>();
		orderTypes.add(ModelConstant.ORDER_TYPE_SERVICE);
		List<ServiceOrder> orderList = serviceOrderRepository.findByUserIdAndOrderType(user.getId(), orderTypes);
		return orderList;
	}
	
	/**
	 * 用户撤销订单
	 * @throws Exception 
	 */
	@Override
	@Transactional
	public void reverseOrder(User user, String orderId) throws Exception {
		
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
		operOrderRequest.setOperType("9");
		customServiceUtil.operatorOrder(user, operOrderRequest);
		
		serviceOrder.setStatus(ModelConstant.ORDER_STATUS_CANCEL);
		serviceOrderRepository.save(serviceOrder);
		
	}
	
	/**
	 * 通知入账
	 * @throws Exception 
	 */
	@Override
	@Transactional
	public void notifyPay(User user, String orderId) throws Exception {
		
		Assert.hasText(orderId, "订单ID不能为空。");
		ServiceOrder serviceOrder = serviceOrderRepository.findOne(Long.valueOf(orderId));
		if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
			throw new BizValidateException("未查询到订单, orderId : " + orderId);
		}
		serviceOrder.setPayDate(new Date());
		serviceOrderRepository.save(serviceOrder);
		
	}
	
	/**
	 * 通知入账
	 * @throws Exception 
	 */
	@Override
	@Transactional
	public void notifyPayByServplat(String tradeWaterId) {
		
		if (StringUtils.isEmpty(tradeWaterId)) {
			return;
		}
		ServiceOrder serviceOrder = serviceOrderRepository.findByOrderNo(tradeWaterId);
		if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
			return;
		}
		serviceOrder.setPayDate(new Date());
		serviceOrderRepository.save(serviceOrder);
		
	}

	/**
	 * 服务订单评论
	 */
	@Transactional
	@Override
	public void comment(ServiceCommentDTO serviceCommentDTO) {
		
		Assert.hasText(serviceCommentDTO.getOrderId(), "订单ID不能为空。");
		Assert.hasText(serviceCommentDTO.getComment(), "平路内容不能为空。");
		
		ServiceOrder serviceOrder = serviceOrderRepository.findOne(Long.valueOf(serviceCommentDTO.getOrderId()));
		if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
			throw new BizValidateException("未查询到订单, orderId : " + serviceCommentDTO.getOrderId());
		}
		if (ModelConstant.ORDER_PINGJIA_TYPE_Y == serviceOrder.getStatus()) {
			throw new BizValidateException("订单已评价，订单ID：" + serviceCommentDTO.getOrderId());
		}
		serviceOrder.setComment(serviceCommentDTO.getComment());
		serviceOrder.setCommentAttitude(serviceCommentDTO.getCommentAttitude());
		serviceOrder.setCommentQuality(serviceCommentDTO.getCommentQuality());
		serviceOrder.setCommentService(serviceCommentDTO.getCommentService());
		serviceOrder.setCommentImgUrls(serviceCommentDTO.getCommentImgUrls());
		serviceOrder.setPingjiaStatus(ModelConstant.ORDER_PINGJIA_TYPE_Y);
		serviceOrder.setCommentDate(new Date());
		serviceOrderRepository.save(serviceOrder);
	}

}

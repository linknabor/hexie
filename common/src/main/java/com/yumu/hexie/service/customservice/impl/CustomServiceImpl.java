package com.yumu.hexie.service.customservice.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.RedisLock;
import com.yumu.hexie.integration.common.CommonPayResponse;
import com.yumu.hexie.integration.customservice.CustomServiceUtil;
import com.yumu.hexie.integration.customservice.dto.CustomerServiceOrderDTO;
import com.yumu.hexie.integration.customservice.dto.OperatorDTO;
import com.yumu.hexie.integration.customservice.dto.OrderQueryDTO;
import com.yumu.hexie.integration.customservice.dto.ServiceCfgDTO;
import com.yumu.hexie.integration.customservice.dto.ServiceCommentDTO;
import com.yumu.hexie.integration.customservice.req.OperOrderRequest;
import com.yumu.hexie.integration.customservice.resp.CustomServiceVO;
import com.yumu.hexie.integration.customservice.resp.ServiceOrderPrepayVO;
import com.yumu.hexie.integration.customservice.resp.ServiceOrderQueryVO;
import com.yumu.hexie.integration.notify.PayNotification.ServiceNotification;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.agent.Agent;
import com.yumu.hexie.model.agent.AgentRepository;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.model.localservice.HomeServiceConstant;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.promotion.PromotionConstant;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.UploadService;
import com.yumu.hexie.service.customservice.CustomService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.o2o.OperatorService;
import com.yumu.hexie.service.user.CouponService;

@Service
public class CustomServiceImpl implements CustomService {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomServiceImpl.class);
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CustomServiceUtil customServiceUtil;
	@Autowired
	private ServiceOrderRepository serviceOrderRepository;
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private GotongService gotongService;
	@Autowired
	private OperatorService operatorService;
	@Autowired
	private UploadService uploadService;
	@Autowired
	private AgentRepository agentRepository;
	@Autowired
	private CouponService couponService;
	
	@Override
	public List<CustomServiceVO> getService(User user) throws Exception {
		
		User currUser = userRepository.findById(user.getId());
		List<CustomServiceVO> list = customServiceUtil.getCustomService(currUser);
		
		//考虑把redis操作放在循环外，频繁操作有序列化和网络交互成本 TODO
//		list.stream().forEach(customServicevo->{
//			redisTemplate.opsForHash().put(ModelConstant.KEY_CUSTOM_SERVICE, customServicevo.getServiceId(), 
//					customServicevo.getServiceTitle());
//		});
		return list;
	}

	/**
	 * 创建订单交易
	 */
	@Transactional
	@Override
	public CommonPayResponse createOrder(CustomerServiceOrderDTO customerServiceOrderDTO) throws Exception {
		
		long begin = System.currentTimeMillis();
		
		//1.调用API创建接口
		User currUser = userRepository.findById(customerServiceOrderDTO.getUser().getId());
		customerServiceOrderDTO.setUser(currUser);
		customerServiceOrderDTO.setOrderType(String.valueOf(ModelConstant.ORDER_TYPE_SERVICE));
		
		Agent agent = null;
		if (!StringUtils.isEmpty(customerServiceOrderDTO.getAgentNo())) {
			agent = agentRepository.findByAgentNo(customerServiceOrderDTO.getAgentNo());
		}
		if (agent==null) {
			agent = new Agent();
			agent.setAgentNo(customerServiceOrderDTO.getAgentNo());
			agent.setName(customerServiceOrderDTO.getAgentName());
			agent.setStatus(1);
		}
		
		Coupon coupon = null;
		if (!StringUtils.isEmpty(customerServiceOrderDTO.getCouponId())) {
			coupon = couponService.findById(Long.valueOf(customerServiceOrderDTO.getCouponId()));
			
			Float amount = Float.valueOf(customerServiceOrderDTO.getTranAmt());
			Long serviceId = Long.valueOf(customerServiceOrderDTO.getServiceId());
			Product product = new Product();
			product.setId(serviceId);
			product.setService(true);
			product.setAgentId(agent.getId());
			couponService.checkAvailableV2(PromotionConstant.COUPON_ITEM_TYPE_SERVICE, product, amount, coupon, false);
			customerServiceOrderDTO.setCouponId(String.valueOf(coupon.getId()));
			customerServiceOrderDTO.setCouponAmt(String.valueOf(coupon.getAmount()));
			
			Float tranAmt = Float.valueOf(customerServiceOrderDTO.getTranAmt());
			Float couponAmt = coupon.getAmount();
			if (tranAmt > couponAmt ) {
				customerServiceOrderDTO.setTranAmt(String.valueOf(tranAmt-couponAmt));
			} else {
				customerServiceOrderDTO.setTranAmt("0.01");
			}
			
		}
		CommonPayResponse data = customServiceUtil.createOrder(customerServiceOrderDTO);
		long end = System.currentTimeMillis();
		logger.info("createOrderService location 1 : " + (end - begin)/1000);
		
		//2.保存本地订单
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
		serviceOrder.setSubType(Long.valueOf(customerServiceOrderDTO.getServiceId()));
		serviceOrder.setSubTypeName(customerServiceOrderDTO.getServiceName());
		
		if (agent!=null) {
			serviceOrder.setAgentId(agent.getId());
			serviceOrder.setAgentName(agent.getName());
			serviceOrder.setAgentNo(agent.getAgentNo());
		}
		serviceOrder.configCoupon(coupon);	//配置红包
		
		String xiaoquId = customerServiceOrderDTO.getSectId();
		String xiaoquName = customerServiceOrderDTO.getSectName();
		logger.info("createOrder, xiaoquId : " + xiaoquId);
		if (StringUtils.isEmpty(xiaoquId)) {
			String sectId = currUser.getSectId();
			List<Region> regionList = regionRepository.findAllBySectId(sectId);
			if (regionList!=null && !regionList.isEmpty()) {
				Region region = regionList.get(0);
				xiaoquId = String.valueOf(region.getId());
				xiaoquName = region.getName();
			}else {
				logger.warn("cannot find region, region sect id : " + sectId);
			}
		}
		if (!StringUtils.isEmpty(xiaoquId)) {
			serviceOrder.setXiaoquId(Long.valueOf(xiaoquId));
			serviceOrder.setXiaoquName(xiaoquName);
		}
		serviceOrder = serviceOrderRepository.save(serviceOrder);
		data.setOrderId(String.valueOf(serviceOrder.getId()));
		end = System.currentTimeMillis();
		logger.info("createOrderService location 2 : " + (end - begin)/1000);
		return data;
		
	}
	
	@Transactional
	@Override
	@Async("taskExecutor")
	public void saveServiceImages(String appId, long orderId, List<String>imgUrls) {
		
		long begin = System.currentTimeMillis();
		Map<String, String> uploaded = uploadService.uploadImages(appId, imgUrls);
		long end = System.currentTimeMillis();
		logger.info("upload time : " + (end - begin)/1000);
		
		ServiceOrder serviceOrder = null;
		int count = 0;
		while (serviceOrder == null && count < 3) {
			serviceOrder = serviceOrderRepository.findById(orderId).get();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			count++;
		}
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < imgUrls.size(); i++) {
			String uploadedUrl = uploaded.get(imgUrls.get(i));
			if (!StringUtils.isEmpty(uploadedUrl)) {
				bf.append(uploadedUrl);
				if (i!=imgUrls.size()-1) {
					bf.append(",");
				}
			}
		}
		serviceOrderRepository.updateImgUrls(bf.toString(), orderId);
		
		end = System.currentTimeMillis();
		logger.info("save img2db time : " + (end - begin)/1000);
		
	}
	
	/**
	 * 非一口价分派订单
	 */
	@Override
	public void assginOrder(CommonPayResponse data) {
		
		long begin = System.currentTimeMillis();
		
		//如果是非一口价的订单，需要分发抢单的信息给操作员,异步
		ServiceNotification serviceNotification = data.getServiceNotification();
		logger.info("receivOrder : " + serviceNotification);
		if (serviceNotification != null) {
			serviceNotification.setOrderId(data.getTradeWaterId());
			sendServiceNotificationAsync(data.getServiceNotification());
		}
		
		long end = System.currentTimeMillis();
		logger.info("assginOrder location 1 : " + (end - begin)/1000);
		
	}
	
	/**
	 * 服务消息推送
	 */
	public void sendServiceNotificationAsync(ServiceNotification serviceNotification) {
		
		if (serviceNotification == null) {
			return;
		}
		
		String key = ModelConstant.KEY_ASSIGN_CS_ORDER_DUPLICATION_CHECK + serviceNotification.getOrderId();
		Long result = RedisLock.lock(key, redisTemplate, 3600l);
		logger.info("result : " + result);
		if (0 == result) {
			logger.info("trade : " + serviceNotification.getOrderId() + ", already in the send queue, will skip .");
			return;
		}
		
		int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				String value = objectMapper.writeValueAsString(serviceNotification);
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_SERVICE_QUEUE, value);
				isSuccess = true;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				retryTimes++;
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		
	}
	
	
	
	/**
	 * 非一口价支付
	 */
	@Override
	@Transactional
	public ServiceOrderPrepayVO orderPay(User user, String orderId, String amount, String couponId) throws Exception {

		long begin = System.currentTimeMillis();
		
		Assert.hasText(orderId, "订单ID不能为空。");
		ServiceOrder serviceOrder = serviceOrderRepository.findById(Long.valueOf(orderId)).get();
		if (serviceOrder == null) {
			throw new BizValidateException("未查询到订单，orderId: " + orderId);
		}
		
		long end = System.currentTimeMillis();
		logger.info("orderPay location 1 : " + (end - begin)/1000);
		
		//1.调用API创建接口
		CustomerServiceOrderDTO dto = new CustomerServiceOrderDTO();
		dto.setLinkman(serviceOrder.getReceiverName());
		dto.setLinktel(serviceOrder.getTel());
		Region region = regionRepository.findById(serviceOrder.getXiaoquId()).get();
		if (region == null) {
			throw new BizValidateException("未查询到小区, region id : " + serviceOrder.getXiaoquId());
		}
		end = System.currentTimeMillis();
		logger.info("orderPay location 2 : " + (end - begin)/1000);
		
		dto.setSectId(String.valueOf(region.getSectId()));
		dto.setServiceAddr(serviceOrder.getAddress());
		dto.setServiceId(String.valueOf(serviceOrder.getProductId()));
		dto.setTradeWaterId(serviceOrder.getOrderNo());
		dto.setTranAmt(amount);
		Coupon coupon = null;
		if (!StringUtils.isEmpty(couponId)) {
			coupon = couponService.findById(Long.valueOf(couponId));
			if (coupon != null) {
				dto.setCouponId(couponId);
				dto.setCouponAmt(String.valueOf(coupon.getAmount()));
				BigDecimal tranAmt = new BigDecimal(amount);
				BigDecimal couponAmt = new BigDecimal(coupon.getAmount());
				BigDecimal payAmt = BigDecimal.ZERO;
				if (tranAmt.compareTo(couponAmt) > 0) {
					payAmt = tranAmt.subtract(couponAmt);
				}else {
					payAmt = new BigDecimal("0.01");
				}
				dto.setTranAmt(payAmt.toString());
			}
		}
		
		dto.setUser(user);
		CommonPayResponse data = customServiceUtil.createOrder(dto);
		
		end = System.currentTimeMillis();
		logger.info("orderPay location 3 : " + (end - begin)/1000);
		
		ServiceOrderPrepayVO vo = new ServiceOrderPrepayVO(data);
		vo.setOrderId(orderId);
		serviceOrder.setPrice(Float.valueOf(amount));
		if (coupon != null) {
			serviceOrder.configCoupon(coupon);
		}
		serviceOrderRepository.save(serviceOrder);
		
		end = System.currentTimeMillis();
		logger.info("orderPay location 4 : " + (end - begin)/1000);
		
		return vo;
		
	}
	

	/**
	 * 确认订单
	 * @throws Exception 
	 */
	@Override
	@Transactional
	public void confirmOrder(User user, String orderId, String operType) throws Exception {
		
		Assert.hasText(orderId, "订单ID不能为空。");
		ServiceOrder serviceOrder = serviceOrderRepository.findById(Long.valueOf(orderId)).get();
		if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
			throw new BizValidateException("未查询到订单, orderId : " + orderId);
		}
		if (serviceOrder.getUserId()!=user.getId() && serviceOrder.getOperatorUserId() != user.getId()) {
			throw new BizValidateException("当前用户无法查看此订单。orderId : " + orderId + ", userId : " + user.getId());
		}

		Date date = new Date();
		OperOrderRequest operOrderRequest = new OperOrderRequest();
		operOrderRequest.setOperDate(DateUtil.dtFormat(date, "yyyyMMddHHmmss"));
		operOrderRequest.setOpenid(user.getOpenid());
		operOrderRequest.setTradeWaterId(serviceOrder.getOrderNo());
		operOrderRequest.setOperType("1");
		customServiceUtil.operatorOrder(user, operOrderRequest);
		
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
		ServiceOrder serviceOrder = serviceOrderRepository.findById(Long.valueOf(orderId)).get();
		if (serviceOrder!=null) {
			boolean isServiceOper = operatorService.isOperator(HomeServiceConstant.SERVICE_TYPE_CUSTOM,user.getId());
			logger.info("isServiceOper : " + isServiceOper);
			if (serviceOrder.getUserId()!=user.getId() && !isServiceOper) {
				throw new BizValidateException("当前用户无法查看此订单。orderId : " + orderId + ", userId : " + user.getId());
			}else if (serviceOrder.getUserId()!=user.getId() && isServiceOper) {
				if(serviceOrder.getOperatorUserId() != 0 && serviceOrder.getOperatorUserId() != user.getId()) {
					throw new BizValidateException("订单已被抢。orderId : " + orderId);
				}
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

		Assert.hasText(orderId, "订单ID不能为空。");
		String key = ModelConstant.KEY_ORDER_ACCEPTED + orderId;
		Long result = RedisLock.lock(key, redisTemplate, 3600l);
		if (0 == result) {
			throw new BizValidateException("请稍后再试。");
		}
		
		ServiceOrder serviceOrder = serviceOrderRepository.findById(Long.valueOf(orderId)).get();
		if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
			throw new BizValidateException("未查询到订单, orderId : " + orderId);
		}
		if (ModelConstant.ORDER_STATUS_ACCEPTED == serviceOrder.getStatus()) {
			throw new BizValidateException("订单["+orderId+"]已被抢。");
		}
		Date date = new Date();
		OperOrderRequest operOrderRequest = new OperOrderRequest();
		operOrderRequest.setOperDate(DateUtil.dtFormat(date, "yyyyMMddHHmmss"));
		operOrderRequest.setOpenid(user.getOpenid());
		operOrderRequest.setTradeWaterId(serviceOrder.getOrderNo());
		operOrderRequest.setOperType("0");	//0接单，1确认，9取消
		customServiceUtil.operatorOrder(user, operOrderRequest);
		
		serviceOrder.setOperatorName(user.getName());
		serviceOrder.setOperatorOpenId(user.getOpenid());
		serviceOrder.setOperatorTel(user.getTel());
		serviceOrder.setOperatorUserId(user.getId());
		serviceOrder.setAcceptedDate(date);
		serviceOrder.setStatus(ModelConstant.ORDER_STATUS_ACCEPTED);
		serviceOrderRepository.save(serviceOrder);
	
		//发送客服消息，告知客户已接单。应该做异步队列TODO
		gotongService.sendCustomServiceAssignedMsg(serviceOrder);
		RedisLock.releaseLock(key, redisTemplate);
		 
	}

	/**
	 * 根据状态查询订单
	 * @param status 0可接单，9确认完工，15已接单
	 */
	@Override
	public List<ServiceOrder> queryOrderByStatus(User user, String status, String serviceId) {

		Assert.notNull(user, "用户信息不能为空。");
		Assert.hasText(status, "订单状态不能为空");
		
		if (StringUtils.isEmpty(serviceId)) {
			serviceId = "0";
		}
		List<Integer> statusList = new ArrayList<>();
		statusList.add(Integer.valueOf(status));
		List<ServiceOrder> orderList  = null;
		if ("0".equals(status)) {
			orderList = serviceOrderRepository.findByOrderStatusAndOrderTypeAndSubType(statusList, ModelConstant.ORDER_TYPE_SERVICE, Long.valueOf(serviceId));
		}else {
			orderList = serviceOrderRepository.findByOperAndStatusAndOrderTypeAndSubType(user.getId(), statusList, ModelConstant.ORDER_TYPE_SERVICE, Long.valueOf(serviceId));
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
		ServiceOrder serviceOrder = serviceOrderRepository.findById(Long.valueOf(orderId)).get();
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
		ServiceOrder serviceOrder = serviceOrderRepository.findById(Long.valueOf(orderId)).get();
		if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
			throw new BizValidateException("未查询到订单, orderId : " + orderId);
		}
		logger.info("orderId : " + orderId + ", orderStatus : " + serviceOrder.getStatus());
		Date date = new Date();
		if (ModelConstant.ORDER_STATUS_INIT == serviceOrder.getStatus()) {
			//do nothing
		}else if (ModelConstant.ORDER_STATUS_ACCEPTED == serviceOrder.getStatus()) {
			serviceOrder.setStatus(ModelConstant.ORDER_STATUS_PAYED);
			serviceOrder.setConfirmDate(date);
		}
		serviceOrder.setPayDate(date);
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
		
		ServiceOrder serviceOrder = serviceOrderRepository.findById(Long.valueOf(serviceCommentDTO.getOrderId())).get();
		if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
			throw new BizValidateException("未查询到订单, orderId : " + serviceCommentDTO.getOrderId());
		}
		if (ModelConstant.ORDER_PINGJIA_TYPE_Y == serviceOrder.getPingjiaStatus()) {
			throw new BizValidateException("订单已评价，订单ID：" + serviceCommentDTO.getOrderId());
		}
		serviceOrderRepository.updateComment(serviceCommentDTO.getComment(), serviceCommentDTO.getCommentAttitude(), serviceCommentDTO.getCommentQuality(), 
				serviceCommentDTO.getCommentService(), "", ModelConstant.ORDER_PINGJIA_TYPE_Y, new Date(), serviceOrder.getId());
		
		serviceOrderRepository.save(serviceOrder);
	}
	
	/**
	 * 保存评论时上传的图片
	 */
	@Transactional
	@Override
	@Async("taskExecutor")
	public void saveCommentImages(String appId, long orderId, List<String>imgUrls) {
		
		Map<String, String> uploaded = uploadService.uploadImages(appId, imgUrls);
		ServiceOrder serviceOrder = null;
		int count = 0;
		while (serviceOrder == null && count < 3) {
			serviceOrder = serviceOrderRepository.findById(orderId).get();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			count++;
		}
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < imgUrls.size(); i++) {
			String uploadedUrl = uploaded.get(imgUrls.get(i));
			if (!StringUtils.isEmpty(uploadedUrl)) {
				bf.append(uploadedUrl);
				if (i!=imgUrls.size()-1) {
					bf.append(",");
				}
			}
		}
		serviceOrderRepository.updateCommentImgUrls(bf.toString(), orderId);
		
	}
	
	/**
	 * 取消支付
	 * @param user
	 * @param orderId
	 * @throws Exception 
	 */
	@Override
	@Transactional
	public void cancelPay(User user, String orderId) throws Exception {
		
		Assert.hasText(orderId, "订单ID不能为空。");
		
		ServiceOrder serviceOrder = serviceOrderRepository.findById(Long.valueOf(orderId)).get();
		if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
			throw new BizValidateException("未查询到订单, orderId : " + orderId);
		}
		customServiceUtil.cancelPay(user, serviceOrder.getOrderNo());
		
		if (ModelConstant.ORDER_STATUS_INIT == serviceOrder.getStatus()) {	//1.先支付，后完工
			serviceOrderRepository.deleteById(serviceOrder.getId());
		}
		
	}
	
	/**
	 * 获取服务人员
	 * @param user
	 * @param orderId
	 * @throws Exception 
	 */
	@Override
	@Transactional
	public void operator(OperatorDTO operatorDTO) {
		
		if (operatorDTO == null) {
			return;
		}
		int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				String value = objectMapper.writeValueAsString(operatorDTO);
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_UPDATE_OPERATOR_QUEUE, value);
				isSuccess = true;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				retryTimes++;
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		
	}
	
    @Override
    public void updateServiceCfg(ServiceCfgDTO serviceCfgDTO) {
    	
    	int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				String value = objectMapper.writeValueAsString(serviceCfgDTO);
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_UPDATE_SERVICE_CFG_QUEUE, value);
				isSuccess = true;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				retryTimes++;
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					logger.error(e.getMessage(), e);
				}
			}
		}
    	
    }
    
    @Override
    public ServiceOrderQueryVO queryOrderByFeeType(OrderQueryDTO orderQueryDTO) throws Exception {
    	
    	return customServiceUtil.queryOrder(orderQueryDTO);
    	
    }
    
    
}

package com.yumu.hexie.service.customservice.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.customservice.CustomServiceUtil;
import com.yumu.hexie.integration.customservice.dto.CustomerServiceOrderDTO;
import com.yumu.hexie.integration.customservice.dto.OperatorDTO;
import com.yumu.hexie.integration.customservice.dto.ServiceCommentDTO;
import com.yumu.hexie.integration.customservice.req.OperOrderRequest;
import com.yumu.hexie.integration.customservice.resp.CreateOrderResponseVO;
import com.yumu.hexie.integration.customservice.resp.CustomServiceVO;
import com.yumu.hexie.integration.customservice.resp.ServiceOrderPrepayVO;
import com.yumu.hexie.integration.notify.PayNotification.ServiceNotification;
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
	
	private static final Logger logger = LoggerFactory.getLogger(CustomServiceImpl.class);
	
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
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
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
	public ServiceOrderPrepayVO createOrder(CustomerServiceOrderDTO customerServiceOrderDTO) throws Exception {
		
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
		
		//3.如果是非一口价的订单，需要分发抢单的信息给操作员,异步
		ServiceNotification serviceNotification = data.getServiceNotification();
		if (serviceNotification != null) {
			serviceNotification.setOrderId(String.valueOf(serviceOrder.getId()));
			notifyService.sendServiceNotificationAsync(data.getServiceNotification());
		}
		//单列字段，前端需要。这里就不单独弄一个VO了
		ServiceOrderPrepayVO vo = new ServiceOrderPrepayVO(data);
		vo.setOrderId(String.valueOf(serviceOrder.getId()));
		return vo;
		
	}
	
	/**
	 * 非一口价支付
	 */
	@Override
	@Transactional
	public ServiceOrderPrepayVO orderPay(User user, String orderId, String amount) throws Exception {
		
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
			throw new BizValidateException("未查询到小区, region id : " + serviceOrder.getXiaoquId());
		}
		dto.setSectId(String.valueOf(region.getSectId()));
		dto.setServiceAddr(serviceOrder.getAddress());
		dto.setServiceId(String.valueOf(serviceOrder.getProductId()));
		dto.setTradeWaterId(serviceOrder.getOrderNo());
		dto.setTranAmt(amount);
		dto.setUser(user);
		CreateOrderResponseVO data = customServiceUtil.createOrder(dto).getData();
		ServiceOrderPrepayVO vo = new ServiceOrderPrepayVO(data);
		vo.setOrderId(orderId);
		
		serviceOrder.setPrice(Float.valueOf(amount));
		serviceOrderRepository.save(serviceOrder);
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

		String key = ModelConstant.KEY_ORDER_ACCEPTED + orderId;

		Assert.hasText(orderId, "订单ID不能为空。");
		getLock(key);
		
		ServiceOrder serviceOrder = serviceOrderRepository.findOne(Long.valueOf(orderId));
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
	
		releaseLock(key);
		 
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
		if (ModelConstant.ORDER_PINGJIA_TYPE_Y == serviceOrder.getPingjiaStatus()) {
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
		
		ServiceOrder serviceOrder = serviceOrderRepository.findOne(Long.valueOf(orderId));
		if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
			throw new BizValidateException("未查询到订单, orderId : " + orderId);
		}
		customServiceUtil.cancelPay(user, serviceOrder.getOrderNo());
		
		if (ModelConstant.ORDER_STATUS_INIT == serviceOrder.getStatus()) {	//1.先支付，后完工
			serviceOrderRepository.delete(serviceOrder.getId());
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
	
	/**
	 * 获取订单锁
	 * @param key
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getLock(String key) {
		
		//rua script保证setnx 跟expire 在一个操作里，保证了原子性,新版本setIfAbsent直接支持，老版本无法保证原子性
//		String script = "if redis.call('setNx',KEYS[1],ARGV[1])==1 then return 1 else return 0 end"; 
		String script = "if redis.call('setNx',KEYS[1],ARGV[1])==1 then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end "; 
		RedisScript redisScript = new DefaultRedisScript<>(script, Long.class);
		
		Object result = stringRedisTemplate.execute(redisScript, stringRedisTemplate.getKeySerializer(), stringRedisTemplate.getValueSerializer(), 
				Collections.singletonList(key), "1", "3600");
		
		logger.info("result : " + result);
	}
	
	 /**
     * 释放锁
     * @param lockKey
     * @param value
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean releaseLock(String key){
 
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
 
        RedisScript redisScript = new DefaultRedisScript<>(script, Long.class);
 
        Object result = stringRedisTemplate.execute(redisScript, stringRedisTemplate.getKeySerializer(), stringRedisTemplate.getValueSerializer(), 
        		Collections.singletonList(key), "1");
        logger.info("result : " + result);
        return false;
    }
    

}

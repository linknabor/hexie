package com.yumu.hexie.service.sales.impl;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.OrderNoUtil;
import com.yumu.hexie.integration.common.CommonPayRequest;
import com.yumu.hexie.integration.common.CommonPayRequest.SubOrder;
import com.yumu.hexie.integration.common.CommonPayResponse;
import com.yumu.hexie.integration.eshop.service.EshopUtil;
import com.yumu.hexie.integration.wechat.entity.common.JsSign;
import com.yumu.hexie.integration.wechat.entity.common.WxRefundOrder;
import com.yumu.hexie.integration.wechat.service.TemplateMsgService;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.agent.Agent;
import com.yumu.hexie.model.agent.AgentRepository;
import com.yumu.hexie.model.commonsupport.comment.Comment;
import com.yumu.hexie.model.commonsupport.comment.CommentConstant;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.commonsupport.info.ProductRule;
import com.yumu.hexie.model.distribution.region.City;
import com.yumu.hexie.model.distribution.region.CityRepository;
import com.yumu.hexie.model.distribution.region.County;
import com.yumu.hexie.model.distribution.region.CountyRepository;
import com.yumu.hexie.model.distribution.region.Province;
import com.yumu.hexie.model.distribution.region.ProvinceRepository;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.localservice.repair.RepairOrderRepository;
import com.yumu.hexie.model.market.Cart;
import com.yumu.hexie.model.market.Evoucher;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.OrderItemRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.market.saleplan.RgroupRule;
import com.yumu.hexie.model.market.saleplan.SalePlan;
import com.yumu.hexie.model.payment.PaymentConstant;
import com.yumu.hexie.model.payment.PaymentOrder;
import com.yumu.hexie.model.promotion.coupon.CouponSeed;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.AddressRepository;
import com.yumu.hexie.model.user.Partner;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.car.CarService;
import com.yumu.hexie.service.comment.CommentService;
import com.yumu.hexie.service.common.ShareService;
import com.yumu.hexie.service.common.SmsService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.common.WechatCoreService;
import com.yumu.hexie.service.eshop.EvoucherService;
import com.yumu.hexie.service.eshop.PartnerService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.o2o.OperatorDefinition;
import com.yumu.hexie.service.o2o.OperatorService;
import com.yumu.hexie.service.payment.PaymentService;
import com.yumu.hexie.service.sales.BaseOrderService;
import com.yumu.hexie.service.sales.CacheableService;
import com.yumu.hexie.service.sales.CartService;
import com.yumu.hexie.service.sales.ProductService;
import com.yumu.hexie.service.sales.SalePlanService;
import com.yumu.hexie.service.sales.req.PromotionOrder;
import com.yumu.hexie.service.user.UserNoticeService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.service.user.dto.GainCouponDTO;
import com.yumu.hexie.vo.CreateOrderReq;
import com.yumu.hexie.vo.SingleItemOrder;

@Service("baseOrderService")
public class BaseOrderServiceImpl extends BaseOrderProcessor implements BaseOrderService {

    protected static final Logger log = LoggerFactory.getLogger(BaseOrderServiceImpl.class);
	@Inject
	protected ServiceOrderRepository serviceOrderRepository;
	@Inject
	protected OrderItemRepository orderItemRepository;
	@Inject
	protected ProductService productService;
	@Inject
	protected PaymentService paymentService;
	@Inject
	protected UserService userService;
	@Inject 
	protected UserNoticeService userNoticeService;
	@Inject
	protected CommentService commentService;
	@Inject
	protected WechatCoreService wechatCoreService;
	@Inject
	protected ShareService shareService;
	@Inject
	protected RepairOrderRepository repairOrderRepository;
	@Inject
	private SalePlanService salePlanService;
	@Inject
	private CarService carService;
	@Inject
	private SystemConfigService systemconfigservice;
	@Autowired
	private EshopUtil eshopUtil;
	@Autowired
	private AgentRepository agentRepository;
	@Autowired
	private EvoucherService evoucherService;
	@Autowired
	private TemplateMsgService templateMsgService;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private SmsService smsService;
	@Autowired
	private ProvinceRepository provinceRepository;
	@Autowired
	private CityRepository cityRepository;
	@Autowired
	private CountyRepository countyRepository;
	@Autowired
	private CartService cartService;
	@Autowired
	@Qualifier("stringRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private OperatorService operatorService;
	@Autowired
	private CacheableService cacheableService;
	@Autowired
	private PartnerService partnerService;

	
	private void preOrderCreate(ServiceOrder order, Address address){
	    log.warn("[Create]创建订单OrderNo:" + order.getOrderNo());
		for(OrderItem item : order.getItems()){
			SalePlan plan = findSalePlan(order.getOrderType(),item.getRuleId());
			//校验规则
			salePlanService.getService(order.getOrderType()).validateRule(order, plan, item, address);
			//校验商品
			Product product = productService.getProduct(plan.getProductId());
			productService.checkSalable(product, item.getCount());
			//填充信息
//			productService.freezeCount(product, item.getCount());	放到外面
            item.fillDetail(plan, product);
            
            Long agentId = product.getAgentId();
            if (ModelConstant.ORDER_TYPE_PROMOTION == order.getOrderType()) {
				agentId = order.getAgentId();
				if (agentId == 0) {
					agentId = product.getAgentId();
				}
			}
            Agent agent = new Agent();
            Optional<Agent> optional = agentRepository.findById(agentId);
            if (optional.isPresent()) {
            	agent = optional.get();
            	item.setAgentId(agent.getId());
                item.setAgentName(agent.getName());
                item.setAgentNo(agent.getAgentNo());
			}
            
			if(StringUtils.isEmpty(order.getProductName())){
                order.fillProductInfo(product);
                order.fillAgentInfo(agent);
                order.setGroupRuleId(plan.getId());
            }
		}
		computePrice(order);
		log.warn("[Create]创建订单OrderNo:" + order.getOrderNo()+"|" + order.getProductName() +"|"+order.getPrice());
	}

	@Override
	public ServiceOrder createRepairOrder(RepairOrder order, float amount) {
        ServiceOrder sOrder = null;
        OrderItem item = null;
        if(order.getOrderId() != null && order.getOrderId() != 0) {
            sOrder = serviceOrderRepository.findById(order.getOrderId()).get();
            if(sOrder!=null){
                if(sOrder.getStatus() == ModelConstant.ORDER_STATUS_CANCEL){
                    sOrder = null;
                } else  if(sOrder.getStatus() != ModelConstant.ORDER_STATUS_INIT){
                    throw new BizValidateException("该维修单无法线上支付");
                } else {
                    List<OrderItem> items = orderItemRepository.findByServiceOrder(sOrder);
                    item = items.get(0);
                }
            }
        }
        if(sOrder == null) {
            sOrder = new ServiceOrder(order,amount);
            item = sOrder.getItems().get(0);
        }
        fillAddressInfo(sOrder);

        sOrder.setPrice(amount);
        sOrder = serviceOrderRepository.save(sOrder);
        
        order.setOrderId(sOrder.getId());
        repairOrderRepository.save(order);

        item.setServiceOrder(sOrder);
        item.setAmount(amount*1f);
        item.setUserId(sOrder.getUserId());
        orderItemRepository.save(item);
        return sOrder;
    }
	
	/**
	 * 创建订单，单个种类物品
	 */
	@Override
	@Transactional
	public ServiceOrder createOrder(SingleItemOrder order){
		
		//1. 填充地址信息
		ServiceOrder o = new ServiceOrder(order);
		Address address = fillAddressInfo(o);
		//2. 填充订单信息并校验规则,设置价格信息
		preOrderCreate(o, address);
		//3. 订单创建
		o = serviceOrderRepository.save(o);
		
		List<OrderItem> items = o.getItems();
		for(OrderItem item : items) {
			item.setServiceOrder(o);
			item.setUserId(o.getUserId());
			orderItemRepository.save(item);
		}
		//4.保存车辆信息 20160721 车大大的车辆服务
		carService.saveOrderCarInfo(o);
		
		//5.电子优惠券订单
		evoucherService.createEvoucher(o);
		
        log.warn("[Create]订单创建OrderId:" + o.getId());
        
        //6.计算优惠券
        computeCoupon(o);
        
		//7. 订单后处理
		commonPostProcess(ModelConstant.ORDER_OP_CREATE,o);
		
		
		//8.冻结库存，这步必须最后
		for(OrderItem item : items) {
			Product pro = new Product();
			pro.setId(item.getProductId());
			productService.freezeCount(pro, item.getCount());
		}
		return o;
	}

	/**
	 * 根据购物车创建订单(老版本)
	 */
	@Override
	@Transactional
	public ServiceOrder createOrder(User user, CreateOrderReq req, Cart cart){
		
		ServiceOrder o = new ServiceOrder(user, req, cart);
		//1. 填充地址信息
		Address address = fillAddressInfo(o);
		//2. 填充订单信息并校验规则,设置价格信息
		preOrderCreate(o, address);
		//3. 订单创建
		o = serviceOrderRepository.save(o);
		List<OrderItem> items = o.getItems();
		for(OrderItem item : items) {
			item.setServiceOrder(o);
			item.setUserId(o.getUserId());
			orderItemRepository.save(item);
		}
		
		computeCoupon(o);
		
		//4.保存车辆信息 20160721 车大大的车辆服务
		carService.saveOrderCarInfo(o);
		
		//5.电子优惠券订单
		evoucherService.createEvoucher(o);
		
        log.warn("[Create]订单创建OrderNo:" + o.getOrderNo());
		//4. 订单后处理
		commonPostProcess(ModelConstant.ORDER_OP_CREATE,o);
		return o;
		
	}
	
	/**
	 * 根据购物车创建订单
	 */
	@Override
	@Transactional
	public ServiceOrder createOrderFromCart(User user, CreateOrderReq req){
		
		Map<Long, List<OrderItem>> itemsMap = new HashMap<>();
		//重新设置页面传上来的商品价格，因为前端传值可以被篡改。除了规则id和件数采用前端上传的
		List<OrderItem> itemList = req.getItemList();
		for (OrderItem orderItem : itemList) {
			String key = ModelConstant.KEY_PRO_RULE_INFO + orderItem.getRuleId();
			ProductRule productRule = redisRepository.getProdcutRule(key);
			if (productRule == null) {
				throw new BizValidateException("未查询到商品规则：" + orderItem.getRuleId());
			}
			
			//只设置单价和免邮件数这些基本属性，以保证后面计算的正确性
			BigDecimal amt = new BigDecimal(String.valueOf(productRule.getPrice())).multiply(new BigDecimal(String.valueOf(orderItem.getCount())));
			orderItem.setAmount(amt.floatValue());
			orderItem.setOriPrice(productRule.getOriPrice());
			orderItem.setFreeShippingNum(productRule.getFreeShippingNum());
			orderItem.setPostageFee(productRule.getPostageFee());
			orderItem.setPrice(productRule.getPrice());
			
			Long agentId = productRule.getAgentId();
			orderItem.setAgentId(productRule.getAgentId());
			
			if (!itemsMap.containsKey(agentId)) {
				List<OrderItem> oList = new ArrayList<>();
				oList.add(orderItem);
				itemsMap.put(agentId, oList);
			} else {
				List<OrderItem> oList = itemsMap.get(agentId);
				oList.add(orderItem);
			}
			
		}
		
		BigDecimal totalOrderAmount = BigDecimal.ZERO;
		Long groupId = Long.valueOf(OrderNoUtil.generateServiceNo());
		Iterator<Entry<Long, List<OrderItem>>> it = itemsMap.entrySet().iterator();
		while(it.hasNext()) {

			Entry<Long, List<OrderItem>> entry = it.next();
			CreateOrderReq orderRequest = new CreateOrderReq();
			BeanUtils.copyProperties(req, orderRequest);
			orderRequest.setItemList(entry.getValue());
			
			ServiceOrder o = new ServiceOrder(user, orderRequest);
			o.setGroupOrderId(groupId);
			
			//1. 填充地址信息
			Address address = fillAddressInfo(o);
			//2. 填充订单信息并校验规则,设置价格信息
			preOrderCreate(o, address);
			//3. 先保存order，产生一个orderId
			serviceOrderRepository.save(o);
			
			totalOrderAmount = totalOrderAmount.add(new BigDecimal(String.valueOf(o.getPrice())));	//这里面含运费
			
			log.info("generated order id : " + o.getId());
			List<OrderItem> items = o.getItems();
			log.info("items : " + items);
			
			//4. 保存orderItem
			for(OrderItem item : items) {
				item.setServiceOrder(o);
				item.setUserId(o.getUserId());
				orderItemRepository.save(item);
			}
			//5. 订单后处理
			commonPostProcess(ModelConstant.ORDER_OP_CREATE, o);
			
		}
		
		//6.红包分摊
		computeCoupon4GroupOrders(groupId);
		
		ServiceOrder newOrder = new ServiceOrder();
		newOrder.setId(groupId);
		
		//7.冻结库存。这步必须放最后，因为redis没法跟随数据库一起回滚
		for (OrderItem orderItem : itemList) {
			Product pro = new Product();
			pro.setId(orderItem.getProductId());
			productService.freezeCount(pro, orderItem.getCount());
			//6.清空购物车中已购买的商品
			cartService.delFromCart(user.getId(), itemList);
		}
		return newOrder;
		
	}
	
	@Async
	protected void commonPostProcess(int orderOp, ServiceOrder order) {

		log.error("commonPostProcess" + order.getOrderNo());
		User user = userService.getById(order.getUserId());//短信发送号码修改为用户注册号码 2016012
		if(orderOp==ModelConstant.ORDER_OP_CREATE){
			log.error("shareService.record" + order.getOrderNo());
			shareService.record(order);
			//userNoticeService.noticeUser(order.getUserId(), ModelConstant.NOTICE_TYPE_NOTICE, "订单"+order.getOrderNo()+"已创建", "");
		} else if(orderOp==ModelConstant.ORDER_OP_CANCEL){
			//userNoticeService.noticeUser(order.getUserId(), ModelConstant.NOTICE_TYPE_NOTICE, "订单"+order.getOrderNo()+"已取消！", "");
		} else if(orderOp == ModelConstant.ORDER_OP_UPDATE_PAYSTATUS
				&&(order.getStatus()==ModelConstant.ORDER_STATUS_PAYED||order.getStatus()==ModelConstant.ORDER_STATUS_CONFIRM)){
			if(order.getOrderType() != ModelConstant.ORDER_TYPE_YUYUE){
				userNoticeService.orderSuccess(order.getUserId(), user.getTel(),order.getId(), order.getOrderNo(), order.getProductName(), order.getPrice());
			}
			String token = systemconfigservice.queryWXAToken(user.getAppId());
			templateMsgService.sendPaySuccessMsg(order, token, user.getAppId());
		} else if(orderOp == ModelConstant.ORDER_OP_SEND){
			userNoticeService.orderSend(order.getUserId(), order.getTel(),order.getId(), order.getOrderNo(), order.getLogisticName(), order.getLogisticNo());
		}
	}
	
	@Override
	@Transactional
	public JsSign requestOrderPay(User user, long orderId) throws Exception {
		
		JsSign jsSign = null;
		ServiceOrder order = findOne(orderId);
		if (order != null && order.getId() > 0) {
			if(user.getId() != order.getUserId()) {
				throw new BizValidateException("无法支付他人订单");
			}
			jsSign = requestPay(order);
		}else {
			jsSign = requestGroupPay(orderId);
		}
		return jsSign;
		
	}

	@Override
	@Transactional
	public JsSign requestPay(ServiceOrder order) throws Exception {
		
        log.info("[requestPay]OrderId:" + order.getId() + ", orderType : " + order.getOrderType());
		//校验订单状态
		if(!order.payable()){
            throw new BizValidateException(order.getId(),"订单状态不可支付，请重新查询确认订单状态！").setError();
        }
		JsSign sign = null;
		//核销券订单走平台支付接口。其余老的订单走原有支付，会慢慢改成新接口
		if (ModelConstant.ORDER_TYPE_EVOUCHER == order.getOrderType() || 
				ModelConstant.ORDER_TYPE_ONSALE == order.getOrderType() ||
				ModelConstant.ORDER_TYPE_RGROUP == order.getOrderType() ||
				ModelConstant.ORDER_TYPE_PROMOTION == order.getOrderType() ||
				ModelConstant.ORDER_TYPE_SAASSALE == order.getOrderType()) {
			
			User user = userService.getById(order.getUserId());
			CommonPayRequest request = new CommonPayRequest();
			request.setUserId(user.getWuyeId());
			request.setAppid(user.getAppId());
			if (ModelConstant.ORDER_TYPE_PROMOTION != order.getOrderType() && ModelConstant.ORDER_TYPE_SAASSALE != order.getOrderType()) {
				request.setSectId(user.getSectId());
			}
			request.setServiceId(String.valueOf(order.getProductId()));
			String linkman = order.getReceiverName();
			if (!StringUtils.isEmpty(linkman)) {
				linkman = URLEncoder.encode(linkman,"GBK");
			}
			request.setLinkman(linkman);
			request.setLinktel(order.getTel());
			
			String address = order.getAddress();
			if (!StringUtils.isEmpty(address)) {
				address = URLEncoder.encode(address,"GBK");
			}
			request.setServiceAddr(address);
			request.setOpenid(order.getOpenId());
			request.setTranAmt(String.valueOf(order.getPrice()));
			request.setTradeWaterId(order.getOrderNo());
			request.setShipFee((String.valueOf(order.getShipFee()));

			String productName = order.getProductName();
			if (!StringUtils.isEmpty(productName)) {
				productName = URLEncoder.encode(productName,"GBK");
			}
			request.setServiceName(productName);
			request.setOrderType(String.valueOf(order.getOrderType()));
			
			if (order.getCouponId() !=null && order.getCouponId() > 0) {
				request.setCouponId(String.valueOf(order.getCouponId()));
				request.setCouponAmt(String.valueOf(order.getCouponAmount()));
			}
			
			Optional<Agent> optional = agentRepository.findById(order.getAgentId());
			if (optional.isPresent()) {
				Agent agent = optional.get();
				request.setAgentNo(agent.getAgentNo());
				String agentName = agent.getName();
				if (!StringUtils.isEmpty(agentName)) {
					agentName = URLEncoder.encode(agentName,"GBK");
				}
				request.setAgentName(agentName);
			}
			request.setCount(String.valueOf(order.getCount()));
			
			List<OrderItem> itemList = orderItemRepository.findByServiceOrder(order);
			List<SubOrder> subOrderList = new ArrayList<>(itemList.size());
			
			for (OrderItem orderItem : itemList) {
				SubOrder subOrder = new SubOrder();
				String subAgentName = orderItem.getAgentName();
				if (!StringUtils.isEmpty(subAgentName)) {
					subAgentName = URLEncoder.encode(subAgentName,"GBK");
				}
				subOrder.setAgentName(subAgentName);
				subOrder.setAgentNo(orderItem.getAgentNo());
				subOrder.setAmount(orderItem.getAmount());
				subOrder.setCount(orderItem.getCount());
				subOrder.setSubPic(orderItem.getProductThumbPic());

				String subProductName = orderItem.getProductName();
				if (!StringUtils.isEmpty(subProductName)) {
					subProductName = URLEncoder.encode(subProductName,"GBK");
				}
				subOrder.setProductName(subProductName);
				subOrder.setProductId(orderItem.getProductId());
				subOrderList.add(subOrder);
			}
			if (order.getCouponId() !=null && order.getCouponId() > 0) {
				SubOrder subOrder = subOrderList.get(0);
				if (subOrder!=null) {
					subOrder.setSubCouponId(order.getCouponId());
					subOrder.setSubCouponAmt(order.getCouponAmount());
				}
			}
			
			request.setSubOrders(subOrderList);
			
			CommonPayResponse responseVo = eshopUtil.requestPay(user, request);
			sign = new JsSign();
			sign.setAppId(responseVo.getAppid());
			sign.setNonceStr(responseVo.getNoncestr());
			sign.setPkgStr(responseVo.getPack());
			sign.setSignature(responseVo.getPaysign());
			sign.setSignType(responseVo.getSigntype());
			sign.setTimestamp(responseVo.getTimestamp());
			sign.setOrderId(String.valueOf(order.getId()));
			order = serviceOrderRepository.findById(order.getId()).get();
			if (StringUtils.isEmpty(order.getOrderNo())) {
				order.setOrderNo(responseVo.getTradeWaterId());	//set之后,jpa会有脏检查，如果数据发生变化，会在事务提交时执行update
			}

			//操作记录
			commonPostProcess(ModelConstant.ORDER_OP_REQPAY,order);
			
		}else {	
			//获取支付单
			PaymentOrder pay = paymentService.fetchPaymentOrder(order);
			User user = userService.getById(order.getUserId());
	        log.warn("[requestPay]PaymentId:" + pay.getId());
			//发起支付
			sign = paymentService.requestPay(user, pay);
	        log.warn("[requestPay]NonceStr:" + sign.getNonceStr());
			//操作记录
			commonPostProcess(ModelConstant.ORDER_OP_REQPAY,order);
			
		}
		return sign;
	}
	
	@Override
	@Transactional
	public JsSign requestGroupPay(long orderId) throws Exception {
		
		List<ServiceOrder> orderList = serviceOrderRepository.findByGroupOrderId(orderId);
		if (orderList.isEmpty()) {
			throw new BizValidateException("未查询倒订单：" + orderId);
		}
		log.info("[requestPay]OrderId:" + orderId);
		
		ServiceOrder o = orderList.get(0);
		//校验订单状态
		if(!o.payable()){
            throw new BizValidateException(orderId,"订单状态不可支付，请重新查询确认订单状态！").setError();
        }
	
		CommonPayRequest request = new CommonPayRequest();
		List<SubOrder> subOrderList = new ArrayList<>(orderList.size());
		
		Long couponId = 0l;
		Float couponAmt = 0f;
		BigDecimal totalPrice = BigDecimal.ZERO;
		int totalCount = 0;
		for (ServiceOrder order : orderList) {
			SubOrder subOrder = new SubOrder();
			String subAgentName = order.getAgentName();
			if (!StringUtils.isEmpty(subAgentName)) {
				subAgentName = URLEncoder.encode(subAgentName,"GBK");
			}
			subOrder.setAgentName(subAgentName);
			subOrder.setAgentNo(order.getAgentNo());
			subOrder.setAmount(order.getPrice());
			subOrder.setCount(order.getCount());
			
			String subProductName = order.getProductName();
			if (!StringUtils.isEmpty(subProductName)) {
				subProductName = URLEncoder.encode(subProductName,"GBK");
			}
			subOrder.setProductName(subProductName);
			subOrder.setProductId(order.getProductId());
			
			if (order.getCouponId() != null && order.getCouponId() > 0) {
				subOrder.setSubCouponId(order.getCouponId());
				couponId = order.getCouponId();
			}
			if (order.getCouponAmount() != null) {	//拆单交易，多个订单只有一个couponId，只记录再一个serivceOrder中，其他子订单不记录couponId，但记录订单金额
				subOrder.setSubCouponAmt(order.getCouponAmount());
				couponAmt += order.getCouponAmount();
			}
			subOrderList.add(subOrder);
			
			totalPrice = totalPrice.add(new BigDecimal(String.valueOf(order.getPrice())));
			totalCount += order.getCount();
		}
		request.setSubOrders(subOrderList);
		if (couponId > 0 ) {
			request.setCouponId(String.valueOf(couponId));
			request.setCouponAmt(String.valueOf(couponAmt));
		}

		User user = userService.getById(o.getUserId());
		request.setUserId(user.getWuyeId());
		request.setAppid(user.getAppId());
		Optional<Region> optional = regionRepository.findById(o.getXiaoquId());
		Region region = null;
		if (optional.isPresent()) {
			region = optional.get();
			request.setSectId(region.getSectId());
		}
		if (StringUtils.isEmpty(request.getSectId())) {
			throw new BizValidateException("未查询地址所对应的小区ID， addressId : " + o.getServiceAddressId());
		}
		
		request.setServiceId(String.valueOf(o.getProductId()));
		String linkman = o.getReceiverName();
		if (!StringUtils.isEmpty(linkman)) {
			linkman = URLEncoder.encode(linkman,"GBK");
		}
		request.setLinkman(linkman);
		request.setLinktel(o.getTel());
		
		String address = o.getAddress();
		if (!StringUtils.isEmpty(address)) {
			address = URLEncoder.encode(address,"GBK");
		}
		request.setServiceAddr(address);
		request.setOpenid(o.getOpenId());
		request.setTranAmt(totalPrice.toString());
		
		String productName = o.getProductName();
		if(orderList.size() > 1){
			productName = productName+"等"+orderList.size()+"种商品";
		}
		if (!StringUtils.isEmpty(productName)) {
			productName = URLEncoder.encode(productName,"GBK");
		}
		request.setServiceName(productName);
		request.setOrderType(String.valueOf(o.getOrderType()));
		
		Optional<Agent> opAgent = agentRepository.findById(o.getAgentId());
		if (optional.isPresent()) {
			Agent agent = opAgent.get();
			request.setAgentNo(agent.getAgentNo());
			String agentName = agent.getName();
			if (!StringUtils.isEmpty(agentName)) {
				agentName = URLEncoder.encode(agentName,"GBK");
			}
			request.setAgentName(agentName);
		}
		request.setCount(String.valueOf(totalCount));
		
		CommonPayResponse responseVo = eshopUtil.requestPay(user, request);
		
		JsSign sign = new JsSign();
		sign.setAppId(responseVo.getAppid());
		sign.setNonceStr(responseVo.getNoncestr());
		sign.setPkgStr(responseVo.getPack());
		sign.setSignature(responseVo.getPaysign());
		sign.setSignType(responseVo.getSigntype());
		sign.setTimestamp(responseVo.getTimestamp());
		sign.setOrderId(String.valueOf(orderId));
		
		o.setOrderNo(responseVo.getTradeWaterId());	//如果是拼单，只记其中一条的orderNo
		commonPostProcess(ModelConstant.ORDER_OP_REQPAY, o);	//记录分享记录，也不循环，拼多视为一单。
		return sign;
	}
	

	@Transactional
    @Override
    public void update4Payment(PaymentOrder payment) {

        log.warn("[update4Payment]Payment:" + payment.getId());
        ServiceOrder order = serviceOrderRepository.findOneWithItem(payment.getOrderId());
		switch(payment.getStatus()) {
            case PaymentConstant.PAYMENT_STATUS_CANCEL:
            case PaymentConstant.PAYMENT_STATUS_FAIL:
            case PaymentConstant.PAYMENT_STATUS_INIT:
                break;
            case PaymentConstant.PAYMENT_STATUS_REFUNDING:
                order.refunding();
                serviceOrderRepository.save(order);
                log.warn("[update4Payment]Refunding");
                break;
            case PaymentConstant.PAYMENT_STATUS_SUCCESS:
                if(order.getStatus()==ModelConstant.ORDER_STATUS_INIT){
                    salePlanService.getService(order.getOrderType()).postPaySuccess(order);
                    couponService.comsume(order);
                    createCouponSeedIfExist(order);
                    commonPostProcess(ModelConstant.ORDER_OP_UPDATE_PAYSTATUS,order);
                    log.warn("[update4Payment]Success");
                }
                break;
            default:
                break;
                
        }
    }
	
	@Async
	protected void createCouponSeedIfExist(ServiceOrder so) {
		CouponSeed cs = couponService.createOrderSeed(so.getUserId(), so);
		if(cs != null) {
			so.setSeedStr(cs.getSeedStr());
			serviceOrderRepository.save(so);
		}
	}

	@Transactional
	@Override
	public void notifyPayed(long orderId) {

		log.info("notifyPayed : " + orderId);
		ServiceOrder so = serviceOrderRepository.findById(orderId).get();
		if(so == null || so.getStatus() == ModelConstant.ORDER_STATUS_PAYED) {
		    return;
		}
		log.info("orderId : " + orderId + ", orderStatus : " + so.getStatus());
		
		if (ModelConstant.ORDER_TYPE_EVOUCHER == so.getOrderType()) {
			return;
		}
		PaymentOrder payment = paymentService.fetchPaymentOrder(so);
        payment = paymentService.refreshStatus(payment);
        update4Payment(payment);
        
	}

	@Override
	@Transactional
	public ServiceOrder confirmOrder(ServiceOrder order) {
        log.warn("[confirmOrder]orderId:"+order.getId());
		order.confirm();
		order = serviceOrderRepository.save(order);
		salePlanService.getService(order.getOrderType()).postOrderConfirm(order);
        log.warn("[confirmOrder]PostConfirm:"+order.getId());
		commonPostProcess(ModelConstant.ORDER_OP_CONFIRM,order);
		return order;
	}
	//订单取消，如果团购单，消减人员，修改团购人员数量
	@Override
	@Transactional
	public ServiceOrder cancelOrder(ServiceOrder order) {
        log.warn("[cancelOrder]req:"+order.getId());
	    //1. 校验
		if(!order.cancelable()) {
            throw new BizValidateException(order.getId(),"该订单不能取消！").setError();
        }
		//2. 取消支付单
		if (ModelConstant.ORDER_TYPE_EVOUCHER == order.getOrderType() || ModelConstant.ORDER_TYPE_SERVICE == order.getOrderType()
				||ModelConstant.ORDER_TYPE_PROMOTION == order.getOrderType() || ModelConstant.ORDER_TYPE_SAASSALE == order.getOrderType()
				||ModelConstant.ORDER_TYPE_ONSALE == order.getOrderType() || ModelConstant.ORDER_TYPE_RGROUP == order.getOrderType()) {
			//do nothing
		}else {
			paymentService.cancelPayment(PaymentConstant.TYPE_MARKET_ORDER, order.getId());
	        log.warn("[cancelOrder]payment:"+order.getId());
		}
		order.cancel();
		serviceOrderRepository.save(order);
        log.warn("[cancelOrder]order:"+order.getId());
		//3.解锁红包
		couponService.unlock(order.getCouponId());
        log.warn("[cancelOrder]coupon:"+order.getCouponId());
		//4.操作后处理
		commonPostProcess(ModelConstant.ORDER_OP_CANCEL,order);
		//5.商品中取消冻结
		if (ModelConstant.ORDER_TYPE_SERVICE != order.getOrderType()) {
			salePlanService.getService(order.getOrderType()).postOrderCancel(order);
	        log.warn("[cancelOrder]unfrezee:"+order.getId());
		}
		return order;
	}


	@Override
	public ServiceOrder signOrder(ServiceOrder order) {
        log.warn("[signOrder]order:"+order.getId());
		if(!order.signable()) {
            throw new BizValidateException(order.getId(),"该订单无法签收！").setError();
        }
		order.sign();
		order = serviceOrderRepository.save(order);
        log.warn("[signOrder]order-signed:"+order.getId());
		commonPostProcess(ModelConstant.ORDER_OP_SIGN,order);
		return order;
	}
	@Transactional
	@Override
	public void comment(ServiceOrder order, Comment comment) {
        log.warn("[comment]order-signed:"+order.getId());
		if(order.getPingjiaStatus() == ModelConstant.ORDER_PINGJIA_TYPE_Y) {
			throw new BizValidateException(order.getId(),"该订单已评价").setError();
		} else if(order.getStatus() != ModelConstant.ORDER_STATUS_RECEIVED){
			throw new BizValidateException(order.getId(),"订单不是签收状态，您无法进行评价！").setError();
		}
		comment = commentService.comment(CommentConstant.TYPE_MARKET_ORDER, order.getId(), comment);
        log.warn("[comment]comment-finish:"+comment.getId());
		order.setPingjiaStatus(ModelConstant.ORDER_PINGJIA_TYPE_Y);
		serviceOrderRepository.save(order);
        log.warn("[comment]order-finish:"+order.getId());
		commonPostProcess(ModelConstant.ORDER_OP_COMMENT,order);
	}


	//FIXME 注意该方法不应该被外部用户调用
	@Transactional
	@Override
	public ServiceOrder refund(ServiceOrder order) throws Exception {
        log.warn("[refund]refund-begin:"+order.getId());
		if(!order.refundable()) {
            throw new BizValidateException(order.getId(),"该订单无法退款！").setError();
        }
		
		if (ModelConstant.ORDER_TYPE_RGROUP == order.getOrderType()) {
			User user = userService.getById(order.getUserId());
			eshopUtil.requestRefund(user, order.getOrderNo());
			order.refunding(true);
		}else if (ModelConstant.ORDER_TYPE_EVOUCHER == order.getOrderType() || 
				ModelConstant.ORDER_TYPE_ONSALE == order.getOrderType() ||
				ModelConstant.ORDER_TYPE_PROMOTION == order.getOrderType() ||
				ModelConstant.ORDER_TYPE_SAASSALE == order.getOrderType() ||
				ModelConstant.ORDER_TYPE_SERVICE == order.getOrderType()) {
			//do nothing
		}else {
			PaymentOrder po = paymentService.fetchPaymentOrder(order);
			if(paymentService.refundApply(po)){
				//FIXME 支付单直接从支付成功到已退款状态  po.setStatus(ModelConstant.PAYMENT_STATUS_REFUND);
				order.refunding(true);
			}
			
		}
		
		order = serviceOrderRepository.save(order);
        log.warn("[refund]refund-finish:"+order.getId());
		commonPostProcess(ModelConstant.ORDER_OP_REFUND_REQ,order);
		return order;
	}
	
	

	@Transactional
	@Override
	public void finishRefund(WxRefundOrder wxRefundOrder) {
        log.warn("[finishRefund]refund-begin:"+wxRefundOrder.getOut_trade_no());
		PaymentOrder po = paymentService.updateRefundStatus(wxRefundOrder);
		ServiceOrder serviceOrder = serviceOrderRepository.findById(po.getOrderId()).get();
		if(po.getStatus() == PaymentConstant.PAYMENT_STATUS_REFUNDED) {
			serviceOrder.setStatus(ModelConstant.ORDER_STATUS_REFUNDED);
			serviceOrderRepository.save(serviceOrder);
	        log.warn("[finishRefund]refund-saved:"+serviceOrder.getId());
			commonPostProcess(ModelConstant.ORDER_OP_REFUND_FINISH,serviceOrder);
		}
        log.warn("[finishRefund]refund-end:"+wxRefundOrder.getOut_trade_no());
	}
	
	/**
	 * 退款处理
	 * 1.修改订单状态 -->已退款
	 * 2.库存重新加回去
	 * 3.已售分数减回去
	 * 4.团购订单，如果还未成团，减成团份数
	 */
	@Transactional
	@Override
	public void finishRefund(ServiceOrder serviceOrder) {
		
        log.warn("[finishRefund]refund-begin:"+serviceOrder.getId());
		if(ModelConstant.ORDER_STATUS_REFUNDING == serviceOrder.getStatus() || ModelConstant.ORDER_STATUS_CONFIRM == serviceOrder.getStatus()|| 
				ModelConstant.ORDER_STATUS_PAYED == serviceOrder.getStatus() || ModelConstant.ORDER_STATUS_SENDED == serviceOrder.getStatus()) {
			
			/*1.团购订单处理*/
			if (ModelConstant.ORDER_TYPE_RGROUP == serviceOrder.getOrderType()) {
				RgroupRule rule = (RgroupRule) salePlanService.getService(serviceOrder.getOrderType()).findSalePlan(serviceOrder.getGroupRuleId());
				if (ModelConstant.RGROUP_STAUS_GROUPING == rule.getGroupStatus()) {
					rule.setCurrentNum(rule.getCurrentNum()-serviceOrder.getCount());
					cacheableService.save(rule);
				}
			}
			/*2.修改订单状态为已退款*/
			serviceOrder.setStatus(ModelConstant.ORDER_STATUS_REFUNDED);
			serviceOrder.setRefundDate(new Date());
			serviceOrderRepository.save(serviceOrder);
			
			/*3.修改已售份数*/
			if (ModelConstant.ORDER_TYPE_SERVICE != serviceOrder.getOrderType()) {
				productService.saledCount(serviceOrder.getProductId(), serviceOrder.getCount()*-1);
				/*4.修改库存*/
				redisTemplate.opsForValue().increment(ModelConstant.KEY_PRO_STOCK + serviceOrder.getProductId(), serviceOrder.getCount());
			}
			
	        log.warn("[finishRefund]refund-saved:"+serviceOrder.getId());
		}else {
			log.warn("could not finishe refund, order status : " + serviceOrder.getStatus());
		}
		log.warn("[finishRefund]refund-begin:"+serviceOrder.getId());
	}
	
	
	/**
	 * 根据ID查询订单
	 */
	public ServiceOrder findOne(long orderId){
		
		ServiceOrder order = new ServiceOrder();
		Optional<ServiceOrder> optional = serviceOrderRepository.findById(orderId);
		if (optional.isPresent()) {
			order = optional.get();
		}
		return order;
	}
	
	/**
	 * 获取订单
	 * @param orderId
	 * @return
	 */
	@Override
	public ServiceOrder getOrder(User user, long orderId) {
		
		ServiceOrder order = findOne(orderId);
		if (order.getId() == 0) {
			List<ServiceOrder> orderList = serviceOrderRepository.findByGroupOrderId(orderId);
			if (!orderList.isEmpty()) {
				order = orderList.get(0);
				BigDecimal totalPrice = BigDecimal.ZERO;
				int totalCount = 0;
				for (ServiceOrder serviceOrder : orderList) {
					totalCount += serviceOrder.getCount();
					totalPrice = totalPrice.add(new BigDecimal(String.valueOf(serviceOrder.getPrice())));
				}
				order.setPrice(totalPrice.floatValue());
				order.setCount(totalCount);
				order.setId(order.getGroupOrderId());
			}
		}
		
		OperatorDefinition operatorDefinition = operatorService.defineOperator(user);
		log.info("orderId : " + order.getId());
		log.info("orderUserId: " + order.getUserId());
		log.info("op : " + operatorDefinition);
		log.info("userId : " + user.getId());
		if (user.getId() != order.getUserId()) {
			if (ModelConstant.ORDER_TYPE_ONSALE == order.getOrderType()) {
				if (!operatorDefinition.isOnsaleTaker()) {
					throw new BizValidateException("当前用户没有权限查看此订单。");
				}
			}else if (ModelConstant.ORDER_TYPE_RGROUP == order.getOrderType()) {
				if (!operatorDefinition.isRgroupTaker()) {
					throw new BizValidateException("当前用户没有权限查看此订单。");
				}
			}
			
		}
		return order;
		
	}
	
	/**
	 * 根据ID查询订单明细
	 */
	@Override
	public List<OrderItem> getOrderDetail(User user, long orderId){
	    
		List<OrderItem> itemList = new ArrayList<>();
		ServiceOrder order = null;
		Optional<ServiceOrder> optional = serviceOrderRepository.findById(orderId);
		if (optional.isPresent()) {
			order = optional.get();
		}
		
		if (order != null) {
			itemList = orderItemRepository.findByServiceOrder(order);
		}else {
			List<ServiceOrder> orderList = serviceOrderRepository.findByGroupOrderId(orderId);
			if (!orderList.isEmpty()) {
				order = orderList.get(0);
				for (ServiceOrder serviceOrder : orderList) {
					List<OrderItem> oList = orderItemRepository.findByServiceOrder(serviceOrder);
					itemList.addAll(oList);
				}
			}
		}
		
		OperatorDefinition operatorDefinition = operatorService.defineOperator(user);
		log.info("getOrderDetail op : " + operatorDefinition);
		if (user.getId() != order.getUserId()) {
			if (ModelConstant.ORDER_TYPE_ONSALE == order.getOrderType()) {
				if (!operatorDefinition.isOnsaleTaker()) {
					throw new BizValidateException("当前用户没有权限查看此订单。");
				}
			}else if (ModelConstant.ORDER_TYPE_RGROUP == order.getOrderType()) {
				if (!operatorDefinition.isRgroupTaker()) {
					throw new BizValidateException("当前用户没有权限查看此订单。");
				}
			}
			
		}
		return itemList;
		
	}
	
	@Transactional
	@Override
	public void cancelPay(User user, String orderId) throws Exception {
		
		Assert.hasText(orderId, "订单ID不能为空。");
		
		ServiceOrder serviceOrder = serviceOrderRepository.findById(Long.valueOf(orderId)).get();
		if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
			throw new BizValidateException("未查询到订单, orderId : " + orderId);
		}
		eshopUtil.cancelPay(user, serviceOrder.getOrderNo());
		
		if (ModelConstant.ORDER_STATUS_INIT == serviceOrder.getStatus()) {	//1.先支付，后完工
			List<OrderItem> list = serviceOrder.getItems();
			orderItemRepository.deleteAll(list);
			serviceOrderRepository.deleteById(serviceOrder.getId());
		}
	}
	
	
	/**
	 * 通知入账
	 * @throws Exception 
	 */
	@Override
	@Transactional
	public void notifyPayByServplat(String tradeWaterId) {
		
		log.info("notifyPayByServplat, tradeWaterId : " + tradeWaterId);
		
		if (StringUtils.isEmpty(tradeWaterId)) {
			return;
		}
		ServiceOrder serviceOrder = serviceOrderRepository.findByOrderNo(tradeWaterId);
		if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
			return;
		}
		log.info("notifyPayByServplat, orderId : " + serviceOrder.getId());
		log.info("notifyPayByServplat, orderType : " + serviceOrder.getOrderType());
		log.info("notifyPayByServplat, orderStatus : " + serviceOrder.getStatus());
		
		
		if (ModelConstant.ORDER_TYPE_EVOUCHER == serviceOrder.getOrderType()) {
			if (ModelConstant.ORDER_STATUS_INIT == serviceOrder.getStatus()) {
				Date date = new Date();
				serviceOrder.setStatus(ModelConstant.ORDER_STATUS_PAYED);
				serviceOrder.setConfirmDate(date);
				serviceOrder.setPayDate(date);
				serviceOrderRepository.save(serviceOrder);
				salePlanService.getService(serviceOrder.getOrderType()).postPaySuccess(serviceOrder);	//修改orderItems
				commonPostProcess(ModelConstant.ORDER_OP_UPDATE_PAYSTATUS, serviceOrder);	//发送模板消息和短信
				evoucherService.enable(serviceOrder);	//激活核销券
			}
		}
		
		if (ModelConstant.ORDER_TYPE_SERVICE == serviceOrder.getOrderType()) {
			
			if (StringUtils.isEmpty(serviceOrder.getPayDate())) {
				if (ModelConstant.ORDER_STATUS_INIT == serviceOrder.getStatus()) {
					//do nothing
				}else if (ModelConstant.ORDER_STATUS_ACCEPTED == serviceOrder.getStatus()) {
					serviceOrder.setStatus(ModelConstant.ORDER_STATUS_PAYED);
				}
				serviceOrder.setPayDate(new Date());
				serviceOrderRepository.save(serviceOrder);
			}
		}
		
	}
	
	/**
	 * 1.验证短信验证码 
	 * 2.创建订单 
	 * 3.支付 
	 * 4.回填tradeWaterId和代理商、合伙人信息
	 * @param promotionOrder
	 * @return
	 * @throws Exception 
	 */
	@Transactional
	@Override
	public JsSign promotionPay(User user, PromotionOrder promotionOrder) throws Exception {
		
		Long templateRuleId = promotionOrder.getRuleId();
		Assert.notNull(promotionOrder.getRuleId(), "规则ID不能为空。");

		if (1003 != promotionOrder.getProductType() && 1004 != promotionOrder.getProductType()) {
			throw new BizValidateException("错误的商品类型 : " + promotionOrder.getProductType());
		}
		
		//1.验证手机
		boolean result = validateMobile(promotionOrder.getMobile(), promotionOrder.getCode());
		if (!result) {
			throw new BizValidateException("验证码不正确。");
		}
		
		//2.创建用户
		createUser(user, promotionOrder.getName(), promotionOrder.getMobile());
		
		/*
		 * 2.创建订单
		 * 1).根据页面填写的内容，先建一个新的地址
		 * 2).新建一个机构(后面分享用)
		 * 3).用新地址创建订单
		 */
		ServiceOrder serviceOrder = null;
		if (1003 == promotionOrder.getProductType()) {
			Address address = createAddress(promotionOrder, user);
			createAgent(promotionOrder.getName(), promotionOrder.getMobile());	//新建机构，以保证当前用户成为合伙人后可以分享订单
			Agent agent = getSharedAgent(promotionOrder.getShareCode());	//获取分享本次订单的机构，有可能是空的
			SingleItemOrder singleItemOrder = new SingleItemOrder();
			singleItemOrder.setCount(1);
			singleItemOrder.setMemo("推广订单");
			singleItemOrder.setOpenId(user.getOpenid());
			singleItemOrder.setOrderType(ModelConstant.ORDER_TYPE_PROMOTION);
			singleItemOrder.setPayType("2");
			singleItemOrder.setRuleId(templateRuleId);
			singleItemOrder.setServiceAddressId(address.getId());
			singleItemOrder.setUserId(user.getId());
			singleItemOrder.setAgentId(agent.getId());
			serviceOrder = createOrder(singleItemOrder);
			
		}else if (1004 == promotionOrder.getProductType()) {
			Address address = createAddress(user);
			SingleItemOrder singleItemOrder = new SingleItemOrder();
			singleItemOrder.setCount(1);
			singleItemOrder.setMemo("saas售卖订单");
			singleItemOrder.setOpenId(user.getOpenid());
			singleItemOrder.setOrderType(ModelConstant.ORDER_TYPE_SAASSALE);
			singleItemOrder.setPayType("2");
			singleItemOrder.setRuleId(templateRuleId);
			singleItemOrder.setServiceAddressId(address.getId());
			singleItemOrder.setUserId(user.getId());
			serviceOrder = createOrder(singleItemOrder);
		}
		return requestPay(serviceOrder);
		
	}
	
	private Address createAddress(User user) {

		Address address = new Address();
		address.setBind(false);
		address.setProvince("上海");
		address.setProvinceId(19l);
		address.setCity("上海市");
		address.setCityId(20l);
		address.setCounty("浦东新区");
		address.setCountyId(34l);
		
		address.setMain(false);
		address.setReceiveName(user.getTel());
		address.setTel(user.getTel());
		address.setUserId(user.getId());
		address.setUserName(user.getTel());
		return addressRepository.save(address);
		
	}

	private Address createAddress(PromotionOrder promotionOrder, User user) {
		
		Address address = new Address();
		address.setBind(false);

		Province province = provinceRepository.findByProvinceId(promotionOrder.getProvince());
		Region rProvince = new Region();
		List<Region> regionList = regionRepository.findByNameAndRegionType(province.getName(), ModelConstant.REGION_PROVINCE);
		if (regionList.isEmpty()) {
			rProvince.setName(province.getName());
			rProvince.setParentId(1l);	//省，写死中国
			rProvince.setParentName("中国");
			rProvince.setRegionType(ModelConstant.REGION_PROVINCE);
			rProvince.setMappingId(province.getProvinceId());
			rProvince.setLatitude(0d);
			rProvince.setLongitude(0d);
			rProvince = regionRepository.save(rProvince);
		}else {
			rProvince = regionList.get(0);
		}
		address.setProvince(rProvince.getName());
		address.setProvinceId(rProvince.getId());
		
		City city = cityRepository.findByCityId(promotionOrder.getCity());
		Region rCity = new Region();
		List<Region> cityList = regionRepository.findByNameAndRegionType(province.getName(), ModelConstant.REGION_CITY);
		if (cityList.isEmpty()) {
			rCity.setName(city.getName());
			rCity.setParentId(rProvince.getId());
			rCity.setParentName(rProvince.getName());
			rCity.setRegionType(ModelConstant.REGION_CITY);
			rCity.setMappingId(city.getCityId());
			rCity.setLatitude(0d);
			rCity.setLongitude(0d);
			rCity = regionRepository.save(rCity);
		}else {
			rCity = cityList.get(0);
		}
		address.setCity(rCity.getName());
		address.setCityId(rCity.getId());
		
		County county = countyRepository.findByCountyId(promotionOrder.getCounty());
		Region rCounty = new Region();
		List<Region> countyList = regionRepository.findByNameAndRegionType(province.getName(), ModelConstant.REGION_COUNTY);
		if (countyList.isEmpty()) {
			rCounty.setName(county.getName());
			rCounty.setParentId(rCity.getId());
			rCounty.setParentName(rCity.getName());
			rCounty.setRegionType(ModelConstant.REGION_COUNTY);
			rCounty.setMappingId(county.getCountyId());
			rCounty.setLatitude(0d);
			rCounty.setLongitude(0d);
			rCounty = regionRepository.save(rCounty);
		}else {
			rCounty = countyList.get(0);
		}
		address.setCounty(rCounty.getName());
		address.setCountyId(rCounty.getId());
		
		
//		Region province = regionRepository.findById(promotionOrder.getProvince()).get();
//		address.setProvince(province.getName());
//		address.setProvinceId(province.getId());
//		
//		Region city = regionRepository.findById(promotionOrder.getCity()).get();
//		address.setCity(city.getName());
//		address.setCityId(city.getId());
//		
//		Region county = regionRepository.findById(promotionOrder.getCounty()).get();
//		address.setCounty(county.getName());
//		address.setCountyId(county.getId());
		
		address.setMain(false);
		address.setReceiveName(promotionOrder.getName());
		address.setTel(promotionOrder.getMobile());
		address.setUserId(user.getId());
		address.setUserName(promotionOrder.getName());
		address.setXiaoquName(promotionOrder.getSectName());
		address = addressRepository.save(address);
		return address;
	}
	
	/**
	 * 验证手机号
	 * @param mobile
	 * @param code
	 * @param name
	 * @return
	 */
	private boolean validateMobile(String mobile, String code) {

		if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(code)) {
			throw new BizValidateException("未填写手机或者验证码信息。");
		}
		boolean result = smsService.checkVerificationCode(mobile, code);
		return result;
	}
	
	private void createUser(User user, String name, String mobile) {
		
		if (!StringUtils.isEmpty(name)) {
			user.setName(name);
		}else {
			if (!StringUtils.isEmpty(mobile)) {
				user.setName(mobile);
			}
		}
		if (!StringUtils.isEmpty(mobile)) {
			user.setTel(mobile);
		}
		
		userService.simpleRegister(user);
	}
	
	private void createAgent(String name, String mobile) {
		
		Agent agent = agentRepository.findByAgentNo(mobile);
		if (agent == null) {
			agent = new Agent();
			agent.setAgentNo(mobile);
			agent.setName(name);
			agent.setStatus(1);
			agent = agentRepository.save(agent);
		}
	}
	
	private Agent getSharedAgent(String shareCode) {
		
		Agent agent = new Agent();
		if (StringUtils.isEmpty(shareCode)) {
			return agent;
		}
		Evoucher evoucher = evoucherService.getEvoucherByCode(shareCode);
		if (evoucher == null) {
			return agent;
		}
		agent = agentRepository.findByAgentNo(evoucher.getAgentNo());
		return agent;
	}

	/**
	 * 查询用户购买过的推广订单
	 */
	@Override
	public List<ServiceOrder> queryPromotionOrder(User user, List<Integer> statusList, List<Integer> typeList) {
		
		List<ServiceOrder> orderList = serviceOrderRepository.findByUserAndStatusAndTypes(user.getId(), statusList, typeList);
		return orderList;
	}

	@Transactional
	@Override
	public JsSign promotionPayV2(User user, PromotionOrder promotionOrder) throws Exception {
		
		Long templateRuleId = promotionOrder.getRuleId();
		Assert.notNull(promotionOrder.getRuleId(), "规则ID不能为空。");

		if (1003 != promotionOrder.getProductType() && 1004 != promotionOrder.getProductType()) {
			throw new BizValidateException("错误的商品类型 : " + promotionOrder.getProductType());
		}
		
		List<Integer> statusList = new ArrayList<>();
		statusList.add(ModelConstant.ORDER_STATUS_PAYED);
		statusList.add(ModelConstant.ORDER_STATUS_REFUNDED);
		
		List<Integer> typeList = new ArrayList<>();
		if (1003 == promotionOrder.getProductType()) {
			typeList.add(ModelConstant.ORDER_TYPE_PROMOTION);
		}else if (1004 == promotionOrder.getProductType()) {
			typeList.add(ModelConstant.ORDER_TYPE_SAASSALE);
		}
		
		User currUser = userService.getById(user.getId());
		List<ServiceOrder> orderList = queryPromotionOrder(currUser, statusList, typeList);
		Long addressId = null;
		if (!orderList.isEmpty()) {
			ServiceOrder paidOrder = orderList.get(0);
			addressId = paidOrder.getServiceAddressId();
		}else {
			List<Address> addrList = addressRepository.findAllByUserId(currUser.getId());
			if (!addrList.isEmpty()) {
				addressId = addrList.get(0).getId();
			}

		}
		createAgent(currUser.getName(), currUser.getTel());	//新建机构，以保证当前用户成为合伙人后可以分享订单
		Agent agent = getSharedAgent(promotionOrder.getShareCode());	//获取分享本次订单的机构，有可能是空的
		
		SingleItemOrder singleItemOrder = new SingleItemOrder();
		singleItemOrder.setCount(1);
		singleItemOrder.setMemo("推广订单");
		if (1004 == promotionOrder.getProductType()) {
			singleItemOrder.setMemo("saas售卖订单");
		}
		singleItemOrder.setOpenId(user.getOpenid());
		singleItemOrder.setOrderType(ModelConstant.ORDER_TYPE_PROMOTION);
		if (1004 == promotionOrder.getProductType()) {
			singleItemOrder.setOrderType(ModelConstant.ORDER_TYPE_SAASSALE);
		}
		singleItemOrder.setPayType("2");
		singleItemOrder.setRuleId(templateRuleId);
		singleItemOrder.setServiceAddressId(addressId);
		singleItemOrder.setUserId(user.getId());
		singleItemOrder.setAgentId(agent.getId());
		ServiceOrder serviceOrder = createOrder(singleItemOrder);
		return requestPay(serviceOrder);
	}
	
	/**
	 * 购物车支付结算页面
	 * @param user
	 * @param req
	 * @return
	 */
	@Override
	public ServiceOrder orderCheck(User user, CreateOrderReq req) {
		
		Map<Long, List<OrderItem>> itemsMap = new HashMap<>();
		
		//重新设置页面传上来的商品价格，因为前端传值可以被篡改。除了规则id和件数采用前端上传的
		List<OrderItem> itemList = req.getItemList();
		if (itemList == null || itemList.isEmpty()) {
			throw new BizValidateException("没有选择任何商品。");
		}
		for (OrderItem orderItem : itemList) {
			String key = ModelConstant.KEY_PRO_RULE_INFO + orderItem.getRuleId();
			ProductRule productRule = redisRepository.getProdcutRule(key);
			if (productRule == null) {
				throw new BizValidateException("未查询到商品规则：" + orderItem.getRuleId());
			}
			
			//只设置单价和免邮件数这些基本属性，以保证后面计算的正确性
			orderItem.setOriPrice(productRule.getOriPrice());
			orderItem.setFreeShippingNum(productRule.getFreeShippingNum());
			orderItem.setPostageFee(productRule.getPostageFee());
			orderItem.setPrice(productRule.getPrice());
			BigDecimal amt = new BigDecimal(String.valueOf(productRule.getPrice())).multiply(new BigDecimal(String.valueOf(orderItem.getCount())));
			orderItem.setAmount(amt.floatValue());
			
			//页面展示用的属性
			orderItem.setProductId(productRule.getProductId());
			orderItem.setProductName(productRule.getName());
			orderItem.setRuleName(productRule.getName());
			orderItem.setProductPic(productRule.getMainPicture());
			orderItem.setProductThumbPic(productRule.getSmallPicture());
			orderItem.setOrderType(productRule.getSalePlanType());
			
			Long agentId = productRule.getAgentId();
			orderItem.setAgentId(productRule.getAgentId());
			
			if (!itemsMap.containsKey(agentId)) {
				List<OrderItem> oList = new ArrayList<>();
				oList.add(orderItem);
				itemsMap.put(agentId, oList);
			} else {
				List<OrderItem> oList = itemsMap.get(agentId);
				oList.add(orderItem);
			}
			
			
			String stock = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_STOCK + productRule.getProductId());
			String freeze = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_FREEZE + productRule.getProductId());
			
			int canSale = Integer.valueOf(stock) - Integer.valueOf(freeze);
			if (canSale <= 0) {
				throw new BizValidateException("抱歉，商品["+productRule.getName()+"]没有库存啦。");
			}
			if (canSale < orderItem.getCount()) {
				throw new BizValidateException("抱歉，商品["+productRule.getName()+"]仅剩"+canSale+"件，请减少购买件数。");
			}

		}
		
		BigDecimal totalAmount = BigDecimal.ZERO;
		BigDecimal shipfee = BigDecimal.ZERO;
		BigDecimal price = BigDecimal.ZERO;
		BigDecimal discount = BigDecimal.ZERO;
		int count = 0;
		long closeTime = System.currentTimeMillis() + 900000;// 默认15分
		
		Iterator<Entry<Long, List<OrderItem>>> it = itemsMap.entrySet().iterator();
		while(it.hasNext()) {

			Entry<Long, List<OrderItem>> entry = it.next();
			CreateOrderReq orderRequest = new CreateOrderReq();
			BeanUtils.copyProperties(req, orderRequest);
			orderRequest.setItemList(entry.getValue());
			ServiceOrder o = new ServiceOrder(user, orderRequest);
			computePrice(o);
			totalAmount = totalAmount.add(new BigDecimal(String.valueOf(o.getTotalAmount())));
			shipfee = shipfee.add(new BigDecimal(String.valueOf(o.getShipFee())));
			price = price.add(new BigDecimal(String.valueOf(o.getPrice())));
			discount = discount.add(new BigDecimal(String.valueOf(o.getDiscountAmount())));
			count += o.getCount();
			
		}
		
		ServiceOrder order = new ServiceOrder(user, req);	//虚拟一个serviceOrder,计算金额用
		order.setTotalAmount(totalAmount.floatValue());
		order.setShipFee(shipfee.floatValue());
		order.setDiscountAmount(discount.floatValue());
		order.setPrice(price.floatValue());
		order.setCloseTime(closeTime);
		order.setCount(count);
		order.setOrderItems(req.getItemList());	//serviceOrder中的items是懒加载，因此一旦hibernate的session关闭，将加载不到items的值。拿到页面序列化的时候会报错
		return order;
		
	}
	
	@Transactional
	@Override
	public void finishOrder(String tradeWaterId) {
		
		log.info("update orderStatus, tradeWaterId : " + tradeWaterId);
		
		ServiceOrder serviceOrder = serviceOrderRepository.findByOrderNo(tradeWaterId);
		if (serviceOrder == null || StringUtils.isEmpty(serviceOrder.getOrderNo())) {
			return;
		}
		log.info("update orderStatus, orderId : " + serviceOrder.getId());
		log.info("update orderStatus, orderType : " + serviceOrder.getOrderType());
		log.info("update orderStatus, orderStatus : " + serviceOrder.getStatus());
		
		if (ModelConstant.ORDER_TYPE_ONSALE == serviceOrder.getOrderType()) {
			List<ServiceOrder> orderList = serviceOrderRepository.findByGroupOrderId(serviceOrder.getGroupOrderId());
			for (ServiceOrder order : orderList) {
				
				if (ModelConstant.ORDER_STATUS_INIT == order.getStatus()) {
					Date date = new Date();
					order.setStatus(ModelConstant.ORDER_STATUS_PAYED);
					order.setConfirmDate(date);
					order.setPayDate(date);
//					serviceOrderRepository.save(order);
					salePlanService.getService(order.getOrderType()).postPaySuccess(order);	//修改orderItems
					
					//发送模板消息和短信
					userNoticeService.orderSuccess(order.getUserId(), order.getTel(),
							order.getId(), order.getOrderNo(), order.getProductName(), order.getPrice());
					//清空购物车中已购买的商品
					List<OrderItem> itemList = orderItemRepository.findByServiceOrder(order);
					cartService.delFromCart(order.getUserId(), itemList);
					//减库存
					for (OrderItem item : itemList) {
						redisTemplate.opsForValue().decrement(ModelConstant.KEY_PRO_STOCK + item.getProductId(), item.getCount());
					}
					
					consumeAndCreateOrderSeed(order);
					
				}
				
			}
		
		}
		
		//核销券、团购、合伙人、saas售卖
		if (ModelConstant.ORDER_TYPE_EVOUCHER == serviceOrder.getOrderType() || 
				ModelConstant.ORDER_TYPE_RGROUP == serviceOrder.getOrderType() ||
				ModelConstant.ORDER_TYPE_PROMOTION == serviceOrder.getOrderType() ||
				ModelConstant.ORDER_TYPE_SAASSALE == serviceOrder.getOrderType()) {
			
			if (ModelConstant.ORDER_STATUS_INIT == serviceOrder.getStatus()) {
				Date date = new Date();
				serviceOrder.setStatus(ModelConstant.ORDER_STATUS_PAYED);
				serviceOrder.setConfirmDate(date);
				serviceOrder.setPayDate(date);
				salePlanService.getService(serviceOrder.getOrderType()).postPaySuccess(serviceOrder);	//修改orderItems
				
				if (ModelConstant.ORDER_TYPE_RGROUP == serviceOrder.getOrderType()) {
					//减库存
					redisTemplate.opsForValue().decrement(ModelConstant.KEY_PRO_STOCK + serviceOrder.getProductId(), serviceOrder.getCount());
				}
				
				if (ModelConstant.ORDER_TYPE_PROMOTION != serviceOrder.getOrderType() && ModelConstant.ORDER_TYPE_SAASSALE != serviceOrder.getOrderType()) {
					//发送模板消息和短信
					userNoticeService.orderSuccess(serviceOrder.getUserId(), serviceOrder.getTel(),
							serviceOrder.getId(), serviceOrder.getOrderNo(), serviceOrder.getProductName(), serviceOrder.getPrice());
					//清空购物车中已购买的商品
					if (ModelConstant.ORDER_TYPE_ONSALE == serviceOrder.getOrderType()) {
						List<OrderItem> itemList = orderItemRepository.findByServiceOrder(serviceOrder);
						cartService.delFromCart(serviceOrder.getUserId(), itemList);
					}
					
				}
				
				if (ModelConstant.ORDER_TYPE_EVOUCHER == serviceOrder.getOrderType() || ModelConstant.ORDER_TYPE_PROMOTION == serviceOrder.getOrderType()) {
					evoucherService.enable(serviceOrder);	//激活核销券
				}
				
				//1.消费优惠券 2.如果配了分裂红包，则创建分裂红包的种子
        		consumeAndCreateOrderSeed(serviceOrder);
				
			}
			
			if (ModelConstant.ORDER_TYPE_PROMOTION == serviceOrder.getOrderType()) {
				Partner partner = new Partner();
				partner.setTel(serviceOrder.getTel());
				partner.setName(serviceOrder.getReceiverName());
				partner.setUserId(serviceOrder.getUserId());
				partnerService.save(partner);
			}
			
			
		}
		
		//服务
		if (ModelConstant.ORDER_TYPE_SERVICE == serviceOrder.getOrderType()) {
			
			if (StringUtils.isEmpty(serviceOrder.getPayDate())) {
				if (ModelConstant.ORDER_STATUS_INIT == serviceOrder.getStatus()) {
					//do nothing
				}else if (ModelConstant.ORDER_STATUS_ACCEPTED == serviceOrder.getStatus()) {
					serviceOrder.setStatus(ModelConstant.ORDER_STATUS_PAYED);
				}
				serviceOrder.setPayDate(new Date());
				serviceOrderRepository.save(serviceOrder);
				
			}
			consumeAndCreateOrderSeed(serviceOrder);
		}
		
	}

	private void consumeAndCreateOrderSeed(ServiceOrder order) {
		//1.消费优惠券 2.如果配了分裂红包，则创建分裂红包的种子
		List<OrderItem> items = orderItemRepository.findByServiceOrder(order);
		if (ModelConstant.ORDER_TYPE_SERVICE == order.getOrderType()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setProductId(order.getProductId());
			items = new ArrayList<>();
			items.add(orderItem);
		}
		order.setItems(items);
		couponService.comsume(order);
		CouponSeed cs = couponService.createOrderSeed(order.getUserId(), order);
		if(cs != null) {
			order.setSeedStr(cs.getSeedStr());
			serviceOrderRepository.save(order);
			if (ModelConstant.COUPON_SEED_ORDER_BUY2 == cs.getSeedType()) {
				User user = userService.getById(order.getUserId());
				try {
					GainCouponDTO dto = couponService.gainCouponFromSeed(user, cs.getSeedStr());
					if (dto.isSuccess()) {
						userNoticeService.couponSuccess(dto.getCoupon());	//发送短信
					} else {
						log.error(dto.getErrMsg());
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}
	
	
}

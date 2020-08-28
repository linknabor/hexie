package com.yumu.hexie.service.sales.impl;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.StringUtil;
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
import com.yumu.hexie.model.market.saleplan.SalePlan;
import com.yumu.hexie.model.payment.PaymentConstant;
import com.yumu.hexie.model.payment.PaymentOrder;
import com.yumu.hexie.model.promotion.coupon.CouponSeed;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.AddressRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.car.CarService;
import com.yumu.hexie.service.comment.CommentService;
import com.yumu.hexie.service.common.ShareService;
import com.yumu.hexie.service.common.SmsService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.common.WechatCoreService;
import com.yumu.hexie.service.eshop.EvoucherService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.payment.PaymentService;
import com.yumu.hexie.service.sales.BaseOrderService;
import com.yumu.hexie.service.sales.ProductService;
import com.yumu.hexie.service.sales.SalePlanService;
import com.yumu.hexie.service.sales.req.PromotionOrder;
import com.yumu.hexie.service.user.UserNoticeService;
import com.yumu.hexie.service.user.UserService;
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
			productService.freezeCount(product, item.getCount());
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
            
			if(StringUtil.isEmpty(order.getProductName())){
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
	
	//创建订单
	@Override
	@Transactional
	public ServiceOrder createOrder(SingleItemOrder order){
		
		//1. 填充地址信息
		ServiceOrder o = new ServiceOrder(order);
		Address address = fillAddressInfo(o);
		//2. 填充订单信息并校验规则,设置价格信息
		preOrderCreate(o, address);
		computeCoupon(o);
		//3. 订单创建
		o = serviceOrderRepository.save(o);
		for(OrderItem item : o.getItems()) {
			item.setServiceOrder(o);
			item.setUserId(o.getUserId());
			orderItemRepository.save(item);
		}
		//4.保存车辆信息 20160721 车大大的车辆服务
		carService.saveOrderCarInfo(o);
		
		//5.电子优惠券订单
		evoucherService.createEvoucher(o);
		
        log.warn("[Create]订单创建OrderId:" + o.getId());
		//4. 订单后处理
		commonPostProcess(ModelConstant.ORDER_OP_CREATE,o);
		
		return o;
	}

	/**
	 * 根据购物车创建订单
	 */
	@Override
	@Transactional
	public ServiceOrder createOrder(User user, CreateOrderReq req, Cart cart){
		
		ServiceOrder o = new ServiceOrder(user, req, cart);
		//1. 填充地址信息
		Address address = fillAddressInfo(o);
		//2. 填充订单信息并校验规则,设置价格信息
		preOrderCreate(o, address);
		computeCoupon(o);
		//3. 订单创建
		o = serviceOrderRepository.save(o);
		for(OrderItem item : o.getItems()) {
			item.setServiceOrder(o);
			item.setUserId(o.getUserId());
			orderItemRepository.save(item);
		}
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
		
		//重新设置页面传上来的商品价格，因为前端传值可以被篡改。除了规则id和件数采用前端上传的
		List<OrderItem> itemList = req.getItemList();
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
		}
		
		ServiceOrder o = new ServiceOrder(user, req);
		//1. 填充地址信息
		Address address = fillAddressInfo(o);
		//2. 填充订单信息并校验规则,设置价格信息
		preOrderCreate(o, address);
		computeCoupon(o);
		//3. 订单创建
		o = serviceOrderRepository.save(o);
		for(OrderItem item : o.getItems()) {
			item.setServiceOrder(o);
			item.setUserId(o.getUserId());
			orderItemRepository.save(item);
		}
		//4.保存车辆信息 20160721 车大大的车辆服务
		carService.saveOrderCarInfo(o);
		
		//5.电子优惠券订单
		evoucherService.createEvoucher(o);
		
        log.warn("[Create]订单创建OrderNo:" + o.getOrderNo());
		//4. 订单后处理
		commonPostProcess(ModelConstant.ORDER_OP_CREATE,o);
		return o;
		
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

	@Transactional
	@Override
	public JsSign requestPay(ServiceOrder order) throws Exception {
		
        log.info("[requestPay]OrderNo:" + order.getId() + ", orderType : " + order.getOrderType());
		//校验订单状态
		if(!order.payable()){
            throw new BizValidateException(order.getId(),"订单状态不可支付，请重新查询确认订单状态！").setError();
        }
		JsSign sign = null;
		//核销券订单走平台支付接口。其余老的订单走原有支付，会慢慢改成新接口
		if (ModelConstant.ORDER_TYPE_EVOUCHER == order.getOrderType() || 
				ModelConstant.ORDER_TYPE_ONSALE == order.getOrderType() ||
				ModelConstant.ORDER_TYPE_RGROUP == order.getOrderType() ||
				ModelConstant.ORDER_TYPE_PROMOTION == order.getOrderType()) {
			
			User user = userService.getById(order.getUserId());
			CommonPayRequest request = new CommonPayRequest();
			request.setUserId(user.getWuyeId());
			request.setAppid(user.getAppId());
			if (ModelConstant.ORDER_TYPE_PROMOTION != order.getOrderType()) {
				request.setSectId(user.getSectId());
			}
			request.setServiceId(String.valueOf(order.getProductId()));
			String linkman = order.getReceiverName();
			if (!StringUtil.isEmpty(linkman)) {
				linkman = URLEncoder.encode(linkman,"GBK");
			}
			request.setLinkman(linkman);
			request.setLinktel(order.getTel());
			
			String address = order.getAddress();
			if (!StringUtil.isEmpty(address)) {
				address = URLEncoder.encode(address,"GBK");
			}
			request.setServiceAddr(address);
			request.setOpenid(order.getOpenId());
			request.setTranAmt(String.valueOf(order.getPrice()));
			request.setTradeWaterId(order.getOrderNo());
			
			String productName = order.getProductName();
			if (!StringUtil.isEmpty(productName)) {
				productName = URLEncoder.encode(productName,"GBK");
			}
			request.setServiceName(productName);
			request.setOrderType(String.valueOf(order.getOrderType()));
			
			Optional<Agent> optional = agentRepository.findById(order.getAgentId());
			if (optional.isPresent()) {
				Agent agent = optional.get();
				request.setAgentNo(agent.getAgentNo());
				String agentName = agent.getName();
				if (!StringUtil.isEmpty(agentName)) {
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
				if (!StringUtil.isEmpty(subAgentName)) {
					subAgentName = URLEncoder.encode(subAgentName,"GBK");
				}
				subOrder.setAgentName(subAgentName);
				subOrder.setAgentNo(orderItem.getAgentNo());
				subOrder.setAmount(orderItem.getAmount());
				subOrder.setCount(orderItem.getCount());
				
				String subProductName = orderItem.getProductName();
				if (!StringUtil.isEmpty(subProductName)) {
					subProductName = URLEncoder.encode(subProductName,"GBK");
				}
				subOrder.setProductName(subProductName);
				subOrder.setProductId(orderItem.getProductId());
				subOrderList.add(subOrder);
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
			if (StringUtil.isEmpty(order.getOrderNo())) {
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
		if (ModelConstant.ORDER_TYPE_EVOUCHER == order.getOrderType() || ModelConstant.ORDER_TYPE_SERVICE == order.getOrderType()) {
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
		//4.商品中取消冻结
		salePlanService.getService(order.getOrderType()).postOrderCancel(order);
        log.warn("[cancelOrder]unfrezee:"+order.getId());
		//5.操作后处理
		commonPostProcess(ModelConstant.ORDER_OP_CANCEL,order);
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
		
		if (ModelConstant.ORDER_TYPE_EVOUCHER == order.getOrderType() || 
				ModelConstant.ORDER_TYPE_ONSALE == order.getOrderType() ||
				ModelConstant.ORDER_TYPE_RGROUP == order.getOrderType()) {
			
			User user = userService.getById(order.getUserId());
			eshopUtil.requestRefund(user, order.getOrderNo());
			order.refunding(true);
			
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
	public ServiceOrder findOne(long orderId){
	    return serviceOrderRepository.findById(orderId).get();
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

		if (1003 != promotionOrder.getProductType()) {
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
		ServiceOrder serviceOrder = createOrder(singleItemOrder);
		return requestPay(serviceOrder);
		
	}

	private Address createAddress(PromotionOrder promotionOrder, User user) {
		
		Address address = new Address();
		address.setBind(false);

		Province province = provinceRepository.findByProvinceId(promotionOrder.getProvince());
		Region rProvince = regionRepository.findByName(province.getName());
		if (rProvince == null) {
			rProvince = new Region();
			rProvince.setName(province.getName());
			rProvince.setParentId(1l);	//省，写死中国
			rProvince.setParentName("中国");
			rProvince.setRegionType(ModelConstant.REGION_PROVINCE);
			rProvince.setMappingId(province.getProvinceId());
			rProvince.setLatitude(0d);
			rProvince.setLongitude(0d);
			rProvince = regionRepository.save(rProvince);
		}
		address.setProvince(rProvince.getName());
		address.setProvinceId(rProvince.getId());
		
		City city = cityRepository.findByCityId(promotionOrder.getCity());
		Region rCity = regionRepository.findByName(city.getName());
		if (rCity == null) {
			rCity = new Region();
			rCity.setName(city.getName());
			rCity.setParentId(rProvince.getId());
			rCity.setParentName(rProvince.getName());
			rCity.setRegionType(ModelConstant.REGION_CITY);
			rCity.setMappingId(city.getCityId());
			rCity.setLatitude(0d);
			rCity.setLongitude(0d);
			rCity = regionRepository.save(rCity);
		}
		address.setCity(rCity.getName());
		address.setCityId(rCity.getId());
		
		County county = countyRepository.findByCountyId(promotionOrder.getCounty());
		Region rCounty = regionRepository.findByName(county.getName());
		if (rCounty == null) {
			rCounty = new Region();
			rCounty.setName(county.getName());
			rCounty.setParentId(rCity.getId());
			rCounty.setParentName(rCity.getName());
			rCounty.setRegionType(ModelConstant.REGION_COUNTY);
			rCounty.setMappingId(county.getCountyId());
			rCounty.setLatitude(0d);
			rCounty.setLongitude(0d);
			rCounty = regionRepository.save(rCounty);
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

		if (StringUtil.isEmpty(mobile) || StringUtil.isEmpty(code)) {
			throw new BizValidateException("未填写手机或者验证码信息。");
		}
		boolean result = smsService.checkVerificationCode(mobile, code);
		return result;
	}
	
	private User createUser(User user, String name, String mobile) {
		
		if (StringUtil.isNotEmpty(name)) {
			user.setName(name);
		}
		if (!StringUtils.isEmpty(mobile)) {
			user.setTel(mobile);
		}
		User savedUser = userService.simpleRegister(user);
		return savedUser;
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
	public Long queryPromotionOrder(User user) {
		
		List<Integer> statusList = new ArrayList<>();
		statusList.add(ModelConstant.ORDER_STATUS_PAYED);
		List<Integer> typeList = new ArrayList<>();
		typeList.add(ModelConstant.ORDER_TYPE_PROMOTION);
		List<ServiceOrder> orderList = serviceOrderRepository.findByUserAndStatusAndTypes(user.getId(), statusList, typeList);
		Long orderId = 0l;
		if (!orderList.isEmpty()) {
			orderId = orderList.get(orderList.size()-1).getId();
		}
		return orderId;
	}
	
	
}

package com.yumu.hexie.web.sales;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.eshop.resp.RgroupRegionsVO;
import com.yumu.hexie.integration.wechat.entity.common.JsSign;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.commonsupport.comment.Comment;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.model.distribution.RgroupAreaItemRepository;
import com.yumu.hexie.model.market.Cart;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.market.saleplan.SalePlan;
import com.yumu.hexie.model.redis.Keys;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.sales.BaseOrderService;
import com.yumu.hexie.service.sales.ProductService;
import com.yumu.hexie.service.sales.RgroupService;
import com.yumu.hexie.service.sales.SalePlanService;
import com.yumu.hexie.service.sales.req.PromotionOrder;
import com.yumu.hexie.service.user.AddressService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.vo.CreateOrderReq;
import com.yumu.hexie.vo.RgroupOrder;
import com.yumu.hexie.vo.SingleItemOrder;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import com.yumu.hexie.web.sales.resp.BuyInfoVO;
import com.yumu.hexie.web.user.resp.UserInfo;

import io.swagger.annotations.ApiOperation;

@Controller(value = "orderController")
public class OrderController extends BaseController{
    @Inject
    private ServiceOrderRepository serviceOrderRepository;
    @Inject
    private ProductService productService;
    @Inject
    private SalePlanService salePlanService;
    @Inject
    private BaseOrderService baseOrderService;
    @Inject
    private RedisRepository redisRepository;
    @Inject
    private RgroupService rgroupService;
	@Inject
	private AddressService addressService;
	@Autowired
	private UserService userService;
	@Autowired
	private RgroupAreaItemRepository rgroupAreaItemRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	@RequestMapping(value = "/getProduct/{productId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<Product> getProduct(@PathVariable long productId) throws Exception {
		return new BaseResult<Product>().success(productService.getProduct(productId));
    }
    
	@RequestMapping(value = "/orders/status/{statusType}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<ServiceOrder>> orders(@ModelAttribute(Constants.USER)User user,@PathVariable String statusType) throws Exception {
		//订单中排除服务单
		List<Integer> types = new ArrayList<>();
		types.add(ModelConstant.ORDER_TYPE_GROUP);
		types.add(ModelConstant.ORDER_TYPE_GROUP_SINGLE);
		types.add(ModelConstant.ORDER_TYPE_ONSALE);
		types.add(ModelConstant.ORDER_TYPE_RGROUP);

		List<Integer> status = new ArrayList<>();
		if("NEEDPAY".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_INIT);
		}else if("NEEDRECEIVE".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_APPLYREFUND);
			status.add(ModelConstant.ORDER_STATUS_SENDED);
		}else if("CANCELD".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_CANCEL);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_BACKEND);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT);
		}else if("PAYED".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_PAYED);
			status.add(ModelConstant.ORDER_STATUS_CONFIRM);
			status.add(ModelConstant.ORDER_STATUS_SENDED);
		}else if("PREPARE".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_CONFIRM);
		}else{
			status.add(ModelConstant.ORDER_STATUS_INIT);
			status.add(ModelConstant.ORDER_STATUS_PAYED);
			status.add(ModelConstant.ORDER_STATUS_CANCEL);
			status.add(ModelConstant.ORDER_STATUS_APPLYREFUND);
			status.add(ModelConstant.ORDER_STATUS_REFUNDING);
			status.add(ModelConstant.ORDER_STATUS_SENDED);
			status.add(ModelConstant.ORDER_STATUS_RECEIVED);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_BACKEND);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT);
			status.add(ModelConstant.ORDER_STATUS_CONFIRM);
			status.add(ModelConstant.ORDER_STATUS_RETURNED);
			status.add(ModelConstant.ORDER_STATUS_REFUNDED);
		} 
		return new BaseResult<List<ServiceOrder>>().success(serviceOrderRepository.findByUserAndStatusAndTypes(user.getId(),status, types));
    }
	@RequestMapping(value = "/orders/status/onsale/{statusType}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<ServiceOrder>> onSaleOrders(@ModelAttribute(Constants.USER)User user,@PathVariable String statusType) throws Exception {
		List<Integer> status = new ArrayList<>();
		if("NEEDPAY".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_INIT);
		}else if("NEEDRECEIVE".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_APPLYREFUND);
		}else if("CANCELD".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_CANCEL);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_BACKEND);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT);
		}else if("PAYED".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_PAYED);
			status.add(ModelConstant.ORDER_STATUS_SENDED);
			status.add(ModelConstant.ORDER_STATUS_CONFIRM);
		}else if("PREPARE".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_CONFIRM);
		}else{
			status.add(ModelConstant.ORDER_STATUS_INIT);
			status.add(ModelConstant.ORDER_STATUS_PAYED);
			status.add(ModelConstant.ORDER_STATUS_CANCEL);
			status.add(ModelConstant.ORDER_STATUS_APPLYREFUND);
			status.add(ModelConstant.ORDER_STATUS_REFUNDING);
			status.add(ModelConstant.ORDER_STATUS_SENDED);
			status.add(ModelConstant.ORDER_STATUS_RECEIVED);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_BACKEND);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT);
			status.add(ModelConstant.ORDER_STATUS_CONFIRM);
			status.add(ModelConstant.ORDER_STATUS_RETURNED);
			status.add(ModelConstant.ORDER_STATUS_REFUNDED);
		} 
		return new BaseResult<List<ServiceOrder>>().success(serviceOrderRepository.findByUserAndStatusAndType(user.getId(),status,ModelConstant.ORDER_TYPE_ONSALE));
    }
	@RequestMapping(value = "/orders/status/group/{statusType}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<RgroupOrder>> groupOrders(@ModelAttribute(Constants.USER)User user,@PathVariable String statusType) throws Exception {
		List<Integer> status = new ArrayList<>();
		if("NEEDPAY".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_INIT);
		}else if("NEEDRECEIVE".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_APPLYREFUND);
			status.add(ModelConstant.ORDER_STATUS_SENDED);
		}else if("CANCELD".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_CANCEL);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_BACKEND);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT);
		}else if("PAYED".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_PAYED);
		}else if("PREPARE".equalsIgnoreCase(statusType)){
			status.add(ModelConstant.ORDER_STATUS_CONFIRM);
		}else{
			status.add(ModelConstant.ORDER_STATUS_INIT);
			status.add(ModelConstant.ORDER_STATUS_PAYED);
			status.add(ModelConstant.ORDER_STATUS_CANCEL);
			status.add(ModelConstant.ORDER_STATUS_APPLYREFUND);
			status.add(ModelConstant.ORDER_STATUS_REFUNDING);
			status.add(ModelConstant.ORDER_STATUS_SENDED);
			status.add(ModelConstant.ORDER_STATUS_RECEIVED);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_BACKEND);
			status.add(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT);
			status.add(ModelConstant.ORDER_STATUS_CONFIRM);
			status.add(ModelConstant.ORDER_STATUS_RETURNED);
			status.add(ModelConstant.ORDER_STATUS_REFUNDED);
		} 
		
		return new BaseResult<List<RgroupOrder>>().success(rgroupService.queryMyRgroupOrders(user.getId(),status));
    }
	
	@RequestMapping(value = "/queryBuyInfo/{type}/{ruleId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<BuyInfoVO> queryBuyInfo(@ModelAttribute(Constants.USER)User user,@PathVariable int type,@PathVariable long ruleId) throws Exception {
		
		User currUser = null;
		if(user != null) {
			currUser = userService.getById(user.getId());
		}
		SalePlan sp = salePlanService.getService(type).findSalePlan(ruleId);
		BuyInfoVO vo = new BuyInfoVO();
		vo.setRule(sp);
		vo.setProduct(productService.getProduct(sp.getProductId()));
		
		Address address = new Address();
		if (currUser != null) {
			if (!StringUtils.isEmpty(currUser.getSectId())&& !"0".equals(currUser.getSectId())) {	//绑定房屋的业主
				List<Address> addrList = addressService.queryRgroupAddressByUser(currUser.getId(), String.valueOf(ruleId));
				if (addrList==null || addrList.size() ==0) {
					addrList = addressService.queryBindedAddressByUser(currUser.getId());
				}
				if (addrList!=null && addrList.size() > 0) {
					address = addrList.get(0);
				}
			}
			vo.setUserInfo(new UserInfo(currUser));
		}
		
		vo.setAddress(address);
//		if (ModelConstant.ORDER_TYPE_RGROUP == type) {
//			List<RgroupAreaItem> areaList = rgroupAreaItemRepository.findByProductIdAndRegionId(sp.getProductId(), address.getXiaoquId());
//			RgroupAreaItem areaItem = new RgroupAreaItem();
//			if (areaList!=null && areaList.size()>0) {
//				areaItem = areaList.get(0);
//				
//			}
//			vo.setRgroupAreaItem(areaItem);
//			
//			areaList = rgroupAreaItemRepository.findByRuleId(ruleId);
//			vo.setAreaItems(areaList);
//			
//		}
		if (ModelConstant.ORDER_TYPE_EVOUCHER == type) {
			if (currUser != null) {
				if (StringUtil.isEmpty(currUser.getSectId()) || "0".equals(currUser.getSectId())) {
					vo.setAddress(new Address());
				}
			}
		}
		return new BaseResult<BuyInfoVO>().success(vo);
    }
	
	/**
	 * 获取可以参加团购的小区和物业信息
	 * @param user
	 * @param ruleId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroup/sect/{ruleId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<RgroupRegionsVO>> queryRgroupSects(@ModelAttribute(Constants.USER)User user, @PathVariable long ruleId) throws Exception {
		
		List<RgroupAreaItem> areaList = rgroupAreaItemRepository.findByRuleId(ruleId);
		List<RgroupRegionsVO> list = addressService.querySectInfo(user, areaList);
		return new BaseResult<List<RgroupRegionsVO>>().success(list);
    }
	
	@RequestMapping(value = "/getOrder/{orderId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<ServiceOrder> getOrder(@ModelAttribute(Constants.USER)User user,@PathVariable long orderId) throws Exception {
		ServiceOrder order = baseOrderService.getOrder(user, orderId);
		return new BaseResult<ServiceOrder>().success(order);
    }

	@ApiOperation(value = "订单支付")
	@RequestMapping(value = "/requestPay/{orderId}", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public BaseResult<JsSign> requestPay(@PathVariable long orderId,@ModelAttribute(Constants.USER)User user) throws Exception {
		
		return new BaseResult<JsSign>().success(baseOrderService.requestOrderPay(user, orderId));
	}
	
	@RequestMapping(value = "/notifyPayed/{orderId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<String> notifyPayed(@PathVariable long orderId,@ModelAttribute(Constants.USER)User user) throws Exception {
		
		logger.info("notifyPayed : " + orderId);
		baseOrderService.notifyPayed(orderId);
		return new BaseResult<String>().success(Constants.PAGE_SUCCESS);
	}
	
	@RequestMapping(value = "/cancelOrder/{orderId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<ServiceOrder> cancelOrder(@PathVariable long orderId,@ModelAttribute(Constants.USER)User user) throws Exception {
		ServiceOrder order = baseOrderService.findOne(orderId);
		if(user.getId() != order.getUserId()) {
			return new BaseResult<ServiceOrder>().failMsg("无法操作他人订单");
		}
		return new BaseResult<ServiceOrder>().success(baseOrderService.cancelOrder(order));
	}
	@RequestMapping(value = "/createOrder", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<ServiceOrder> createOrder(@RequestBody SingleItemOrder sOrder,@ModelAttribute(Constants.USER)User user) throws Exception {
		
		sOrder.setUserId(user.getId());
		sOrder.setOpenId(user.getOpenid());
		
		logger.info("createOrder, singleItemOrder : " + sOrder);
		return new BaseResult<ServiceOrder>().success(baseOrderService.createOrder(sOrder));
	}

	@RequestMapping(value = "/createOrder4Cart", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<ServiceOrder> createOrder4Package(@RequestBody CreateOrderReq req,@ModelAttribute(Constants.USER)User user) throws Exception {
		Cart cart = redisRepository.getCart(Keys.uidCardKey(user.getId()));
		if(cart == null) {
			return new BaseResult<ServiceOrder>().failMsg("购物车为空，请重新选择你需要购买的商品！");
		}
		if(cart.getOrderType() < 0){
			return new BaseResult<ServiceOrder>().failMsg("商品信息获取异常，请重新选择你需要购买的商品！");
		}
		ServiceOrder o = baseOrderService.createOrder(user, req, cart);
		if(o == null) {
			return new BaseResult<ServiceOrder>().failMsg("订单提交失败，请稍后重试！");
		} else {
			redisRepository.removeCart(Keys.uidCardKey(user.getId()));
		}
		return new BaseResult<ServiceOrder>().success(o);
	}
	
	@RequestMapping(value = "/signOrder/{orderId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<ServiceOrder> signOrder(@PathVariable long orderId,@ModelAttribute(Constants.USER)User user) throws Exception {
		ServiceOrder order = baseOrderService.findOne(orderId);
		if(user.getId() != order.getUserId()) {
			return new BaseResult<ServiceOrder>().failMsg("无法操作他人订单");
		}
		return new BaseResult<ServiceOrder>().success(baseOrderService.signOrder(order));
	}
	
	@RequestMapping(value = "/comment", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<String> comment(@RequestBody Comment comment,@ModelAttribute(Constants.USER)User user) throws Exception {
		ServiceOrder order = baseOrderService.findOne(comment.getOrderId());
		if(order == null || user.getId() != order.getUserId()) {
			return new BaseResult<String>().failMsg("无法操作他人订单");
		}
		comment.setUserId(user.getId());
		comment.setUserName(user.getName());
		comment.setUserHeadImg(user.getHeadimgurl());
		baseOrderService.comment(order, comment);
		return new BaseResult<String>().success("评价成功");
	}
	
	/**
	 * 取消唤起支付
	 * @param user
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/order/cancelRequestPay/{orderId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<String> cancelPay(@ModelAttribute(Constants.USER) User user, 
			@PathVariable String orderId) throws Exception {
		
		baseOrderService.cancelPay(user, orderId);
		return BaseResult.successResult(Constants.PAGE_SUCCESS);
	}
	
	/**
	 * 购物车支付页面创建订单
	 * @param user
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "购物车支付页面创建订单")
	@RequestMapping(value = "/createOrderFromCart", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<ServiceOrder> createOrderFromCart(@ModelAttribute(Constants.USER)User user, @RequestBody CreateOrderReq req) throws Exception {
		
		ServiceOrder o = baseOrderService.createOrderFromCart(user, req);
		if(o == null) {
			return new BaseResult<ServiceOrder>().failMsg("订单提交失败，请稍后重试！");
		}
		return new BaseResult<ServiceOrder>().success(o);
	}

	/**
	 * 社区合伙人购买
	 * @param session
	 * @param user
	 * @param promotionOrder
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "社区合伙人购买")
	@RequestMapping(value = "/promotionPay", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<JsSign> promotionPay(HttpSession session, @ModelAttribute(Constants.USER)User user, 
			@RequestBody PromotionOrder promotionOrder) throws Exception {
		
		logger.info("promotionPay : " + promotionOrder);
		JsSign jsSign = baseOrderService.promotionPay(user, promotionOrder);
		session.setAttribute(Constants.USER, user);
		return new BaseResult<JsSign>().success(jsSign);
	}

	/**
	 * saas软件售卖
	 * @param user
	 * @param promotionOrder
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "saas软件售卖")
	@RequestMapping(value = "/promotionPayV2", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<JsSign> promotionPayV2(@ModelAttribute(Constants.USER)User user, @RequestBody PromotionOrder promotionOrder) throws Exception {
		
		logger.info("promotionPayV2 : " + promotionOrder);
		JsSign jsSign = baseOrderService.promotionPayV2(user, promotionOrder);
		return new BaseResult<JsSign>().success(jsSign);
	}

	/**
	 * 查询是否购买过推广商品(有退款的也算)
	 * @param user
	 * @param orderType
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "查询是否购买过推广商品(有退款的也算)")
	@RequestMapping(value = "/queryPromotionOrder", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<Long> queryPromotionOrder(@ModelAttribute(Constants.USER)User user, @RequestParam(required = false) String orderType) throws Exception {
		
		List<Integer> statusList = new ArrayList<>();
		statusList.add(ModelConstant.ORDER_STATUS_PAYED);
		statusList.add(ModelConstant.ORDER_STATUS_REFUNDED);
		
		List<Integer> typeList = new ArrayList<>();
		if (StringUtil.isEmpty(orderType)) {
			typeList.add(ModelConstant.ORDER_TYPE_PROMOTION);
		}else if ("99".equals(orderType)) {
			typeList.add(ModelConstant.ORDER_TYPE_PROMOTION);
			typeList.add(ModelConstant.ORDER_TYPE_SAASSALE);
		}else {
			Integer type = Integer.valueOf(orderType);
			typeList.add(type);
		}
		List<ServiceOrder> orderList = baseOrderService.queryPromotionOrder(user, statusList, typeList);
		long orderId = 0L;
		if (!orderList.isEmpty()) {
			for (ServiceOrder serviceOrder : orderList) {
				if (serviceOrder.getStatus() == ModelConstant.EVOUCHER_STATUS_NORMAL) {
					orderId = serviceOrder.getId();
					break;
				}
			}
			if (orderId == 0L) {
				orderId = orderList.get(0).getId();
			}
		}
		
		return new BaseResult<Long>().success(orderId);
	}
	
	/**
	 * 订单结算也页面
	 * @param user
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/order/check", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<ServiceOrder> orderCheck(@ModelAttribute(Constants.USER)User user, @RequestBody CreateOrderReq req)throws Exception {
		
		ServiceOrder serviceOrder = baseOrderService.orderCheck(user, req);
		return new BaseResult<ServiceOrder>().success(serviceOrder);
	}
	
	@RequestMapping(value = "/getOrderDetail/{orderId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<OrderItem>> getOrderDetail(@ModelAttribute(Constants.USER)User user, @PathVariable long orderId) throws Exception {
		
		List<OrderItem> itemList = baseOrderService.getOrderDetail(user, orderId);
		return new BaseResult<List<OrderItem>>().success(itemList);
    }
	
	/**
	 * 购物车支付页面创建订单
	 * @param user
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "团购创建订单")
	@RequestMapping(value = "/rgroup/v3/order/create", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<ServiceOrder> createOrder4Rgroup(@ModelAttribute(Constants.USER)User user, @RequestBody CreateOrderReq req) throws Exception {
		
		ServiceOrder o = baseOrderService.createOrder4Rgoup(user, req);
		if(o == null) {
			return new BaseResult<ServiceOrder>().failMsg("订单提交失败，请稍后重试！");
		}
		return new BaseResult<ServiceOrder>().success(o);
	}
	
}

package com.yumu.hexie.service.sales.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.market.Collocation;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.OrderItemRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.market.saleplan.SalePlan;
import com.yumu.hexie.model.promotion.PromotionConstant;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.sales.CollocationService;
import com.yumu.hexie.service.sales.SalePlanService;
import com.yumu.hexie.service.user.AddressService;
import com.yumu.hexie.service.user.CouponService;
import com.yumu.hexie.service.user.dto.CheckCouponDTO;

public abstract class BaseOrderProcessor {
	@Inject
	protected CollocationService collocationService;
	@Inject
	protected AddressService addressService;
	@Inject 
	protected CouponService couponService;
	@Inject 
    protected SalePlanService salePlanService;
	@Autowired
	protected ServiceOrderRepository serviceOrderRepository;
	@Autowired
	protected OrderItemRepository orderItemRepository;
	

    protected SalePlan findSalePlan(int type, long ruleId){
        return salePlanService.getService(type).findSalePlan(ruleId);
    }

	// 计算总价 运费 折扣 支付价格 最后支付时间 （不包含）
	protected void computePrice(ServiceOrder order) {
		
		BigDecimal totalAmount = BigDecimal.ZERO;
		BigDecimal discount = BigDecimal.ZERO;
		BigDecimal shipfee = BigDecimal.ZERO;
		BigDecimal price = BigDecimal.ZERO;
		int count = 0;
		long closeTime = System.currentTimeMillis() + 900000;// 默认15分
		// 设定价格运费等
		if (order.getCollocationId() > 0) {
			// 1.优惠组合
			// 算总价
			for (OrderItem item : order.getItems()) {
				totalAmount = totalAmount.add(new BigDecimal(String.valueOf(item.getAmount())));
				count += item.getCount();
			}
			price = price.add(totalAmount);
			Collocation c = collocationService.findOne(order.getCollocationId());
			BigDecimal discountTimes = price.divide(new BigDecimal(String.valueOf(c.getSatisfyAmount())));
			discountTimes = discount.setScale(0, RoundingMode.DOWN);
			
			// 算优惠
			discount = new BigDecimal(String.valueOf(c.getDiscountAmount())).multiply(discountTimes);
			price = price.subtract(discount);
			// 算邮费
			BigDecimal freeShipAmount = new BigDecimal(String.valueOf(c.getFreeShipAmount()));
			if (freeShipAmount.compareTo(BigDecimal.ZERO) == 0 || 
					freeShipAmount.subtract(price).compareTo(BigDecimal.ZERO) > 0) {
				shipfee = new BigDecimal(String.valueOf(c.getShipAmount()));
			}
			price = price.add(shipfee);
			closeTime = c.getTimeoutForPay() + System.currentTimeMillis();
			
		} else if (order.getItems().size() == 1) {
			// 2.单个商品
			OrderItem item = order.getItems().get(0);
			SalePlan plan = findSalePlan(order.getOrderType(), item.getRuleId());

			totalAmount = new BigDecimal(String.valueOf(item.getAmount()));
			price = totalAmount;
			if (item.getCount() < plan.getFreeShippingNum()) {
				shipfee = new BigDecimal(String.valueOf(plan.getPostageFee()));
			}
			count += item.getCount();
			price = price.add(shipfee);
			closeTime = plan.getTimeoutForPay() + System.currentTimeMillis();
			
		} else {
			//3.没有优惠组合的情况下，多件商品一起购买
			// 算总价
			long minTimeout = 0l;
			BigDecimal itemShipFee = BigDecimal.ZERO;
			for (OrderItem item : order.getItems()) {
				totalAmount = totalAmount.add(new BigDecimal(String.valueOf(item.getAmount())));
				count += item.getCount();
				SalePlan plan = findSalePlan(order.getOrderType(), item.getRuleId());
				if (minTimeout == 0l) {
					minTimeout = plan.getTimeoutForPay();
				}else if (minTimeout > plan.getTimeoutForPay()) {
					minTimeout = plan.getTimeoutForPay();
				}
				if (item.getCount() < plan.getFreeShippingNum()) {
					BigDecimal unitShipFee = new BigDecimal(String.valueOf(plan.getPostageFee()));
					itemShipFee = unitShipFee.multiply(new BigDecimal(item.getCount()));
				}else {
					itemShipFee = BigDecimal.ZERO;
				}
				shipfee = shipfee.add(itemShipFee);
			}
			price = price.add(totalAmount);
			price = price.add(shipfee);
			
		}
		order.setTotalAmount(totalAmount.floatValue());
		order.setShipFee(shipfee.floatValue());
		order.setDiscountAmount(discount.floatValue());
		order.setPrice(price.floatValue());
		order.setCloseTime(closeTime);
		order.setCount(count);
	}

	/**
	 * 计算红包减免后的订单金额，并且锁定红包
	 * @param order
	 */
	protected boolean computeCoupon(ServiceOrder order) {
		if(order.getCouponId() == null || order.getCouponId() ==0) {
			return false;
		}
		Coupon coupon = couponService.findOne(order.getCouponId());
		if(coupon == null) {
		    return false;
		}
		
		if (ModelConstant.ORDER_TYPE_SERVICE != order.getOrderType()) {
			
			if(!couponService.checkAvailable4Sales(order, coupon, false)){
				return false;
			}
			order.configCoupon(coupon);
			couponService.lock(order, coupon);
		}
		return true;
	}
	
	/**
	 * 拆分订单的操作
	 * @param groupOrderId
	 * @return
	 */
	protected boolean computeCoupon4GroupOrders(long groupOrderId) {
		
		List<ServiceOrder> orderList = serviceOrderRepository.findByGroupOrderId(groupOrderId);
		
		Long couponId = null;
		if (!orderList.isEmpty()) {
			couponId = orderList.get(0).getCouponId();
		}
		if (couponId == null || couponId == 0) {
			return false;
		}
		Coupon coupon = couponService.findById(couponId);
		if (coupon == null) {
			return false;
		}
		
		BigDecimal couponAmount = new BigDecimal(String.valueOf(coupon.getAmount()));	//红包总金额
		BigDecimal couponUsedItemAmount = BigDecimal.ZERO;	//商品总金额 （不含运费）
		
		Map<Long, OrderItem> couponUsedItemMap = new HashMap<>();	//用了红包的item, key:item的id,value: item
		Map<Long, ServiceOrder> orderMap = new HashMap<>();	//key: orderId, value: serviceOrder
		Map<Long, ServiceOrder> itemOrderMap = new HashMap<>();	//key:itemId, value: orderId
		List<Long> couponUsedList = new ArrayList<>();
		for (ServiceOrder serviceOrder : orderList) {
			
			List<OrderItem> itemList = orderItemRepository.findByServiceOrder(serviceOrder);
			serviceOrder.setItems(itemList);
			orderMap.put(serviceOrder.getId(), serviceOrder);
			
			for (OrderItem orderItem : itemList) {
				Product product = new Product();
				product.setId(orderItem.getProductId());
				product.setAgentId(orderItem.getAgentId());
				
				CheckCouponDTO check = couponService.checkAvailable4Service(PromotionConstant.COUPON_ITEM_TYPE_MARKET, product, orderItem.getAmount(), coupon, false); //单个商品都可用
				if (check.isValid()) {
					couponUsedItemMap.put(orderItem.getId(), orderItem);
					couponUsedItemAmount = couponUsedItemAmount.add(new BigDecimal(String.valueOf(orderItem.getAmount())));	//会在这个基础商进行红包分摊，而不是根据总交易金额分摊。并非所有商品都可以使用红包
					itemOrderMap.put(orderItem.getId(), serviceOrder);
				}
			}
			
		}
		BigDecimal remaiderCouponAmount = couponAmount;	//剩余未分摊的红包
		Iterator<Entry<Long, OrderItem>> it = couponUsedItemMap.entrySet().iterator();
		int seq = 0;
		OrderItem lastItem = null;
		while(it.hasNext()) {
			
			Entry<Long, OrderItem> entry = it.next();
			OrderItem orderItem = entry.getValue();
			if (couponUsedItemMap.entrySet().size()-1 == seq) {
				lastItem = orderItem;
			}
			BigDecimal itemAmount = new BigDecimal(String.valueOf(orderItem.getAmount()));
			BigDecimal ratio = itemAmount.divide(couponUsedItemAmount, 4, RoundingMode.HALF_UP);	//先保留四位
			BigDecimal unitCouponAmount = couponAmount.multiply(ratio).setScale(2, RoundingMode.HALF_UP);	//每个item摊得的红包费用
			remaiderCouponAmount = remaiderCouponAmount.subtract(unitCouponAmount);	//未分摊的金额
			if (couponUsedItemMap.entrySet().size()-1 == seq && remaiderCouponAmount.compareTo(BigDecimal.ZERO) != 0) {
				unitCouponAmount = unitCouponAmount.add(remaiderCouponAmount);	//最后一个item，所有余额都分摊进去
			}
			BigDecimal unitItemAmount = itemAmount.subtract(unitCouponAmount);
			if (unitItemAmount.compareTo(BigDecimal.ZERO) <=0) {
				unitItemAmount = BigDecimal.ZERO;
			}
			orderItem.setCouponId(couponId);
			orderItem.setCouponAmount(unitCouponAmount.floatValue());
			orderItem.setPrice(unitItemAmount.floatValue());
			
			ServiceOrder order = itemOrderMap.get(orderItem.getId());
			
			BigDecimal orderCoupouAmount = BigDecimal.ZERO;
			if (!StringUtils.isEmpty(order.getCouponAmount())) {
				orderCoupouAmount = new BigDecimal(String.valueOf(order.getCouponAmount()));
			}
			orderCoupouAmount = orderCoupouAmount.add(unitCouponAmount);
			
			BigDecimal orderPrice = new BigDecimal(String.valueOf(order.getPrice()));
			orderPrice = orderPrice.subtract(orderCoupouAmount);
			if (orderPrice.compareTo(BigDecimal.ZERO) < 0) {
				orderPrice = BigDecimal.ZERO;
			}
			order.setCouponId(couponId);
			order.setCouponAmount(orderCoupouAmount.floatValue());
			order.setPrice(orderPrice.floatValue());
			
			orderItemRepository.save(orderItem);
			serviceOrderRepository.save(order);
			
			if (!couponUsedList.contains(order.getId())) {
				couponUsedList.add(order.getId());
			}
			
			seq++;
		}
		
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (ServiceOrder serviceOrder : orderList) {
			totalPrice = totalPrice.add(new BigDecimal(String.valueOf(serviceOrder.getPrice())));
		}
		
		if (totalPrice.compareTo(BigDecimal.ZERO) <=0 ) {
			totalPrice = new BigDecimal("0.01");
			ServiceOrder lastOrder = orderList.get(orderList.size()-1);
			lastOrder.setPrice(totalPrice.floatValue());
			serviceOrderRepository.save(lastOrder);
			
			lastItem.setPrice(totalPrice.floatValue());
			orderItemRepository.save(lastItem);
		}
		
		ServiceOrder lockOrder = orderMap.get(couponUsedList.get(0));
		couponService.lock(lockOrder, coupon);
		return true;
	}
	
	protected Address fillAddressInfo(ServiceOrder o) {
		Address address = addressService.queryAddressById(o.getServiceAddressId());
		if (address == null) {
			throw new BizValidateException("请选择可用地址！");
		}
		o.fillAddressInfo(address);
		return address;
	}

}

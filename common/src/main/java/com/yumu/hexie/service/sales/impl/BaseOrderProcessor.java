package com.yumu.hexie.service.sales.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.inject.Inject;

import com.yumu.hexie.model.market.Collocation;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.saleplan.SalePlan;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.sales.CollocationService;
import com.yumu.hexie.service.sales.SalePlanService;
import com.yumu.hexie.service.user.AddressService;
import com.yumu.hexie.service.user.CouponService;

public abstract class BaseOrderProcessor {
	@Inject
	protected CollocationService collocationService;
	@Inject
	protected AddressService addressService;
	@Inject 
	protected CouponService couponService;
	@Inject 
    protected SalePlanService salePlanService;
	

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

	protected void computeCoupon(ServiceOrder order) {
		if(order.getCouponId() == null || order.getCouponId() ==0) {
			return;
		}
		Coupon coupon = couponService.findOne(order.getCouponId());
		if(coupon == null) {
		    return;
		}
		if(!couponService.isAvaible(order, coupon, false)){
			throw new BizValidateException("该现金券已被其它订单锁定或不可使用，请选择其它可用现金券"+coupon.getId());
		}
		order.configCoupon(coupon);
		couponService.lock(order, coupon);
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

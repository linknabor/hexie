package com.yumu.hexie.service.coupon.impl;

import org.springframework.stereotype.Service;

import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.coupon.CouponStrategy;

@Service(value = "noCouponStrategy")
public class NoCouponStrategy implements CouponStrategy {

	@Override
	public void sendCoupon(User user) {
		
	}

}

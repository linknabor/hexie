package com.yumu.hexie.service.coupon.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.coupon.CouponAsyncTask;
import com.yumu.hexie.service.coupon.CouponStrategy;

/**
 * 贵州注册类红包
 * @author david
 *
 */
@Service
public class GzRegisterCouponStrategy implements CouponStrategy {
	
	private Logger logger = LoggerFactory.getLogger(GzRegisterCouponStrategy.class);

	@Autowired
	private CouponAsyncTask couponAsyncTask;
	
	@Override
	public void sendCoupon(User user) {
		user.setCouponCount(1);	//新进注册，红包数量+1
		logger.info("set coupon count");
		couponAsyncTask.sendCouponAsync(user);
	}
	
	

}

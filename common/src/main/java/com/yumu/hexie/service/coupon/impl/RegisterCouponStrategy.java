package com.yumu.hexie.service.coupon.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.promotion.coupon.CouponSeed;
import com.yumu.hexie.model.promotion.coupon.CouponSeedRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.coupon.CouponStrategy;
import com.yumu.hexie.service.user.CouponService;

/**
 * 注册类红包
 * @author david
 *
 */
@Service(value = "registerCouponStrategy")
public class RegisterCouponStrategy implements CouponStrategy {
	
	private Logger logger = LoggerFactory.getLogger(RegisterCouponStrategy.class);

	@Autowired
	private CouponService couponService;
	@Autowired
	private CouponSeedRepository couponSeedRepository;
	@Autowired
	private GotongService gotongService;
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void sendCoupon(User user) {
		
		List<CouponSeed> seedList = couponSeedRepository.findBySeedTypeAndStatusAndAppid(ModelConstant.COUPON_SEED_USER_REGIST, 
				ModelConstant.COUPON_SEED_STATUS_AVAILABLE, user.getAppId());
		if (seedList == null || seedList.isEmpty()) {
			return;
		}
		if (seedList.size() > 1) {
			logger.error("活动对应多个种子，请检查优惠券种子配置。 appId : " + user.getAppId());
		}
		CouponSeed couponSeed = seedList.get(0);
		Coupon coupon = couponService.addCouponFromSeed(couponSeed, user);
		if (coupon != null) {
			gotongService.sendRegiserMsg(user);
		}
		user.setCouponCount(1);	//新进注册，红包数量+1
	}
	
	

}

package com.yumu.hexie.service.coupon;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.promotion.coupon.CouponSeed;
import com.yumu.hexie.model.promotion.coupon.CouponSeedRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.user.CouponService;

@Component
public class CouponAsyncTask {
	
	private Logger logger = LoggerFactory.getLogger(CouponAsyncTask.class);
	
	@Autowired
	private CouponService couponService;
	@Autowired
	private CouponSeedRepository couponSeedRepository;
	@Autowired
	private GotongService gotongService;

	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void sendCouponAsync(User user) {
		
		logger.info("send coupon ");
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
	}
}

package com.yumu.hexie.service.coupon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;

@Component
public class CouponStrategyFactory {

	@Autowired
	private SystemConfigService systemConfigService;
	@Autowired
	@Qualifier(value = "registerCouponStrategy")
	private CouponStrategy registerCouponStrategy;
	@Autowired
	@Qualifier(value = "noCouponStrategy")
	private CouponStrategy NoCouponStrategy;
	
	/**
	 * 注册类优惠券策略
	 */
	public CouponStrategy getRegisterStrategy(User user) {
		
		if (systemConfigService.registerCouponServiceAvailabe(user.getAppId())) {
			return registerCouponStrategy;
		}
		//有其他策略再加 else if 里面写，这样调用接口的地方代码不需要变动，只要增加接口就行了
		else {
			return NoCouponStrategy;	//什么都不发的策略
		}
	}
	
	
}

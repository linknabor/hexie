package com.yumu.hexie.service.coupon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;

@Component
public class CouponStrategyFactory {

	@Autowired
	private SystemConfigService systemConfigService;
	@Autowired
	private CouponStrategy gzRegisterCouponStrategy;
	@Autowired
	private CouponStrategy commonRegisterCouponStrategy;
	
	/**
	 * 注册类优惠券策略
	 */
	public CouponStrategy getRegisterStrategy(User user) {
		
		if (systemConfigService.registerCouponServiceAvailabe(user.getAppId())) {
			return gzRegisterCouponStrategy;
		}
		//有其他策略再加 else if 里面写，这样调用接口的地方代码不需要变动，只要增加接口就行了
		else {
			return commonRegisterCouponStrategy;
		}
	}
	
	
}

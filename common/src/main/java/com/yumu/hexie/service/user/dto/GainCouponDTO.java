package com.yumu.hexie.service.user.dto;

import com.yumu.hexie.model.promotion.coupon.Coupon;

public class GainCouponDTO {

	private Coupon coupon;
	private boolean success = false;
	private String errMsg;
	
	public Coupon getCoupon() {
		return coupon;
	}
	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	@Override
	public String toString() {
		return "GainCouponDTO [coupon=" + coupon + ", success=" + success + ", errMsg=" + errMsg + "]";
	}
	
	
}

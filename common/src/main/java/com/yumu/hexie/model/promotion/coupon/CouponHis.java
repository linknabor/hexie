package com.yumu.hexie.model.promotion.coupon;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

/**
 * 优惠券领取历史记录
 * @author david
 *
 */
@Entity
public class CouponHis extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1592539750629280315L;
	
	private long userId;
	private String userName;
	private long couponId;
	private String title;
	private String seedStr;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public long getCouponId() {
		return couponId;
	}
	public void setCouponId(long couponId) {
		this.couponId = couponId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSeedStr() {
		return seedStr;
	}
	public void setSeedStr(String seedStr) {
		this.seedStr = seedStr;
	}

	
}

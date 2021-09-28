package com.yumu.hexie.model.card.dto;

import com.yumu.hexie.model.user.User;

public class EventSubscribeDTO {
	
	private String cardId;	//卡券ID
	private String getCardUrl;	//领取地址
	private String couponId;	//红包礼券
	private String getCouponUrl;	//领取地址
	
	private User user;	//用户信息
	
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getGetCardUrl() {
		return getCardUrl;
	}
	public void setGetCardUrl(String getCardUrl) {
		this.getCardUrl = getCardUrl;
	}
	public String getCouponId() {
		return couponId;
	}
	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}
	public String getGetCouponUrl() {
		return getCouponUrl;
	}
	public void setGetCouponUrl(String getCouponUrl) {
		this.getCouponUrl = getCouponUrl;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
}

package com.yumu.hexie.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SingleItemOrder implements Serializable {

	private static final long serialVersionUID = 825143360521751425L;
	private long ruleId;
	private int count = 1;
	private int orderType;
	private long serviceAddressId;//FIXME 服务地址
	private int receiveTimeType;//周一至周五、周六周日、全周
	private String memo;
	private Long couponId;
	@JsonIgnore
	private String openId;
	private Long userId;
	private String payType;	//1.走公众号原来的支付，2走平台的支付
	private Long agentId;
	
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getOrderType() {
		return orderType;
	}
	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}
	public long getServiceAddressId() {
		return serviceAddressId;
	}
	public void setServiceAddressId(long serviceAddressId) {
		this.serviceAddressId = serviceAddressId;
	}
	public int getReceiveTimeType() {
		return receiveTimeType;
	}
	public void setReceiveTimeType(int receiveTimeType) {
		this.receiveTimeType = receiveTimeType;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public Long getAgentId() {
		return agentId;
	}
	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}
	@Override
	public String toString() {
		return "SingleItemOrder [ruleId=" + ruleId + ", count=" + count + ", orderType=" + orderType
				+ ", serviceAddressId=" + serviceAddressId + ", receiveTimeType=" + receiveTimeType + ", memo=" + memo
				+ ", couponId=" + couponId + ", openId=" + openId + ", userId=" + userId + ", payType=" + payType
				+ ", agentId=" + agentId + "]";
	}
	
}

package com.yumu.hexie.vo;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.model.market.OrderItem;

public class CreateOrderReq implements Serializable{
	
	private static final long serialVersionUID = -2090643413772467559L;
	private Long couponId;
	private long serviceAddressId;//FIXME 服务地址
	private int receiveTimeType;//周一至周五、周六周日、全周
	private String memo;
	private String payType;
	private int orderType;	//订单类型
	private List<OrderItem> itemList;
	
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
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
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public List<OrderItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<OrderItem> itemList) {
		this.itemList = itemList;
	}
	public int getOrderType() {
		return orderType;
	}
	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}
	@Override
	public String toString() {
		return "CreateOrderReq [couponId=" + couponId + ", serviceAddressId=" + serviceAddressId + ", receiveTimeType="
				+ receiveTimeType + ", memo=" + memo + ", payType=" + payType + ", orderType=" + orderType
				+ ", itemList=" + itemList + "]";
	}
	
	
}

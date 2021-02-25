package com.yumu.hexie.integration.wechat.entity.subscribemsg;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *	新订单通知
 * @author david
 *
 */
public class OrderNotificationVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4204997398402248550L;
	
	@JsonProperty("thing3")
	private SubscribeItem orderType;	//订单类型
	@JsonProperty("time5")
	private SubscribeItem createDate;	//下单时间
	@JsonProperty("thing7")
	private SubscribeItem receiver;	//收货人
	@JsonProperty("thing6")
	private SubscribeItem recvAddr;	//收获地址
	public SubscribeItem getOrderType() {
		return orderType;
	}
	public void setOrderType(SubscribeItem orderType) {
		this.orderType = orderType;
	}
	public SubscribeItem getCreateDate() {
		return createDate;
	}
	public void setCreateDate(SubscribeItem createDate) {
		this.createDate = createDate;
	}
	public SubscribeItem getReceiver() {
		return receiver;
	}
	public void setReceiver(SubscribeItem receiver) {
		this.receiver = receiver;
	}
	public SubscribeItem getRecvAddr() {
		return recvAddr;
	}
	public void setRecvAddr(SubscribeItem recvAddr) {
		this.recvAddr = recvAddr;
	}
	@Override
	public String toString() {
		return "OrderNotification [orderType=" + orderType + ", createDate=" + createDate + ", receiver=" + receiver
				+ ", recvAddr=" + recvAddr + "]";
	}
	
	

}

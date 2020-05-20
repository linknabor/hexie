package com.yumu.hexie.integration.wuye.dto;

import java.io.Serializable;

import com.yumu.hexie.model.user.User;

public class PayNotifyDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2053345074969873725L;
	
	private User user;
	
	private String openid;	//用户openid
	private int msgType;	//消息类型	

	private String orderId;	//订单ID
	private String payMethod;	//支付方式
	private String tranDateTime;	//yyyy-MM-dd hh:mm:ss
	private String tranAmt;	//交易金额
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	public String getTranDateTime() {
		return tranDateTime;
	}
	public void setTranDateTime(String tranDateTime) {
		this.tranDateTime = tranDateTime;
	}
	public String getTranAmt() {
		return tranAmt;
	}
	public void setTranAmt(String tranAmt) {
		this.tranAmt = tranAmt;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return "PayNotifyDTO [openid=" + openid + ", msgType=" + msgType + ", orderId=" + orderId + ", payMethod="
				+ payMethod + ", tranDateTime=" + tranDateTime + ", tranAmt=" + tranAmt + "]";
	}
	

}

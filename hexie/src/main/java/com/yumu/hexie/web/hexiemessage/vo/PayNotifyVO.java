package com.yumu.hexie.web.hexiemessage.vo;

import java.io.Serializable;

public class PayNotifyVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4106752720419008039L;

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
	@Override
	public String toString() {
		return "PayNotifyVO [openid=" + openid + ", msgType=" + msgType + ", orderId=" + orderId + ", payMethod="
				+ payMethod + ", tranDateTime=" + tranDateTime + ", tranAmt=" + tranAmt + "]";
	}
	
	
}

package com.yumu.hexie.integration.wechat.entity.subscribemsg;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 到账通知
 * @author david
 *
 */
public class ArrivalNotificationVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7390087650969060825L;

	@JsonProperty("thing2")
	private SubscribeItem payMethod;	//支付方式
	@JsonProperty("time1")
	private SubscribeItem tranDate;	//交易日期
	@JsonProperty("amount3")
	private SubscribeItem amount;	//交易金额
	@JsonProperty("thing4")
	private SubscribeItem remark;	//备注
	
	public SubscribeItem getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(SubscribeItem payMethod) {
		this.payMethod = payMethod;
	}
	public SubscribeItem getTranDate() {
		return tranDate;
	}
	public void setTranDate(SubscribeItem tranDate) {
		this.tranDate = tranDate;
	}
	public SubscribeItem getAmount() {
		return amount;
	}
	public void setAmount(SubscribeItem amount) {
		this.amount = amount;
	}
	public SubscribeItem getRemark() {
		return remark;
	}
	public void setRemark(SubscribeItem remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		return "ArrivalNotification [payMethod=" + payMethod + ", tranDate=" + tranDate + ", amount=" + amount
				+ ", remark=" + remark + "]";
	}
	
}

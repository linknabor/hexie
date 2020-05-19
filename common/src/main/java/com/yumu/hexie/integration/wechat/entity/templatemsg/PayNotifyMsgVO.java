package com.yumu.hexie.integration.wechat.entity.templatemsg;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PayNotifyMsgVO {

	@JsonProperty("first")
	private TemplateItem title;
	
	@JsonProperty("keyword1")
	private TemplateItem tranAmt;	//交易金额
	
	@JsonProperty("keyword2")
	private TemplateItem payMethod;	//支付方式
	
	@JsonProperty("keyword3")
	private TemplateItem tranDateTime;	//支付时间,yyyy-MM-dddd hh:mm:ss
	
	@JsonProperty("keyword4")
	private TemplateItem orderId;	//订单编号

	private TemplateItem remark;	//备注

	public TemplateItem getTitle() {
		return title;
	}

	public void setTitle(TemplateItem title) {
		this.title = title;
	}

	public TemplateItem getTranAmt() {
		return tranAmt;
	}

	public void setTranAmt(TemplateItem tranAmt) {
		this.tranAmt = tranAmt;
	}

	public TemplateItem getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(TemplateItem payMethod) {
		this.payMethod = payMethod;
	}

	public TemplateItem getTranDateTime() {
		return tranDateTime;
	}

	public void setTranDateTime(TemplateItem tranDateTime) {
		this.tranDateTime = tranDateTime;
	}

	public TemplateItem getOrderId() {
		return orderId;
	}

	public void setOrderId(TemplateItem orderId) {
		this.orderId = orderId;
	}

	public TemplateItem getRemark() {
		return remark;
	}

	public void setRemark(TemplateItem remark) {
		this.remark = remark;
	}
	
	
}

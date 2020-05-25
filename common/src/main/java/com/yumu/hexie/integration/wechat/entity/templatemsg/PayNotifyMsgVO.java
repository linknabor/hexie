package com.yumu.hexie.integration.wechat.entity.templatemsg;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PayNotifyMsgVO {

	@JsonProperty("first")
	private TemplateItem title;
	
	@JsonProperty("keyword1")
	private TemplateItem tranType;	//交易类型
	
	@JsonProperty("keyword2")
	private TemplateItem payMethod;	//交易方式
	
	@JsonProperty("keyword3")
	private TemplateItem tranAmt;	//交易金额
	
	@JsonProperty("keyword4")
	private TemplateItem tranDateTime;	//支付时间,yyyy-MM-dddd hh:mm:ss
	
	private TemplateItem remark;	//备注

	public TemplateItem getTitle() {
		return title;
	}

	public void setTitle(TemplateItem title) {
		this.title = title;
	}

	public TemplateItem getTranType() {
		return tranType;
	}

	public void setTranType(TemplateItem tranType) {
		this.tranType = tranType;
	}

	public TemplateItem getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(TemplateItem payMethod) {
		this.payMethod = payMethod;
	}

	public TemplateItem getTranAmt() {
		return tranAmt;
	}

	public void setTranAmt(TemplateItem tranAmt) {
		this.tranAmt = tranAmt;
	}

	public TemplateItem getTranDateTime() {
		return tranDateTime;
	}

	public void setTranDateTime(TemplateItem tranDateTime) {
		this.tranDateTime = tranDateTime;
	}

	public TemplateItem getRemark() {
		return remark;
	}

	public void setRemark(TemplateItem remark) {
		this.remark = remark;
	}

	
	
}

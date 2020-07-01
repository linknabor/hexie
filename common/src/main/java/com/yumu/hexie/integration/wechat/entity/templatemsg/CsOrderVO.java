package com.yumu.hexie.integration.wechat.entity.templatemsg;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CsOrderVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1436234330809393770L;

	@JsonProperty("first")
	private TemplateItem title;
	
	@JsonProperty("keyword1")
	private TemplateItem orderId;
	
	@JsonProperty("keyword2")
	private TemplateItem serviceType;
	
	@JsonProperty("keyword3")
	private TemplateItem customerName;
	
	@JsonProperty("keyword4")
	private TemplateItem customerTel;
	
	@JsonProperty("remark")
	private TemplateItem remark;

	public TemplateItem getTitle() {
		return title;
	}

	public void setTitle(TemplateItem title) {
		this.title = title;
	}

	public TemplateItem getOrderId() {
		return orderId;
	}

	public void setOrderId(TemplateItem orderId) {
		this.orderId = orderId;
	}

	public TemplateItem getServiceType() {
		return serviceType;
	}

	public void setServiceType(TemplateItem serviceType) {
		this.serviceType = serviceType;
	}

	public TemplateItem getCustomerName() {
		return customerName;
	}

	public void setCustomerName(TemplateItem customerName) {
		this.customerName = customerName;
	}

	public TemplateItem getCustomerTel() {
		return customerTel;
	}

	public void setCustomerTel(TemplateItem customerTel) {
		this.customerTel = customerTel;
	}

	public TemplateItem getRemark() {
		return remark;
	}

	public void setRemark(TemplateItem remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "CsOrderVO [title=" + title + ", orderId=" + orderId + ", serviceType=" + serviceType + ", customerName="
				+ customerName + ", customerTel=" + customerTel + ", remark=" + remark + "]";
	}
	
	
}

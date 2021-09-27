package com.yumu.hexie.integration.notify;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.model.user.User;

public class InvoiceNotification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7934245648248376679L;

	@JsonProperty("trade_water_id")
	private String orderId;	//交易流水号
	@JsonProperty("shop_name")
	private String shopName;	//商户名称
	@JsonProperty("invoice_title")
	private String invoiceTitle;	//发票抬头
	@JsonProperty("invoice_type")
	private String invoiceType;	//类型
	@JsonProperty("js_amt")
	private String jsAmt;	//开票金额
	@JsonProperty("make_date")
	private String makeDate;	//开票时间
	@JsonProperty("pdf_addr")
	private String pdfAddr;	//发票地址
	@JsonProperty("apply_type")
	private String applyType;	//正常开票或者红冲;0正数1负数
	
	private String applied;	//是否已经申请。一般公众号交易无须申请。0否1是
	private String openid;	//用户openid
	private String timestamp;	//时间戳
	
	private User user;
	
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getInvoiceTitle() {
		return invoiceTitle;
	}
	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}
	public String getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}
	public String getJsAmt() {
		return jsAmt;
	}
	public void setJsAmt(String jsAmt) {
		this.jsAmt = jsAmt;
	}
	public String getMakeDate() {
		return makeDate;
	}
	public void setMakeDate(String makeDate) {
		this.makeDate = makeDate;
	}
	public String getPdfAddr() {
		return pdfAddr;
	}
	public void setPdfAddr(String pdfAddr) {
		this.pdfAddr = pdfAddr;
	}
	public String getApplyType() {
		return applyType;
	}
	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getApplied() {
		return applied;
	}
	public void setApplied(String applied) {
		this.applied = applied;
	}
	@Override
	public String toString() {
		return "InvoiceNotification [orderId=" + orderId + ", shopName=" + shopName + ", invoiceTitle=" + invoiceTitle
				+ ", invoiceType=" + invoiceType + ", jsAmt=" + jsAmt + ", makeDate=" + makeDate + ", pdfAddr="
				+ pdfAddr + ", applyType=" + applyType + ", applied=" + applied + ", openid=" + openid + ", timestamp="
				+ timestamp + ", user=" + user + "]";
	}
	
	
}

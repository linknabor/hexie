package com.yumu.hexie.web.shequ.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiscountViewReqVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5671013066707711652L;

	private String billId;
	private String stmtId;
	private String payType;		//0微信,1卡
	private String payFeeType;	//01管理费，02停车费
	private String regionName;	//定位地区
	
	/*2020-07 添加 begin*/
	private String isQrcode;
	
	@JsonProperty("invoice_title_type")
	private String invoiceTitleType;
	@JsonProperty("invoice_title")
	private String invoiceTitle;
	@JsonProperty("credit_code")
	private String creditCode;
	/*2020-07 添加 end*/
	
	public String getBillId() {
		return billId;
	}
	public void setBillId(String billId) {
		this.billId = billId;
	}
	public String getStmtId() {
		return stmtId;
	}
	public void setStmtId(String stmtId) {
		this.stmtId = stmtId;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getPayFeeType() {
		return payFeeType;
	}
	public void setPayFeeType(String payFeeType) {
		this.payFeeType = payFeeType;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public String getIsQrcode() {
		return isQrcode;
	}
	public void setIsQrcode(String isQrcode) {
		this.isQrcode = isQrcode;
	}
	public String getInvoiceTitleType() {
		return invoiceTitleType;
	}
	public void setInvoiceTitleType(String invoiceTitleType) {
		this.invoiceTitleType = invoiceTitleType;
	}
	public String getInvoiceTitle() {
		return invoiceTitle;
	}
	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}
	public String getCreditCode() {
		return creditCode;
	}
	public void setCreditCode(String creditCode) {
		this.creditCode = creditCode;
	}
	@Override
	public String toString() {
		return "DiscountViewReqVO [billId=" + billId + ", stmtId=" + stmtId + ", payType=" + payType + ", payFeeType="
				+ payFeeType + ", regionName=" + regionName + ", isQrcode=" + isQrcode + ", invoiceTitleType="
				+ invoiceTitleType + ", invoiceTitle=" + invoiceTitle + ", creditCode=" + creditCode + "]";
	}
	
	
	
}

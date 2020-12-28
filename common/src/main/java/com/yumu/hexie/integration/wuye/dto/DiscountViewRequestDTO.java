package com.yumu.hexie.integration.wuye.dto;

import java.io.Serializable;

import com.yumu.hexie.model.user.User;

public class DiscountViewRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3747001718763164995L;

	private User user;
	private String billId;
	private String stmtId;
	private String payType;		//0微信,1卡
	private String payFeeType;	//01管理费，02停车费
	private String regionName;	//定位地区
	
	/*2020-07 添加 begin*/
	private String isQrcode;
	private String invoiceTitleType;
	private String invoiceTitle;
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
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
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
		return "DiscountViewRequestDTO [user=" + user + ", billId=" + billId + ", stmtId=" + stmtId + ", payType="
				+ payType + ", payFeeType=" + payFeeType + ", regionName=" + regionName + ", isQrcode=" + isQrcode
				+ ", invoiceTitleType=" + invoiceTitleType + ", invoiceTitle=" + invoiceTitle + ", creditCode="
				+ creditCode + "]";
	}
	
	
	
}

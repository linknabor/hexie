package com.yumu.hexie.service.mpqrcode.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateMpQrCodeReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6477343821038992864L;
	
	@JsonProperty("trade_water_id")
	private String orderId;
	@JsonProperty("tran_amt")
	private String tranAmt;
	@JsonProperty("shop_name")
	private String shopName;
	private String appid;
	
	private String type;	//01或空 代表电子发票，02表示电子凭证
	@JsonProperty("pay_method")
	private String payMethod;
	@JsonProperty("fee_name")
	private String feeName;
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getTranAmt() {
		return tranAmt;
	}
	public void setTranAmt(String tranAmt) {
		this.tranAmt = tranAmt;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFeeName() {
		return feeName;
	}
	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}
	@Override
	public String toString() {
		return "CreateMpQrCodeReq [orderId=" + orderId + ", tranAmt=" + tranAmt + ", shopName=" + shopName + ", appid="
				+ appid + ", type=" + type + ", payMethod=" + payMethod + ", feeName=" + feeName + "]";
	}
	
	
}

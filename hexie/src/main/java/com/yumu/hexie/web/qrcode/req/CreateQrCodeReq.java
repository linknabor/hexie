package com.yumu.hexie.web.qrcode.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateQrCodeReq implements Serializable {

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
	@Override
	public String toString() {
		return "CreateQrCodeReq [orderId=" + orderId + ", tranAmt=" + tranAmt + ", shopName=" + shopName + "]";
	}
	

}

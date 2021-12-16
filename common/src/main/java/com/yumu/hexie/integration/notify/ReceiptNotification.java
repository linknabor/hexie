package com.yumu.hexie.integration.notify;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReceiptNotification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1865280157823301650L;
	
	@JsonProperty("application_id")
	private String applicatoinId;
	@JsonProperty("apply_date")
	private String applyDate;
	private String appid;
	private String openid;
	@JsonProperty("trade_water_id")
	private String tradeWaterId;
	@JsonProperty("receipt_id")
	private String receiptId;
	@JsonProperty("tran_amt")
	private String tranAmt;
	@JsonProperty("shop_name")
	private String shopName;
	private String applied;	//是否公众号交易 0否1是
	private String timestamp;	//生成时间戳
	
	public String getApplicatoinId() {
		return applicatoinId;
	}
	public void setApplicatoinId(String applicatoinId) {
		this.applicatoinId = applicatoinId;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getTradeWaterId() {
		return tradeWaterId;
	}
	public void setTradeWaterId(String tradeWaterId) {
		this.tradeWaterId = tradeWaterId;
	}
	public String getReceiptId() {
		return receiptId;
	}
	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
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
	public String getApplied() {
		return applied;
	}
	public void setApplied(String applied) {
		this.applied = applied;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		return "ReceiptNotification [applicatoinId=" + applicatoinId + ", applyDate=" + applyDate + ", appid=" + appid
				+ ", openid=" + openid + ", tradeWaterId=" + tradeWaterId + ", receiptId=" + receiptId + ", tranAmt="
				+ tranAmt + ", shopName=" + shopName + ", applied=" + applied + ", timestamp=" + timestamp + "]";
	}
	
	
}

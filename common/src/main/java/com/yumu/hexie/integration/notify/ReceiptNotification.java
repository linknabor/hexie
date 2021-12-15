package com.yumu.hexie.integration.notify;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReceiptNotification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1865280157823301650L;
	
	@JsonProperty("receipt_id")
	private String receiptId;
	@JsonProperty("tran_amt")
	private String tranAmt;
	@JsonProperty("create_date")
	private String createDate;
	private String sys;
	
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
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getSys() {
		return sys;
	}
	public void setSys(String sys) {
		this.sys = sys;
	}
	@Override
	public String toString() {
		return "ReceiptNotification [receiptId=" + receiptId + ", tranAmt=" + tranAmt + ", createDate=" + createDate
				+ ", sys=" + sys + "]";
	}
	
}

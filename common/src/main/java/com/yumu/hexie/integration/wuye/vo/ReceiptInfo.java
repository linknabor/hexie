package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReceiptInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1000713745875534311L;
	/**
	 * 
	 */
	
	private Receipt receipt;
	private ReceiptDetail[]receiptDetail;
	
	public static class Receipt {
		
		@JsonProperty("receipt_id")
		private String receiptId;
		@JsonProperty("receipt_status")
		private String receiptStatus;
		@JsonProperty("trade_water_id")
		private String tradeWaterId;
		@JsonProperty("sect_name")
		private String sectName;
		@JsonProperty("csp_name")
		private String cspName;
		@JsonProperty("tran_date")
		private String tranDate;
		@JsonProperty("tran_time")
		private String tranTime;
		@JsonProperty("pay_method")
		private String payMethod;
		@JsonProperty("tran_amt")
		private String tranAmt;
		private String openid;
		@JsonProperty("trash_reason")
		private String trashReason;
		
		public String getReceiptId() {
			return receiptId;
		}
		public void setReceiptId(String receiptId) {
			this.receiptId = receiptId;
		}
		public String getReceiptStatus() {
			return receiptStatus;
		}
		public void setReceiptStatus(String receiptStatus) {
			this.receiptStatus = receiptStatus;
		}
		public String getTradeWaterId() {
			return tradeWaterId;
		}
		public void setTradeWaterId(String tradeWaterId) {
			this.tradeWaterId = tradeWaterId;
		}
		public String getSectName() {
			return sectName;
		}
		public void setSectName(String sectName) {
			this.sectName = sectName;
		}
		public String getCspName() {
			return cspName;
		}
		public void setCspName(String cspName) {
			this.cspName = cspName;
		}
		public String getTranDate() {
			return tranDate;
		}
		public void setTranDate(String tranDate) {
			this.tranDate = tranDate;
		}
		public String getTranTime() {
			return tranTime;
		}
		public void setTranTime(String tranTime) {
			this.tranTime = tranTime;
		}
		public String getPayMethod() {
			return payMethod;
		}
		public void setPayMethod(String payMethod) {
			this.payMethod = payMethod;
		}
		public String getTranAmt() {
			return tranAmt;
		}
		public void setTranAmt(String tranAmt) {
			this.tranAmt = tranAmt;
		}
		public String getOpenid() {
			return openid;
		}
		public void setOpenid(String openid) {
			this.openid = openid;
		}
		public String getTrashReason() {
			return trashReason;
		}
		public void setTrashReason(String trashReason) {
			this.trashReason = trashReason;
		}
		@Override
		public String toString() {
			return "Receipt [receiptId=" + receiptId + ", receiptStatus=" + receiptStatus + ", tradeWaterId="
					+ tradeWaterId + ", sectName=" + sectName + ", cspName=" + cspName + ", tranDate=" + tranDate
					+ ", tranTime=" + tranTime + ", payMethod=" + payMethod + ", tranAmt=" + tranAmt + ", openid="
					+ openid + ", trashReason=" + trashReason + "]";
		}
		
		
	}
	
	public static class ReceiptDetail {
		
		@JsonProperty("pay_subject")
		private String paySubject; 
		@JsonProperty("tran_amt")
		private String tranAmt;
		@JsonProperty("fee_name")
		private String feeName;
		@JsonProperty("fee_description")
		private String feeDescription;
		
		public String getPaySubject() {
			return paySubject;
		}
		public void setPaySubject(String paySubject) {
			this.paySubject = paySubject;
		}
		public String getTranAmt() {
			return tranAmt;
		}
		public void setTranAmt(String tranAmt) {
			this.tranAmt = tranAmt;
		}
		public String getFeeName() {
			return feeName;
		}
		public void setFeeName(String feeName) {
			this.feeName = feeName;
		}
		public String getFeeDescription() {
			return feeDescription;
		}
		public void setFeeDescription(String feeDescription) {
			this.feeDescription = feeDescription;
		}
		@Override
		public String toString() {
			return "ReceiptDetail [paySubject=" + paySubject + ", tranAmt=" + tranAmt + ", feeName=" + feeName
					+ ", feeDescription=" + feeDescription + "]";
		}
		
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	public ReceiptDetail[] getReceiptDetail() {
		return receiptDetail;
	}

	public void setReceiptDetail(ReceiptDetail[] receiptDetail) {
		this.receiptDetail = receiptDetail;
	}

	@Override
	public String toString() {
		return "ReceiptNotification [receipt=" + receipt + ", receiptDetail=" + Arrays.toString(receiptDetail) + "]";
	}
	
	
}

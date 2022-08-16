package com.yumu.hexie.vo;

import java.io.Serializable;
import java.util.List;

public class RefundVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3848131415323164075L;
	
	private long orderId;
	private Integer refundType;
	private String refundReason;
	private String memo;
	private String refundAmt;
	private List<String> pictures;
	private List<String> itemList;	//orderItem的id
	
	public Integer getRefundType() {
		return refundType;
	}
	public void setRefundType(Integer refundType) {
		this.refundType = refundType;
	}
	public String getRefundReason() {
		return refundReason;
	}
	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public List<String> getPictures() {
		return pictures;
	}
	public void setPictures(List<String> pictures) {
		this.pictures = pictures;
	}
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public List<String> getItemList() {
		return itemList;
	}
	public void setItemList(List<String> itemList) {
		this.itemList = itemList;
	}
	public String getRefundAmt() {
		return refundAmt;
	}
	public void setRefundAmt(String refundAmt) {
		this.refundAmt = refundAmt;
	}
	@Override
	public String toString() {
		return "RefundVO [orderId=" + orderId + ", refundType=" + refundType + ", refundReason=" + refundReason
				+ ", memo=" + memo + ", refundAmt=" + refundAmt + ", pictures=" + pictures + ", itemList=" + itemList
				+ "]";
	}
	
	
}

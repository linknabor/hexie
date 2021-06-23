package com.yumu.hexie.integration.wuye.dto;

import com.yumu.hexie.model.user.User;

public class OtherPayDTO {
	
	private User user;
	private String money;
	private String sectId;
	private String feeId;
	private String remark;
	private String qrCodeId;
	private String mngCellId;
	private String payee_openid;
	private String orderId;
	private String orderDetail;

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getFeeId() {
		return feeId;
	}
	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getQrCodeId() {
		return qrCodeId;
	}
	public void setQrCodeId(String qrCodeId) {
		this.qrCodeId = qrCodeId;
	}
	public String getMngCellId() {
		return mngCellId;
	}
	public void setMngCellId(String mngCellId) {
		this.mngCellId = mngCellId;
	}

	public String getPayee_openid() {
		return payee_openid;
	}

	public void setPayee_openid(String payee_openid) {
		this.payee_openid = payee_openid;
	}

	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderDetail() {
		return orderDetail;
	}
	public void setOrderDetail(String orderDetail) {
		this.orderDetail = orderDetail;
	}

	@Override
	public String toString() {
		return "OtherPayDTO [user=" + user + ", money=" + money + ", sectId=" + sectId + ", feeId=" + feeId
				+ ", remark=" + remark + ", qrCodeId=" + qrCodeId + ", mngCellId=" + mngCellId + ", orderId=" + orderId
				+ ", orderDetail=" + orderDetail + "]";
	}
	
}

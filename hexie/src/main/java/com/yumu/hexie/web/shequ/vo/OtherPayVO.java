package com.yumu.hexie.web.shequ.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OtherPayVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5770501102521970718L;
	
	private String money;
	@JsonProperty("sect_id")
	private String sectId;
	@JsonProperty("fee_id")
	private String feeId;
	private String openid;
	private String remark;
	private String appid;
	@JsonProperty("qrcode_id")
	private String qrCodeId;
	@JsonProperty("mng_cell_id")
	private String mngCellId;
	@JsonProperty("real_appid")
	private String realAppid;
	private String payee_openid;
	@JsonProperty("order_id")
	private String orderId;
	@JsonProperty("order_detail")
	private String orderDetail;

	private String invoice_type;

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
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
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
	public String getRealAppid() {
		return realAppid;
	}
	public void setRealAppid(String realAppid) {
		this.realAppid = realAppid;
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

	public String getInvoice_type() {
		return invoice_type;
	}

	public void setInvoice_type(String invoice_type) {
		this.invoice_type = invoice_type;
	}

	@Override
	public String toString() {
		return "OtherPayVO{" +
				"money='" + money + '\'' +
				", sectId='" + sectId + '\'' +
				", feeId='" + feeId + '\'' +
				", openid='" + openid + '\'' +
				", remark='" + remark + '\'' +
				", appid='" + appid + '\'' +
				", qrCodeId='" + qrCodeId + '\'' +
				", mngCellId='" + mngCellId + '\'' +
				", realAppid='" + realAppid + '\'' +
				", payee_openid='" + payee_openid + '\'' +
				", orderId='" + orderId + '\'' +
				", orderDetail='" + orderDetail + '\'' +
				", invoice_type='" + invoice_type + '\'' +
				'}';
	}
}

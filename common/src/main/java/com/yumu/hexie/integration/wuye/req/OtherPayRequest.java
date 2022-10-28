package com.yumu.hexie.integration.wuye.req;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.integration.wuye.dto.OtherPayDTO;
import com.yumu.hexie.service.exception.BizValidateException;

public class OtherPayRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7292708879420556618L;
	
	private String appid;
	private String openid;
	private String money;
	@JsonProperty("sect_id")
	private String sectId;
	@JsonProperty("fee_id")
	private String feeId;
	private String remark;
	@JsonProperty("qrcode_id")
	private String qrCodeId;
	@JsonProperty("mng_cell_id")
	private String mngCellId;
	private String payee_openid;
	@JsonProperty("order_id")
	private String orderId;
	@JsonProperty("order_detail")
	private String orderDetail;
	private String invoice_type;
	@JsonProperty("sms_batch")
	private String smsBatch;
	@JsonProperty("start_date")
	private String startDate;
	@JsonProperty("end_date")
	private String endDate;
	private String invoice_title_type;
	
	public OtherPayRequest(OtherPayDTO otherPayDTO) {
		BeanUtils.copyProperties(otherPayDTO, this);
		this.openid =  otherPayDTO.getUser().getOpenid();
		this.appid = otherPayDTO.getUser().getAppId();
		if (!StringUtils.isEmpty(otherPayDTO.getRemark())) {
			try {
				//中文打码
				this.remark = URLEncoder.encode(otherPayDTO.getRemark(),"GBK");
			} catch (UnsupportedEncodingException e) {
				throw new BizValidateException(e.getMessage(), e);	
			}
		}
	}
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
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
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPayee_openid() {
		return payee_openid;
	}

	public void setPayee_openid(String payee_openid) {
		this.payee_openid = payee_openid;
	}

	public String getOrderDetail() {
		return orderDetail;
	}

	public String getSmsBatch() {
		return smsBatch;
	}

	public void setSmsBatch(String smsBatch) {
		this.smsBatch = smsBatch;
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

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getInvoice_title_type() {
		return invoice_title_type;
	}

	public void setInvoice_title_type(String invoice_title_type) {
		this.invoice_title_type = invoice_title_type;
	}

	@Override
	public String toString() {
		return "OtherPayRequest{" +
				"appid='" + appid + '\'' +
				", openid='" + openid + '\'' +
				", money='" + money + '\'' +
				", sectId='" + sectId + '\'' +
				", feeId='" + feeId + '\'' +
				", remark='" + remark + '\'' +
				", qrCodeId='" + qrCodeId + '\'' +
				", mngCellId='" + mngCellId + '\'' +
				", payee_openid='" + payee_openid + '\'' +
				", orderId='" + orderId + '\'' +
				", orderDetail='" + orderDetail + '\'' +
				", invoice_type='" + invoice_type + '\'' +
				", smsBatch='" + smsBatch + '\'' +
				", startDate='" + startDate + '\'' +
				", endDate='" + endDate + '\'' +
				", invoice_title_type='" + invoice_title_type + '\'' +
				'}';
	}
}

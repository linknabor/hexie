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
	@JsonProperty("order_id")
	private String orderId;
	@JsonProperty("order_detail")
	private String orderDetail;
	
	public OtherPayRequest(OtherPayDTO otherPayDTO) {
		BeanUtils.copyProperties(otherPayDTO, this);
		this.openid =  otherPayDTO.getUser().getOpenid();
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

	@Override
	public String toString() {
		return "OtherPayRequest [openid=" + openid + ", money=" + money + ", sectId=" + sectId + ", feeId=" + feeId
				+ ", remark=" + remark + ", qrCodeId=" + qrCodeId + ", mngCellId=" + mngCellId + ", orderId=" + orderId
				+ "]";
	}
	
	
}

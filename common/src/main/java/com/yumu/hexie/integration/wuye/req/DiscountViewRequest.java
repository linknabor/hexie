package com.yumu.hexie.integration.wuye.req;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.integration.wuye.dto.DiscountViewRequestDTO;

public class DiscountViewRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6102273680557624524L;
	
	@JsonProperty("user_id")
	private String wuyeId;
	@JsonProperty("bill_id")
	private String billId;
	@JsonProperty("stmt_id")
	private String stmtId;
	@JsonProperty("pay_type")
	private String payType;		//0微信,1卡
	@JsonProperty("pay_fee_type")
	private String payFeeType;		//01管理费，02停车费
	private String appid;
	
	public DiscountViewRequest() {
		super();
	}
	public DiscountViewRequest(DiscountViewRequestDTO discountViewRequestDTO) {
		
		BeanUtils.copyProperties(discountViewRequestDTO, this);
		this.wuyeId = discountViewRequestDTO.getUser().getWuyeId();
		this.appid = discountViewRequestDTO.getUser().getAppId();
	}
	public String getBillId() {
		return billId;
	}
	public void setBillId(String billId) {
		this.billId = billId;
	}
	public String getStmtId() {
		return stmtId;
	}
	public void setStmtId(String stmtId) {
		this.stmtId = stmtId;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getPayFeeType() {
		return payFeeType;
	}
	public void setPayFeeType(String payFeeType) {
		this.payFeeType = payFeeType;
	}
	public String getWuyeId() {
		return wuyeId;
	}
	public void setWuyeId(String wuyeId) {
		this.wuyeId = wuyeId;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	@Override
	public String toString() {
		return "DiscountViewRequest [wuyeId=" + wuyeId + ", billId=" + billId + ", stmtId=" + stmtId + ", payType="
				+ payType + ", payFeeType=" + payFeeType + ", appid=" + appid + "]";

	}
	
	

}

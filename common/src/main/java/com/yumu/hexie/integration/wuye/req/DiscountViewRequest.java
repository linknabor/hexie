package com.yumu.hexie.integration.wuye.req;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.integration.wuye.dto.DiscountViewRequestDTO;

public class DiscountViewRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6102273680557624524L;
	
	@JsonProperty("bill_id")
	private String billId;
	@JsonProperty("stmt_id")
	private String stmtId;
	@JsonProperty("pay_method")
	private String payMethod;		//0微信,1卡
	@JsonProperty("pay_fee_type")
	private String payFeeType;		//01管理费，02停车费
	private String startDate;
	private String endDate;
	@JsonProperty("house_id")
	private String houseId;			//房屋ID
	
	public DiscountViewRequest() {
		super();
	}
	public DiscountViewRequest(DiscountViewRequestDTO discountViewRequestDTO) {
		
		BeanUtils.copyProperties(discountViewRequestDTO, this);
		this.payMethod = discountViewRequestDTO.getPayType();
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
	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	public String getPayFeeType() {
		return payFeeType;
	}
	public void setPayFeeType(String payFeeType) {
		this.payFeeType = payFeeType;
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
	public String getHouseId() {
		return houseId;
	}
	public void setHouseId(String houseId) {
		this.houseId = houseId;
	}
	
	

}

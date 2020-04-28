package com.yumu.hexie.web.shequ.vo;

import java.io.Serializable;

public class DiscountViewReqVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5671013066707711652L;

	private String billId;
	private String stmtId;
	private String payType;		//0微信,1卡
	private String payFeeType;	//01管理费，02停车费
	private String startDate;
	private String endDate;
	private String houseId;		//房屋ID
	private String regionName;	//定位地区
	
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
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	
	
	
}

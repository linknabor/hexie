package com.yumu.hexie.integration.wuye.dto;

import java.io.Serializable;

import com.yumu.hexie.model.user.User;

public class DiscountViewRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3747001718763164995L;

	private User user;
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
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
	
}

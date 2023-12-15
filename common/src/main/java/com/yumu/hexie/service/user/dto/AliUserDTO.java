package com.yumu.hexie.service.user.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AliUserDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3020758718156754169L;
	
	private String appid;	//支付宝appid
	@JsonProperty("user_id")
	private String userId;	//支付宝用户唯一标识
	@JsonProperty("cell_id")
	private String cellId;	//缴费房屋ID
	private String mobile;	//用户手机号
	
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getCellId() {
		return cellId;
	}
	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@Override
	public String toString() {
		return "AlipayH5LoginReq [appid=" + appid + ", userId=" + userId + ", cellId=" + cellId + ", mobile=" + mobile
				+ "]";
	}
	
}

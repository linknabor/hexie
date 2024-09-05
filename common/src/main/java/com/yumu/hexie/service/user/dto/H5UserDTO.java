package com.yumu.hexie.service.user.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class H5UserDTO implements Serializable {

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
	@JsonProperty("au_id")
	private String auId;	//房地局系统用户id
	@JsonProperty("client_type")
	private String clientType;	//终端类型,1支付宝用户，2微信用户
	@JsonProperty("hou_no")
	private String houNo;	//户号
	private String from;	//来自那个平台,_shwy或者_lifepay
	
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
	public String getAuId() {
		return auId;
	}
	public void setAuId(String auId) {
		this.auId = auId;
	}
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	public String getHouNo() {
		return houNo;
	}
	public void setHouNo(String houNo) {
		this.houNo = houNo;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	@Override
	public String toString() {
		return "H5UserDTO [appid=" + appid + ", userId=" + userId + ", cellId=" + cellId + ", mobile=" + mobile
				+ ", auId=" + auId + ", clientType=" + clientType + ", houNo=" + houNo + ", from=" + from + "]";
	}
	
}

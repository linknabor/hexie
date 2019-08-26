package com.eshequ.hexie.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VerifyTicket implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1725980341126314032L;

	@JsonProperty("AppId")
	private String appId;
	
	@JsonProperty("CreateTime")
	private String createTime;
	
	@JsonProperty("InfoType")
	private String infoType;
	
	@JsonProperty("ComponentVerifyTicket")
	private String verifyTicket;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getInfoType() {
		return infoType;
	}

	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

	public String getVerifyTicket() {
		return verifyTicket;
	}

	public void setVerifyTicket(String verifyTicket) {
		this.verifyTicket = verifyTicket;
	}

	@Override
	public String toString() {
		return "VerifyTicket [appId=" + appId + ", createTime=" + createTime + ", infoType=" + infoType
				+ ", verifyTicket=" + verifyTicket + "]";
	}
	
	
}

package com.yumu.hexie.integration.notify;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Operator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5089155883275597468L;
	
	private String name;
	private String mobile;
	@JsonProperty("info_name")
	private String infoName;
	private String openid;
	private String unionid;
	@JsonProperty("corp_id")
	private String corpid;
	@JsonProperty("corp_user_id")
	private String corpUserId;
	private String appid;
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getCorpid() {
		return corpid;
	}
	public void setCorpid(String corpid) {
		this.corpid = corpid;
	}
	public String getCorpUserId() {
		return corpUserId;
	}
	public void setCorpUserId(String corpUserId) {
		this.corpUserId = corpUserId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getInfoName() {
		return infoName;
	}
	public void setInfoName(String infoName) {
		this.infoName = infoName;
	}
	
	

}

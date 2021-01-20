package com.yumu.hexie.integration.message.mapper;

import java.io.Serializable;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryMsgOperMapper implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -992475069795632594L;
	
	private BigInteger id;
	@JsonProperty("openid")
	private String openId;
	private String name;
	private String tel;
	private String appid;
	@JsonProperty("user_id")
	private BigInteger userId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public BigInteger getUserId() {
		return userId;
	}
	public void setUserId(BigInteger userId) {
		this.userId = userId;
	}
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	
	
	
}

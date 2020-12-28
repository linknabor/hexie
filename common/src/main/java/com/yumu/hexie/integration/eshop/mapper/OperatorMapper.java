package com.yumu.hexie.integration.eshop.mapper;

import java.io.Serializable;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OperatorMapper implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5515150347720343930L;

	private BigInteger id;
	@JsonProperty("openid")
	private String openId;
	private String name;
	private String tel;
	private String appid;
	@JsonProperty("userid")
	private BigInteger userId;
	
	public OperatorMapper(BigInteger id, String openId, String name, String tel, String appid, BigInteger userId) {
		super();
		this.id = id;
		this.openId = openId;
		this.name = name;
		this.tel = tel;
		this.appid = appid;
		this.userId = userId;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public BigInteger getUserId() {
		return userId;
	}

	public void setUserId(BigInteger userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "OperatorMapper [id=" + id + ", openId=" + openId + ", name=" + name + ", tel=" + tel + ", appid="
				+ appid + ", userId=" + userId + "]";
	}
	
	
	
}

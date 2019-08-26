package com.eshequ.hexie.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PreAuthCode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3466560216028587519L;

	@JsonProperty("pre_auth_code")
	private String preAuthCode;
	
	@JsonProperty("expires_in")
	private String expiresIn;
	
	private Long createTime;

	public String getPreAuthCode() {
		return preAuthCode;
	}

	public void setPreAuthCode(String preAuthCode) {
		this.preAuthCode = preAuthCode;
	}

	public String getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "PreAuthCode [preAuthCode=" + preAuthCode + ", expiresIn=" + expiresIn + ", createTime=" + createTime
				+ "]";
	}
	
	
}

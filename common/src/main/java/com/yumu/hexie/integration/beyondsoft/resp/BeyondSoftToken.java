package com.yumu.hexie.integration.beyondsoft.resp;

import java.io.Serializable;

public class BeyondSoftToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3952503726129162488L;
	
	private String access_token;
	private String enable;
	private String scope;
	private String token_type;
	private Double expires_in;
	private String jti;
	
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getEnable() {
		return enable;
	}
	public void setEnable(String enable) {
		this.enable = enable;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	public Double getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(Double expires_in) {
		this.expires_in = expires_in;
	}
	public String getJti() {
		return jti;
	}
	public void setJti(String jti) {
		this.jti = jti;
	}
	@Override
	public String toString() {
		return "BeyondSoftToken [access_token=" + access_token + ", enable=" + enable + ", scope=" + scope
				+ ", token_type=" + token_type + ", expires_in=" + expires_in + ", jti=" + jti + "]";
	}
	
	
}

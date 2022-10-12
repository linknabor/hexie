package com.yumu.hexie.integration.wechat.entity;

import java.io.Serializable;

public class MiniAccessToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7227822592970275361L;
	
	private String access_token;
	private String expires_in;
	private String errcode;	//-1,系统繁忙，此时请开发者稍候再试,0	请求成功
	private String errmsg;
	
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}
	public String getErrcode() {
		return errcode;
	}
	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	@Override
	public String toString() {
		return "MiniAccessToken [access_token=" + access_token + ", expires_in=" + expires_in + ", errcode=" + errcode
				+ ", errmsg=" + errmsg + "]";
	}
	
	

}

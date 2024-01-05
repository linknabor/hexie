package com.yumu.hexie.integration.wechat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class UserMiniprogram implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7475667531374306290L;
	
	private String appid;	//小程序appid
	private String openid;
	@JsonProperty("session_key")
	private String sessionKey;
	private String unionid;
	private String errcode;
	private String errmsg;
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
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
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	@Override
	public String toString() {
		return "UserMiniprogram [appid=" + appid + ", openid=" + openid + ", sessionKey=" + sessionKey + ", unionid="
				+ unionid + ", errcode=" + errcode + ", errmsg=" + errmsg + "]";
	}
	
	
	

}

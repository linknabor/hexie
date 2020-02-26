package com.yumu.hexie.model.wechat;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class WechatApp extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7027425722194775816L;
	
	private String appId;
	private String appName;
	private String appSecret;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppSecret() {
		return appSecret;
	}
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	
	
}

package com.yumu.hexie.model.merchant;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class MerchantPlat extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6588464847525847855L;
	
	private long merchantId;
	private String merchantName;
	private String appId;
	private String appName;
	
	public MerchantPlat(long merchantId, String merchantName, String appId, String appName) {
		super();
		this.merchantId = merchantId;
		this.merchantName = merchantName;
		this.appId = appId;
		this.appName = appName;
	}
	
	public long getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
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
	
	
	
}

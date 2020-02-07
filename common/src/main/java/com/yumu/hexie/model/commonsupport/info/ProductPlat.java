package com.yumu.hexie.model.commonsupport.info;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;
import com.yumu.hexie.model.wechat.WechatApp;

@Entity
public class ProductPlat extends BaseModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -527837665579306987L;
	
	private long productId;
	private String productName;
	private String appId;
	private String appName;
	
	public ProductPlat() {
		super();
	}

	public ProductPlat(Product product, WechatApp wechatApp) {

		this.productId = product.getId();
		this.productName = product.getName();
		this.appId = wechatApp.getAppId();
		this.appName = wechatApp.getAppName();
	}
	
	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
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

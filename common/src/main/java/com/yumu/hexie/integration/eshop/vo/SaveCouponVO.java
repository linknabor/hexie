package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;

public class SaveCouponVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7891551016006851276L;
	
	private String userId;
	private String seedStr;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSeedStr() {
		return seedStr;
	}
	public void setSeedStr(String seedStr) {
		this.seedStr = seedStr;
	}
	@Override
	public String toString() {
		return "SaveCouponVO [userId=" + userId + ", seedStr=" + seedStr + "]";
	}
	

}

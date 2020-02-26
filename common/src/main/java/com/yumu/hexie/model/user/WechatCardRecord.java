package com.yumu.hexie.model.user;

import com.yumu.hexie.model.BaseModel;

public class WechatCardRecord extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1036470250408719581L;
	
	private String keyStr;
	private String memo;
	private String reason;
	private String type;
	private String userId;
	
	public String getKeyStr() {
		return keyStr;
	}
	public void setKeyStr(String keyStr) {
		this.keyStr = keyStr;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	

}

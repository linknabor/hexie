package com.yumu.hexie.vo;

import java.io.Serializable;

public class OperAuthorization implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1795474650790171761L;
	
	private String appid;
	private String sectIds;	//逗号分割
	private String type;
	private String scene;	//场景:线上、线下、二维码支付
	private String behavior;	//行为:通知或者交易
	private String feeId;	//费用类型(其他收入才有)
	private String timestamp;	//时间戳
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getSectIds() {
		return sectIds;
	}
	public void setSectIds(String sectIds) {
		this.sectIds = sectIds;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFeeId() {
		return feeId;
	}
	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getScene() {
		return scene;
	}
	public void setScene(String scene) {
		this.scene = scene;
	}
	public String getBehavior() {
		return behavior;
	}
	public void setBehavior(String behavior) {
		this.behavior = behavior;
	}
	@Override
	public String toString() {
		return "OperAuthorization [appid=" + appid + ", sectIds=" + sectIds + ", type=" + type + ", scene=" + scene
				+ ", behavior=" + behavior + ", feeId=" + feeId + ", timestamp=" + timestamp + "]";
	}
	
}

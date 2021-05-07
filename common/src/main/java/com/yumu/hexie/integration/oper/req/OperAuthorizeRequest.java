package com.yumu.hexie.integration.oper.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OperAuthorizeRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4928928252289863373L;
	@JsonProperty("user_id")
	private String userId;
	private String appid;
	@JsonProperty("sect_ids")
	private String sectIds;	//逗号分割
	private String type;
	private String scene;	//场景:线上、线下、二维码支付
	private String behavior;	//行为:通知或者交易
	@JsonProperty("fee_id")
	private String feeId;	//费用类型(其他收入才有)
	private String timestamp;	//时间戳
	private String openid;
	private String tel;	//使用订阅消息以后可以不要
	private String name;
	
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
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "OperAuthorizeRequest [userId=" + userId + ", appid=" + appid + ", sectIds=" + sectIds + ", type=" + type
				+ ", scene=" + scene + ", behavior=" + behavior + ", feeId=" + feeId + ", timestamp=" + timestamp
				+ ", openid=" + openid + ", tel=" + tel + ", name=" + name + "]";
	}
	
}

package com.yumu.hexie.web.shequ.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OtherPayVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5770501102521970718L;
	
	private String money;
	@JsonProperty("sect_id")
	private String sectId;
	@JsonProperty("fee_id")
	private String feeId;
	private String openid;
	private String remark;
	private String appid;
	
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getFeeId() {
		return feeId;
	}
	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	@Override
	public String toString() {
		return "OtherPayVO [money=" + money + ", sectId=" + sectId + ", feeId=" + feeId + ", openid=" + openid
				+ ", remark=" + remark + ", appid=" + appid + "]";
	}
	

}

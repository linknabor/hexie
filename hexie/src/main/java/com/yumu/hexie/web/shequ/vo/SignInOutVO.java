package com.yumu.hexie.web.shequ.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignInOutVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2418662425483958659L;
	
	private String openid;
	@JsonProperty("cfg_id")
	private String cfgId;
	@JsonProperty("sect_id")
	private String sectId;
	@JsonProperty("sign_in")
	private String signFlag;
	@JsonProperty("fee_id")
	private String feeId;
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getCfgId() {
		return cfgId;
	}
	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getSignFlag() {
		return signFlag;
	}
	public void setSignFlag(String signFlag) {
		this.signFlag = signFlag;
	}
	public String getFeeId() {
		return feeId;
	}
	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	@Override
	public String toString() {
		return "SignInOutVO [openid=" + openid + ", cfgId=" + cfgId + ", sectId=" + sectId + ", signFlag=" + signFlag
				+ ", feeId=" + feeId + "]";
	}
	
}

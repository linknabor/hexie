package com.yumu.hexie.integration.wuye.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignInOutRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3990593875560346815L;

	private String openid;
	@JsonProperty("cfg_id")
	private String cfgId;
	@JsonProperty("sect_id")
	private String sectId;
	@JsonProperty("sign_in")
	private String signFlag;
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
	@Override
	public String toString() {
		return "SignInOutRequest [openid=" + openid + ", cfgId=" + cfgId + ", sectId=" + sectId + ", signFlag="
				+ signFlag + "]";
	}
	
	
}

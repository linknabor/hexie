package com.yumu.hexie.integration.wuye.dto;

import com.yumu.hexie.model.user.User;

public class SignInOutDTO {
	
	private User user;

	private String openid;
	private String cfgId;
	private String sectId;
	private String signFlag;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
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
		return "SignInOutDTO [user=" + user + ", openid=" + openid + ", cfgId=" + cfgId + ", sectId=" + sectId
				+ ", signFlag=" + signFlag + "]";
	}

	
}

package com.yumu.hexie.model.card.dto;

import java.io.Serializable;

public class EventUpdateCardDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -773445918000323209L;
	
	private String openid;
	private String appId;
	private String cardId;
	private String cardCode;
	private String modifyBonus;
	private String modifyBalance;
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getCardCode() {
		return cardCode;
	}
	public void setCardCode(String cardCode) {
		this.cardCode = cardCode;
	}
	public String getModifyBonus() {
		return modifyBonus;
	}
	public void setModifyBonus(String modifyBonus) {
		this.modifyBonus = modifyBonus;
	}
	public String getModifyBalance() {
		return modifyBalance;
	}
	public void setModifyBalance(String modifyBalance) {
		this.modifyBalance = modifyBalance;
	}
	@Override
	public String toString() {
		return "EventUpdateCardDTO [openid=" + openid + ", appId=" + appId + ", cardId=" + cardId + ", cardCode="
				+ cardCode + ", modifyBonus=" + modifyBonus + ", modifyBalance=" + modifyBalance + "]";
	}
	
	

}

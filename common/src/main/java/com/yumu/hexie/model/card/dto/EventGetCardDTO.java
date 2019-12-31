package com.yumu.hexie.model.card.dto;

import java.io.Serializable;

public class EventGetCardDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4004912739738156983L;

	private String openid;
	private String appId;
	private String cardId;
	private String cardCode;
	private String outerId;
	private String outStr;
	private String oldUserCardCode;
	private String isRestoreMemberCard;
	private String sourceScene;
	
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
	public String getOuterId() {
		return outerId;
	}
	public void setOuterId(String outerId) {
		this.outerId = outerId;
	}
	public String getOutStr() {
		return outStr;
	}
	public void setOutStr(String outStr) {
		this.outStr = outStr;
	}
	public String getOldUserCardCode() {
		return oldUserCardCode;
	}
	public void setOldUserCardCode(String oldUserCardCode) {
		this.oldUserCardCode = oldUserCardCode;
	}
	public String getIsRestoreMemberCard() {
		return isRestoreMemberCard;
	}
	public void setIsRestoreMemberCard(String isRestoreMemberCard) {
		this.isRestoreMemberCard = isRestoreMemberCard;
	}
	public String getSourceScene() {
		return sourceScene;
	}
	public void setSourceScene(String sourceScene) {
		this.sourceScene = sourceScene;
	}
	@Override
	public String toString() {
		return "EventGetCardDTO [openid=" + openid + ", appId=" + appId + ", cardId=" + cardId + ", cardCode="
				+ cardCode + ", outerId=" + outerId + ", outStr=" + outStr + ", oldUserCardCode=" + oldUserCardCode
				+ ", isRestoreMemberCard=" + isRestoreMemberCard + ", sourceScene=" + sourceScene + "]";
	}
	
}

package com.yumu.hexie.integration.wechat.entity.card;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateUserCardReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8684342534307742698L;
	
	private String code;
	@JsonProperty("card_id")
	private String cardId;
	private String bonus;
	@JsonProperty("add_bonus")
	private String addBonus;
	@JsonProperty("record_bonus")
	private String recordBonus;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getBonus() {
		return bonus;
	}
	public void setBonus(String bonus) {
		this.bonus = bonus;
	}
	public String getAddBonus() {
		return addBonus;
	}
	public void setAddBonus(String addBonus) {
		this.addBonus = addBonus;
	}
	public String getRecordBonus() {
		return recordBonus;
	}
	public void setRecordBonus(String recordBonus) {
		this.recordBonus = recordBonus;
	}
	@Override
	public String toString() {
		return "UpdateUserCardReq [code=" + code + ", cardId=" + cardId + ", bonus=" + bonus + ", addBonus=" + addBonus
				+ ", recordBonus=" + recordBonus + "]";
	}
	
	
}

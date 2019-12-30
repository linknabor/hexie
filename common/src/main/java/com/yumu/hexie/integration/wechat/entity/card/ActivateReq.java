package com.yumu.hexie.integration.wechat.entity.card;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivateReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 148192725773345951L;

	@JsonProperty("membership_number")
	private String membershipNumber;
	
	private String code;
	
	@JsonProperty("card_id")
	private String cardId;
	
	@JsonProperty("init_bonus")
	private String initBonus;	//初始积分，不填为0
	
	@JsonProperty("init_bonus_record")
	private String initBonusRecord;	//积分同步说明
	
	@JsonProperty("init_balance")
	private String initBalance;	//初始余额，不填为零

	public String getMembershipNumber() {
		return membershipNumber;
	}

	public void setMembershipNumber(String membershipNumber) {
		this.membershipNumber = membershipNumber;
	}

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

	public String getInitBonus() {
		return initBonus;
	}

	public void setInitBonus(String initBonus) {
		this.initBonus = initBonus;
	}

	public String getInitBonusRecord() {
		return initBonusRecord;
	}

	public void setInitBonusRecord(String initBonusRecord) {
		this.initBonusRecord = initBonusRecord;
	}

	public String getInitBalance() {
		return initBalance;
	}

	public void setInitBalance(String initBalance) {
		this.initBalance = initBalance;
	}
	
	
	
}

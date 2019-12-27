package com.yumu.hexie.integration.wechat.entity.card;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PreActivateReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8358342145607418703L;
	
	@JsonProperty("card_id")
	private String cardId;
	
	@JsonProperty("encrypt_code")
	private String encryptCode;
	
	@JsonProperty("openid")
	private String openid;
	
	@JsonProperty("outer_str")
	private String outerStr;
	
	@JsonProperty("activate_ticket")
	private String activateTicket;

	@Override
	public String toString() {
		return "PreActivateReq [cardId=" + cardId + ", encryptCode=" + encryptCode + ", openid=" + openid
				+ ", outerStr=" + outerStr + ", activateTicket=" + activateTicket + "]";
	}
	

}

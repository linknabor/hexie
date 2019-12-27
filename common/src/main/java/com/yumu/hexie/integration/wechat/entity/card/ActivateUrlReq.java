package com.yumu.hexie.integration.wechat.entity.card;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 激活链接请求
 * @author david
 *
 */
public class ActivateUrlReq implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6566480296657314589L;

	@JsonProperty("card_id")
	private String cardId;
	
	@JsonProperty("outer_str")
	private String outerStr;

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getOuterStr() {
		return outerStr;
	}

	public void setOuterStr(String outerStr) {
		this.outerStr = outerStr;
	}
	
	
	
}

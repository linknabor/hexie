package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6867705985413586511L;
	
	@JsonProperty("user_id")
	private String wuyeId;
	@JsonProperty("trade_water_id")
	private String tradeWaterId;
	@JsonProperty("tran_amt")
	private String tranAmt;
	
	public String getWuyeId() {
		return wuyeId;
	}
	public void setWuyeId(String wuyeId) {
		this.wuyeId = wuyeId;
	}
	public String getTradeWaterId() {
		return tradeWaterId;
	}
	public void setTradeWaterId(String tradeWaterId) {
		this.tradeWaterId = tradeWaterId;
	}
	public String getTranAmt() {
		return tranAmt;
	}
	public void setTranAmt(String tranAmt) {
		this.tranAmt = tranAmt;
	}
	
	
	
}

package com.yumu.hexie.integration.eshop.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotifyConsumeRequest extends EshopServiceRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8329003443291855906L;
	
	@JsonProperty("trade_water_id")
	private String tradeWaterId;
	private String evouchers;
	
	public String getEvouchers() {
		return evouchers;
	}

	public void setEvouchers(String evouchers) {
		this.evouchers = evouchers;
	}

	public String getTradeWaterId() {
		return tradeWaterId;
	}

	public void setTradeWaterId(String tradeWaterId) {
		this.tradeWaterId = tradeWaterId;
	}

	@Override
	public String toString() {
		return "NotifyConsumeRequest [tradeWaterId=" + tradeWaterId + ", evouchers=" + evouchers + "]";
	}

	
	
	
}

package com.yumu.hexie.integration.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceOrderRequest extends CommonRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7769480227414232932L;
	
	@JsonProperty("trade_water_id")
	private String tradeWaterId;

	public String getTradeWaterId() {
		return tradeWaterId;
	}

	public void setTradeWaterId(String tradeWaterId) {
		this.tradeWaterId = tradeWaterId;
	}

	@Override
	public String toString() {
		return "OrderRequest [tradeWaterId=" + tradeWaterId + "]";
	}
	

}

package com.yumu.hexie.integration.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceOrderRequest extends CommonRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7769480227414232932L;
	
	@JsonProperty("trade_water_id")
	private String tradeWaterId;
	@JsonProperty("fee_id")
	private String feeId;
	@JsonProperty("sect_id")
	private String sectId;

	public String getTradeWaterId() {
		return tradeWaterId;
	}

	public void setTradeWaterId(String tradeWaterId) {
		this.tradeWaterId = tradeWaterId;
	}
	
	public String getFeeId() {
		return feeId;
	}

	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}

	public String getSectId() {
		return sectId;
	}

	public void setSectId(String sectId) {
		this.sectId = sectId;
	}

	@Override
	public String toString() {
		return "ServiceOrderRequest [tradeWaterId=" + tradeWaterId + ", feeId=" + feeId + ", sectId=" + sectId + "]";
	}


}

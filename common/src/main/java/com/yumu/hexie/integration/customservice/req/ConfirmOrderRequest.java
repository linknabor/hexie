package com.yumu.hexie.integration.customservice.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfirmOrderRequest extends CustomServiceRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8126908458348802356L;

	@JsonProperty("trade_water_id")
	private String tradeWaterId;
	private String openid;
	@JsonProperty("approval_date")
	private String confirmDate;
	@JsonProperty("oper_type")
	private String operType;
	
	public String getTradeWaterId() {
		return tradeWaterId;
	}
	public void setTradeWaterId(String tradeWaterId) {
		this.tradeWaterId = tradeWaterId;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getConfirmDate() {
		return confirmDate;
	}
	public void setConfirmDate(String confirmDate) {
		this.confirmDate = confirmDate;
	}
	public String getOperType() {
		return operType;
	}
	public void setOperType(String operType) {
		this.operType = operType;
	}
	@Override
	public String toString() {
		return "ConfirmOrderRequest [tradeWaterId=" + tradeWaterId + ", openid=" + openid + ", confirmDate="
				+ confirmDate + ", operType=" + operType + "]";
	}
	
	
}

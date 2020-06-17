package com.yumu.hexie.integration.customservice.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OperOrderRequest extends CustomServiceRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8126908458348802356L;

	@JsonProperty("trade_water_id")
	private String tradeWaterId;
	private String openid;
	@JsonProperty("approval_date")
	private String operDate;
	@JsonProperty("oper_type")
	private String operType;	//0接单，1确认，9取消
	
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
	public String getOperType() {
		return operType;
	}
	public void setOperType(String operType) {
		this.operType = operType;
	}
	public String getOperDate() {
		return operDate;
	}
	public void setOperDate(String operDate) {
		this.operDate = operDate;
	}
	@Override
	public String toString() {
		return "UpdateOrderStatusRequest [tradeWaterId=" + tradeWaterId + ", openid=" + openid + ", operDate="
				+ operDate + ", operType=" + operType + "]";
	}
	
	
}

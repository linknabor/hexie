package com.yumu.hexie.integration.wuye.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryEReceiptRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4543445624589562618L;
	
	@JsonProperty("trade_water_id")
	private String orderNo;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Override
	public String toString() {
		return "PayResultRequest [orderNo=" + orderNo + "]";
	}
	
	

}

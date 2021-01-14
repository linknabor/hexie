package com.yumu.hexie.integration.wuye.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryEReceiptRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4543445624589562618L;
	
	@JsonProperty("trade_water_id")
	private String orderNo;
	@JsonProperty("sys_source")
	private String sysSource;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getSysSource() {
		return sysSource;
	}

	public void setSysSource(String sysSource) {
		this.sysSource = sysSource;
	}

	@Override
	public String toString() {
		return "QueryEReceiptRequest [orderNo=" + orderNo + ", sysSource=" + sysSource + "]";
	}

	

}

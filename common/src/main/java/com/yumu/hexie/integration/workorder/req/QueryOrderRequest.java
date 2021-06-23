package com.yumu.hexie.integration.workorder.req;

import java.io.Serializable;

public class QueryOrderRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -726257792957930488L;
	
	private String orderId;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Override
	public String toString() {
		return "QueryOrderRequest [orderId=" + orderId + "]";
	}
	
	
}

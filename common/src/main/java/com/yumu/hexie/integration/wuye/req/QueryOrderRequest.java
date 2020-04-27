package com.yumu.hexie.integration.wuye.req;

public class QueryOrderRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1866339221876404587L;
	
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

package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;

public class CardPaySms implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2173369713563255782L;

	private String orderNo;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Override
	public String toString() {
		return "CardPaySms [orderNo=" + orderNo + "]";
	}
	
	
	
}

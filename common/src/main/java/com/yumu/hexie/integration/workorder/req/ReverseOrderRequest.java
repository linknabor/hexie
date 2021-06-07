package com.yumu.hexie.integration.workorder.req;

import java.io.Serializable;

public class ReverseOrderRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -352224767361732115L;
	
	private String orderId;
	private String reason;
	private String operOpenid;
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getOperOpenid() {
		return operOpenid;
	}
	public void setOperOpenid(String operOpenid) {
		this.operOpenid = operOpenid;
	}
	@Override
	public String toString() {
		return "ReverseOrderRequest [orderId=" + orderId + ", reason=" + reason + ", operOpenid=" + operOpenid + "]";
	}
	
	

}

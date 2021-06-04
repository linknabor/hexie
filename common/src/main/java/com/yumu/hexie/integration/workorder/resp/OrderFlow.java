package com.yumu.hexie.integration.workorder.resp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.common.util.DateUtil;

public class OrderFlow implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4801912114329620457L;
	
	@JsonProperty("order_id")
	private String orderId;
	@JsonProperty("current_status")
	private String currentStatus;
	private String operation;
	@JsonProperty("operation_cn")
	private String operationCn;
	private String oper;
	@JsonProperty("oper_contact")
	private String operContact;
	@JsonProperty("oper_date")
	private String operDate;
	@JsonProperty("oper_time")
	private String operTime;
	@JsonProperty("oper_reason")
	private String reason;
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getOper() {
		return oper;
	}
	public void setOper(String oper) {
		this.oper = oper;
	}
	public String getOperContact() {
		return operContact;
	}
	public void setOperContact(String operContact) {
		this.operContact = operContact;
	}
	public String getOperDate() {
		return operDate;
	}
	public void setOperDate(String operDate) {
		this.operDate = operDate;
	}
	public String getOperTime() {
		return operTime;
	}
	public void setOperTime(String operTime) {
		this.operTime = operTime;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public String getOperDateStr() {
		return DateUtil.formatFromDB(operDate, operTime);
	}
	
	@Override
	public String toString() {
		return "OrderFlow [orderId=" + orderId + ", currentStatus=" + currentStatus + ", operation=" + operation
				+ ", oper=" + oper + ", operContact=" + operContact + ", operDate=" + operDate + ", operTime="
				+ operTime + ", reason=" + reason + "]";
	}
	
	

}

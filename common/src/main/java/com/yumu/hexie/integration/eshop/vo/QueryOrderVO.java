package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;

public class QueryOrderVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4056182978069208244L;

	private String id;
	private String orderNo;
	private String orderType;
	private String status;
	private String tel;
	private String receiverName;
	private String agentNo;
	private String agentName;
	private String productName;
	private String logisticNo;
	private String sendDateBegin;
	private String sendDateEnd;
	
	private int currentPage;
	private int pageSize;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public String getAgentNo() {
		return agentNo;
	}
	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public String getLogisticNo() {
		return logisticNo;
	}
	public void setLogisticNo(String logisticNo) {
		this.logisticNo = logisticNo;
	}
	public String getSendDateBegin() {
		return sendDateBegin;
	}
	public void setSendDateBegin(String sendDateBegin) {
		this.sendDateBegin = sendDateBegin;
	}
	public String getSendDateEnd() {
		return sendDateEnd;
	}
	public void setSendDateEnd(String sendDateEnd) {
		this.sendDateEnd = sendDateEnd;
	}
	@Override
	public String toString() {
		return "QueryOrderVO [id=" + id + ", orderNo=" + orderNo + ", orderType=" + orderType + ", status=" + status
				+ ", tel=" + tel + ", receiverName=" + receiverName + ", agentNo=" + agentNo + ", agentName="
				+ agentName + ", productName=" + productName + ", logisticNo=" + logisticNo + ", sendDateBegin="
				+ sendDateBegin + ", sendDAteEnd=" + sendDateEnd + ", currentPage=" + currentPage + ", pageSize="
				+ pageSize + "]";
	}
	
	
}

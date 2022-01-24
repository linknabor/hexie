package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;
import java.util.List;

public class QueryOrderVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4056182978069208244L;

	private String id;
	private String orderNo;
	private String orderType;
	private String orderStatus;
	private String status;
	private String groupStatus; //成团状态
	private String tel;
	private String receiverName;
	private String agentNo;
	private String agentName;
	private String productName;
	private String logisticNo;
	private String sendDateBegin;
	private String sendDateEnd;
	private String sectName;
	private long createDateBegin;
	private long createDateEnd;
	private String userid;
	private List<String> sectIds;
	private String queryFlag;

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

	public String getSectName() {
		return sectName;
	}

	public void setSectName(String sectName) {
		this.sectName = sectName;
	}

	public String getGroupStatus() {
		return groupStatus;
	}

	public void setGroupStatus(String groupStatus) {
		this.groupStatus = groupStatus;
	}

	public String getQueryFlag() {
		return queryFlag;
	}

	public void setQueryFlag(String queryFlag) {
		this.queryFlag = queryFlag;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<String> getSectIds() {
		return sectIds;
	}

	public void setSectIds(List<String> sectIds) {
		this.sectIds = sectIds;
	}
	
	public long getCreateDateBegin() {
		return createDateBegin;
	}
	
	public void setCreateDateBegin(long createDateBegin) {
		this.createDateBegin = createDateBegin;
	}
	
	public long getCreateDateEnd() {
		return createDateEnd;
	}
	
	public void setCreateDateEnd(long createDateEnd) {
		this.createDateEnd = createDateEnd;
	}
	
	public String getOrderStatus() {
		return orderStatus;
	}
	
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	@Override
	public String toString() {
		return "QueryOrderVO [id=" + id + ", orderNo=" + orderNo + ", orderType=" + orderType + ", orderStatus="
				+ orderStatus + ", status=" + status + ", groupStatus=" + groupStatus + ", tel=" + tel
				+ ", receiverName=" + receiverName + ", agentNo=" + agentNo + ", agentName=" + agentName
				+ ", productName=" + productName + ", logisticNo=" + logisticNo + ", sendDateBegin=" + sendDateBegin
				+ ", sendDateEnd=" + sendDateEnd + ", sectName=" + sectName + ", createDateBegin=" + createDateBegin
				+ ", createDateEnd=" + createDateEnd + ", userid=" + userid + ", sectIds=" + sectIds + ", queryFlag="
				+ queryFlag + ", currentPage=" + currentPage + ", pageSize=" + pageSize + "]";
	}
}

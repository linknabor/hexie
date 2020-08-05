package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;

public class QueryEvoucherVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 400774352451230271L;
	
	private String tel;
	private String status;
	private String agentNo;
	private String agentName;
	
	private int currentPage;
	private int pageSize;
	
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	@Override
	public String toString() {
		return "QueryEvoucherVO [tel=" + tel + ", status=" + status + ", agentNo=" + agentNo + ", agentName="
				+ agentName + ", currentPage=" + currentPage + ", pageSize=" + pageSize + "]";
	}
	
	
	
}

package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;

public class QueryCouponVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6237088334987888845L;

	private String title;
	private String seedType;
	private String status;
	private String tel;
	private String agentNo;
	private String agentName;
	
	private int currentPage;
	private int pageSize;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSeedType() {
		return seedType;
	}
	public void setSeedType(String seedType) {
		this.seedType = seedType;
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
		return "QueryCouponVO [title=" + title + ", seedType=" + seedType + ", status=" + status + ", tel=" + tel
				+ ", agentNo=" + agentNo + ", agentName=" + agentName + ", currentPage=" + currentPage + ", pageSize="
				+ pageSize + "]";
	}
	
	 
	
}

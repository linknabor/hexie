package com.yumu.hexie.web.customservice.vo;

import java.io.Serializable;

public class OrderQueryVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8962524346780836237L;

	private String sectId;
	private String feeId;
	private String totalCount;
	private String currentPage;
	
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getFeeId() {
		return feeId;
	}
	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	public String getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
	public String getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}
	@Override
	public String toString() {
		return "OrderQueryVO [sectId=" + sectId + ", feeId=" + feeId + ", totalCount=" + totalCount + ", currentPage="
				+ currentPage + "]";
	}
	
	
	
}

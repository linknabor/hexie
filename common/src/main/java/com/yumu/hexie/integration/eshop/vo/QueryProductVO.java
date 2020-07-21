package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;

public class QueryProductVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5699349492992058047L;
	
	private String productId;
	private String productName;
	private String productStatus;
	private String productType;	
	private String agentNo;
	private String agentName;
	private String appid;
	
	private int currentPage;
	private int pageSize;
	
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductStatus() {
		return productStatus;
	}
	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
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
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	@Override
	public String toString() {
		return "QueryProductVO [productId=" + productId + ", productName=" + productName + ", productStatus="
				+ productStatus + ", productType=" + productType + ", agentNo=" + agentNo + ", agentName=" + agentName
				+ ", appid=" + appid + ", currentPage=" + currentPage + ", pageSize=" + pageSize + "]";
	}
	
	
}

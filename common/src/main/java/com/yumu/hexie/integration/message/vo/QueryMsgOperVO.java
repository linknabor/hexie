package com.yumu.hexie.integration.message.vo;

import java.io.Serializable;

public class QueryMsgOperVO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9023595509838674370L;
	
	private String operName;
	private String operTel;
	private String operType;
	
	private int currentPage;
	private int pageSize;
	public String getOperName() {
		return operName;
	}
	public void setOperName(String operName) {
		this.operName = operName;
	}
	public String getOperTel() {
		return operTel;
	}
	public void setOperTel(String operTel) {
		this.operTel = operTel;
	}
	public String getOperType() {
		return operType;
	}
	public void setOperType(String operType) {
		this.operType = operType;
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
	
	
}

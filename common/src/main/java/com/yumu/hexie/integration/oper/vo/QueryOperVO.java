package com.yumu.hexie.integration.oper.vo;

import java.io.Serializable;
import java.util.List;

public class QueryOperVO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9023595509838674370L;
	
	private String operId;
	private String operName;
	private String operTel;
	private String operType;
	private String operUserId;
	
	private List<String> sectIds;
	
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
	public List<String> getSectIds() {
		return sectIds;
	}
	public void setSectIds(List<String> sectIds) {
		this.sectIds = sectIds;
	}
	public String getOperId() {
		return operId;
	}
	public void setOperId(String operId) {
		this.operId = operId;
	}
	public String getOperUserId() {
		return operUserId;
	}
	public void setOperUserId(String operUserId) {
		this.operUserId = operUserId;
	}
	@Override
	public String toString() {
		return "QueryOperVO [operId=" + operId + ", operName=" + operName + ", operTel=" + operTel + ", operType="
				+ operType + ", operUserId=" + operUserId + ", sectIds=" + sectIds + ", currentPage=" + currentPage
				+ ", pageSize=" + pageSize + "]";
	}
	
	
}

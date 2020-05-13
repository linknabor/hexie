package com.yumu.hexie.integration.wuye.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuickPayRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7094293580343959134L;
	
	@JsonProperty("stmt_id")
	private String stmtId;
	@JsonProperty("curr_page")
	private String currPage;
	@JsonProperty("total_count")
	private String totalCount;
	
	public String getStmtId() {
		return stmtId;
	}
	public void setStmtId(String stmtId) {
		this.stmtId = stmtId;
	}
	public String getCurrPage() {
		return currPage;
	}
	public void setCurrPage(String currPage) {
		this.currPage = currPage;
	}
	public String getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
	@Override
	public String toString() {
		return "QuickPayRequest [stmtId=" + stmtId + ", currPage=" + currPage + ", totalCount=" + totalCount + "]";
	}
	
	

}

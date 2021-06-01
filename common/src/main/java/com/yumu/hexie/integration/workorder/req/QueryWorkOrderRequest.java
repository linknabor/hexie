package com.yumu.hexie.integration.workorder.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryWorkOrderRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7068643684415059517L;
	
	@JsonProperty("create_user_id")
	private String queryUserId;
	@JsonProperty("curr_page")
	private String currPage;
	@JsonProperty("total_count")
	private String totalCount = "9999";
	public String getQueryUserId() {
		return queryUserId;
	}
	public void setQueryUserId(String queryUserId) {
		this.queryUserId = queryUserId;
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
		return "QueryWorkOrderRequest [queryUserId=" + queryUserId + ", currPage=" + currPage + ", totalCount="
				+ totalCount + "]";
	}
	
	

}

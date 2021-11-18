package com.yumu.hexie.integration.posting.vo;

import java.io.Serializable;
import java.util.List;

public class QueryPostingSummaryVO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9021482880582370212L;
	
	private String startDate;
	private String endDate;
	private List<String> sectIds;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public List<String> getSectIds() {
		return sectIds;
	}
	public void setSectIds(List<String> sectIds) {
		this.sectIds = sectIds;
	}

	@Override
	public String toString() {
		return "QueryPostingSummaryVO{" +
				"startDate='" + startDate + '\'' +
				", endDate='" + endDate + '\'' +
				", sectIds=" + sectIds +
				'}';
	}
}

package com.yumu.hexie.service.page.pojo.vo;

import java.io.Serializable;

public class PageViewSumVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8767932623510511528L;

	private String appid;
	private String startDate;
	private String endDate;
	private Integer totalCounts;
	
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
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
	public Integer getTotalCounts() {
		return totalCounts;
	}
	public void setTotalCounts(Integer totalCounts) {
		this.totalCounts = totalCounts;
	}
	
	
}

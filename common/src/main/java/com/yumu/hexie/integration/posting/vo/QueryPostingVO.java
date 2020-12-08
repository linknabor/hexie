package com.yumu.hexie.integration.posting.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.DateUtil;

public class QueryPostingVO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9021482880582370212L;
	
	private String id;
	private String startDate;
	private String endDate;
	private String userName;
	private String sectId;
	private List<String> sectIds;
	
	private int currentPage;
	private int pageSize;

	public String getStartDate() {
		if (!StringUtils.isEmpty(startDate)) {
			Date d = DateUtil.getDateFromString(startDate);
			return String.valueOf(d.getTime());
		}
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		if (!StringUtils.isEmpty(endDate)) {
			Date d = DateUtil.getDateFromString(endDate);
			return String.valueOf(d.getTime());
		}
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public List<String> getSectIds() {
		return sectIds;
	}
	public void setSectIds(List<String> sectIds) {
		this.sectIds = sectIds;
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
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "QueryPostingVO [id=" + id + ", startDate=" + startDate + ", endDate=" + endDate + ", userName="
				+ userName + ", sectId=" + sectId + ", sectIds=" + sectIds + ", currentPage=" + currentPage
				+ ", pageSize=" + pageSize + "]";
	}
	
	
}

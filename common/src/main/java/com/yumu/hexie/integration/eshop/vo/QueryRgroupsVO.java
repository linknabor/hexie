package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;
import java.util.List;

public class QueryRgroupsVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -773235293995047529L;
	
	private String ruleId;
	private String ruleName;
	private String startDate;
	private String endDate;
	private String groupStatus;
	private String agentNo;
	private String userid;
	private List<String> sectList;
	
	private int currentPage;
	private int pageSize;
	
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
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
	public String getGroupStatus() {
		return groupStatus;
	}
	public void setGroupStatus(String groupStatus) {
		this.groupStatus = groupStatus;
	}
	public String getAgentNo() {
		return agentNo;
	}
	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public List<String> getSectList() {
		return sectList;
	}
	public void setSectList(List<String> sectList) {
		this.sectList = sectList;
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
		return "QueryRgroupsVO [ruleId=" + ruleId + ", ruleName=" + ruleName + ", startDate=" + startDate + ", endDate="
				+ endDate + ", groupStatus=" + groupStatus + ", agentNo=" + agentNo + ", userid=" + userid
				+ ", sectList=" + sectList + ", currentPage=" + currentPage + ", pageSize=" + pageSize + "]";
	}
	

}

package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;

public class QueryCouponCfgVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8947479546819437763L;

	private String ruleId;	//规则id
	private String title;	//名称
	private String seedId;	//种子ID
	private String seedType;	//种子类型
	private String status;	//状态
	private String agentName;	//代理商名称
	private String agentNo;	//代理商编号
	
	private int currentPage;
	private int pageSize;
	
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSeedType() {
		return seedType;
	}
	public void setSeedType(String seedType) {
		this.seedType = seedType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getAgentNo() {
		return agentNo;
	}
	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
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
	public String getSeedId() {
		return seedId;
	}
	public void setSeedId(String seedId) {
		this.seedId = seedId;
	}
	@Override
	public String toString() {
		return "QueryCouponCfgVO [ruleId=" + ruleId + ", title=" + title + ", seedId=" + seedId + ", seedType="
				+ seedType + ", status=" + status + ", agentName=" + agentName + ", agentNo=" + agentNo
				+ ", currentPage=" + currentPage + ", pageSize=" + pageSize + "]";
	}
	
}

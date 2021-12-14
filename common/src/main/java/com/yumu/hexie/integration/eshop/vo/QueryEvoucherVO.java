package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;
import java.util.List;

public class QueryEvoucherVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 400774352451230271L;
	
	private String tel;
	private String status;
	private String agentNo;
	private String agentName;
	private String type;

	private String userid;
	private List<String> sectIds;

	private int currentPage;
	private int pageSize;
	
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<String> getSectIds() {
		return sectIds;
	}

	public void setSectIds(List<String> sectIds) {
		this.sectIds = sectIds;
	}

	@Override
	public String toString() {
		return "QueryEvoucherVO{" +
				"tel='" + tel + '\'' +
				", status='" + status + '\'' +
				", agentNo='" + agentNo + '\'' +
				", agentName='" + agentName + '\'' +
				", type='" + type + '\'' +
				", userid='" + userid + '\'' +
				", sectIds=" + sectIds +
				", currentPage=" + currentPage +
				", pageSize=" + pageSize +
				'}';
	}
}

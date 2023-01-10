package com.yumu.hexie.integration.community.req;

import java.io.Serializable;

public class QueryGroupOwnerReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6932313291415429886L;

	private long id;		//团长Id
	private String name;	//团长名称
	private String tel;	//团长手机
	private String agentNo;	//团长所属机构ID
	private int miniNum = 0;	//最小开团数量
	private int maxNum = 0;	//最大开团数量
	
	private int currentPage;
	private int pageSize;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public int getMiniNum() {
		return miniNum;
	}
	public void setMiniNum(int miniNum) {
		this.miniNum = miniNum;
	}
	public int getMaxNum() {
		return maxNum;
	}
	public void setMaxNum(int maxNum) {
		this.maxNum = maxNum;
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
	public String getAgentNo() {
		return agentNo;
	}
	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}
	@Override
	public String toString() {
		return "QueryGroupOwnerReq [id=" + id + ", name=" + name + ", tel=" + tel + ", agentNo=" + agentNo
				+ ", miniNum=" + miniNum + ", maxNum=" + maxNum + ", currentPage=" + currentPage + ", pageSize="
				+ pageSize + "]";
	}
	
	
}

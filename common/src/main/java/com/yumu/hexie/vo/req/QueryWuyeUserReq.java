package com.yumu.hexie.vo.req;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryWuyeUserReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1722181620887919863L;
	
	private String id;
	private String name;
	private String tel;
	@JsonProperty("sect_ids")
	private List<String> sectIds;
	@JsonProperty("csp_id")
	private String cspId;
	@JsonProperty("sect_name")
	private String xiaoquName;
	
	@JsonProperty("wuye_ids")
	private String wuyeIds;
	
	private int currentPage;
	private int pageSize;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
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
	public String getCspId() {
		return cspId;
	}
	public void setCspId(String cspId) {
		this.cspId = cspId;
	}
	public String getXiaoquName() {
		return xiaoquName;
	}
	public void setXiaoquName(String xiaoquName) {
		this.xiaoquName = xiaoquName;
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
	public String getWuyeIds() {
		return wuyeIds;
	}
	public void setWuyeIds(String wuyeIds) {
		this.wuyeIds = wuyeIds;
	}
	@Override
	public String toString() {
		return "QueryWuyeUserReq [id=" + id + ", name=" + name + ", tel=" + tel + ", sectIds=" + sectIds + ", cspId="
				+ cspId + ", xiaoquName=" + xiaoquName + ", wuyeIds=" + wuyeIds + ", currentPage=" + currentPage
				+ ", pageSize=" + pageSize + "]";
	}
	
}

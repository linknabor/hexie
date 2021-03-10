package com.yumu.hexie.integration.wuye.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryCellRequest extends WuyeRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2525778642379113337L;
	
	@JsonProperty("sect_id")
	private String sectId;
	@JsonProperty("cell_addr")
	private String cellAddr;
	@JsonProperty("user_id")
	private String userId;
	
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getCellAddr() {
		return cellAddr;
	}
	public void setCellAddr(String cellAddr) {
		this.cellAddr = cellAddr;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "QueryCellRequest [sectId=" + sectId + ", cellAddr=" + cellAddr + ", userId=" + userId + "]";
	}
	
	
}

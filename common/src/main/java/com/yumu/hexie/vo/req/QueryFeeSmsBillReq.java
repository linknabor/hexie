package com.yumu.hexie.vo.req;

import java.io.Serializable;

public class QueryFeeSmsBillReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3358155018425706374L;
	
	private String batchNo;
	private String cellId;
	private String appid;
	
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getCellId() {
		return cellId;
	}
	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	@Override
	public String toString() {
		return "QueryFeeSmsBillReq [batchNo=" + batchNo + ", cellId=" + cellId + ", appid=" + appid + "]";
	}
	
	

}

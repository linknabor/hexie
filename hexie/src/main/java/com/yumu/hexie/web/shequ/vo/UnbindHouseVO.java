package com.yumu.hexie.web.shequ.vo;

import java.io.Serializable;

public class UnbindHouseVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6004068974774854223L;
	
	private String wuyeId;
	private String cellId;
	
	public String getWuyeId() {
		return wuyeId;
	}
	public void setWuyeId(String wuyeId) {
		this.wuyeId = wuyeId;
	}
	public String getCellId() {
		return cellId;
	}
	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	@Override
	public String toString() {
		return "UnbindHouseVO [wuyeId=" + wuyeId + ", cellId=" + cellId + "]";
	}
	

}

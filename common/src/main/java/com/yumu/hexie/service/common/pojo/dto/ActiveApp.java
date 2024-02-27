package com.yumu.hexie.service.common.pojo.dto;

import java.io.Serializable;

public class ActiveApp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4071237467437600957L;
	
	private String activeAppid;
	private String activeOpenid;
	
	public ActiveApp() {
		super();
	}
	public ActiveApp(String activeAppid, String activeOpenid) {
		super();
		this.activeAppid = activeAppid;
		this.activeOpenid = activeOpenid;
	}
	public String getActiveAppid() {
		return activeAppid;
	}
	public void setActiveAppid(String activeAppid) {
		this.activeAppid = activeAppid;
	}
	public String getActiveOpenid() {
		return activeOpenid;
	}
	public void setActiveOpenid(String activeOpenid) {
		this.activeOpenid = activeOpenid;
	}
	@Override
	public String toString() {
		return "ActiveApp [activeAppid=" + activeAppid + ", activeOpenid=" + activeOpenid + "]";
	}

}

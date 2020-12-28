package com.yumu.hexie.model.view;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class CsHotline extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3572594260114415951L;
	
	private String fromSys;
	private String hotline;
	
	public String getFromSys() {
		return fromSys;
	}
	public void setFromSys(String fromSys) {
		this.fromSys = fromSys;
	}
	public String getHotline() {
		return hotline;
	}
	public void setHotline(String hotline) {
		this.hotline = hotline;
	}
	
	
	
}

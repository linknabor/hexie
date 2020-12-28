package com.yumu.hexie.model.commonsupport.logistics;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class LogisticCompany extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3919166814010236575L;
	
	private String code;
	private String name;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "LogisticCompany [code=" + code + ", name=" + name + "]";
	}
	
	

}

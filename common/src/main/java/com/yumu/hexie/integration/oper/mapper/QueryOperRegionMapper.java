package com.yumu.hexie.integration.oper.mapper;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryOperRegionMapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8606930430332667941L;
	
	private String name;
	@JsonProperty("sect_id")
	private String sectId;
	
	public QueryOperRegionMapper(String name, String sectId) {
		this.name = name;
		this.sectId = sectId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	
	

}

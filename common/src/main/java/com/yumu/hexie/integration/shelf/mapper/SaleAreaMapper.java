package com.yumu.hexie.integration.shelf.mapper;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SaleAreaMapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7690030992898586940L;
	
	private String name;
	@JsonProperty("parent_name")
	private String parentName;
	@JsonProperty("sect_id")
	private String sectId;
	
	public SaleAreaMapper(String name, String parentName, String sectId) {
		super();
		this.name = name;
		this.parentName = parentName;
		this.sectId = sectId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}

	
	
}

package com.yumu.hexie.integration.wuye.resp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserAccessSpotResp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7269500962137964024L;

	@JsonProperty("spot_id")
	private String spotId;
	@JsonProperty("spot_name")
	private String spotName;
	@JsonProperty("sect_id")
	private String sectId;
	
	public String getSpotId() {
		return spotId;
	}
	public void setSpotId(String spotId) {
		this.spotId = spotId;
	}
	public String getSpotName() {
		return spotName;
	}
	public void setSpotName(String spotName) {
		this.spotName = spotName;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	
}

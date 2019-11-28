package com.yumu.hexie.model.region;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class RegionUrl extends BaseModel{

	private static final long serialVersionUID = 4276437344314180001L;
	
	private String regionName;
	private String regionUrl;
	private String regionCode;
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public String getRegionUrl() {
		return regionUrl;
	}
	public void setRegionUrl(String regionUrl) {
		this.regionUrl = regionUrl;
	}
	public String getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}
	
	
	
}

package com.yumu.hexie.integration.wuye.dto;

import com.yumu.hexie.model.user.User;

public class GetCellDTO {
	
	private User user;
	
	private String sectId;
	private String buildId;
	private String unitId;
	private String dataType;
	private String regionName;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getBuildId() {
		return buildId;
	}
	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}
	public String getUnitId() {
		return unitId;
	}
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	@Override
	public String toString() {
		return "GetCellDTO [user=" + user + ", sectId=" + sectId + ", buildId=" + buildId + ", unitId=" + unitId
				+ ", dataType=" + dataType + ", regionName=" + regionName + "]";
	}
	
	

}

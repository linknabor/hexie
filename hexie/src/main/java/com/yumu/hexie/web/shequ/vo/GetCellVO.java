package com.yumu.hexie.web.shequ.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetCellVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4037194847646118465L;

	@JsonProperty("sect_id")
	private String sectId;
	@JsonProperty("build_id")
	private String buildId;
	@JsonProperty("unit_id")
	private String unitId;
	@JsonProperty("data_type")
	private String dataType;
	@JsonProperty("regionname")
	private String regionName;
	
	private String appid;
	private String openid;
	
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
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	@Override
	public String toString() {
		return "GetCellVO [sectId=" + sectId + ", buildId=" + buildId + ", unitId=" + unitId + ", dataType=" + dataType
				+ ", regionName=" + regionName + ", appid=" + appid + ", openid=" + openid + "]";
	}
	
	
}

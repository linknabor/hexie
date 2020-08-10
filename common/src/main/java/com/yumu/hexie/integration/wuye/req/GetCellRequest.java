package com.yumu.hexie.integration.wuye.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetCellRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3259632389696914284L;
	
	@JsonProperty("sect_id")
	private String sectId;
	@JsonProperty("build_id")
	private String buildId;
	@JsonProperty("unit_id")
	private String unitId;
	@JsonProperty("data_type")
	private String dataType;
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
	@Override
	public String toString() {
		return "GetCellRequest [sectId=" + sectId + ", buildId=" + buildId + ", unitId=" + unitId + ", dataType="
				+ dataType + "]";
	}

	

}

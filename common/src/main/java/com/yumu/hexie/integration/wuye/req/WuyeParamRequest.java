package com.yumu.hexie.integration.wuye.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WuyeParamRequest extends WuyeRequest {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6693354614982427263L;
	
	@JsonProperty("info_id")
	private String infoId;
	private String type;
	@JsonProperty("para_name")
	private String paraName;
	
	public String getInfoId() {
		return infoId;
	}
	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getParaName() {
		return paraName;
	}
	public void setParaName(String paraName) {
		this.paraName = paraName;
	}
	@Override
	public String toString() {
		return "WuyeParamRequest [infoId=" + infoId + ", type=" + type + ", paraName=" + paraName + "]";
	}
	
	
}

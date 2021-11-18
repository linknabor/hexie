package com.yumu.hexie.service.shequ.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class RatioDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7843016505150551043L;
	@JsonProperty("fee_name")
	private String feeName;
	@JsonProperty("ratio")
	private String ratio;
	
	public String getFeeName() {
		return feeName;
	}
	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}
	public String getRatio() {
		return ratio;
	}
	public void setRatio(String ratio) {
		this.ratio = ratio;
	}
	

}

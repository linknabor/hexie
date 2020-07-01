package com.yumu.hexie.integration.customservice.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetServiceRequest extends CustomServiceRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6161415239209916461L;

	@JsonProperty("user_id")
	private String userId;
	@JsonProperty("sect_id")
	private String sectId;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	
	
}

package com.yumu.hexie.integration.wuye.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BillStdRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7218891681750028330L;
	
	@JsonProperty("user_id")
	private String wuyeId;
	@JsonProperty("start_date")
	private String startDate;
	@JsonProperty("end_date")
	private String endDate;
	@JsonProperty("mng_cell_id")
	private String houseId;
	
	public String getWuyeId() {
		return wuyeId;
	}
	public void setWuyeId(String wuyeId) {
		this.wuyeId = wuyeId;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getHouseId() {
		return houseId;
	}
	public void setHouseId(String houseId) {
		this.houseId = houseId;
	}
	

}

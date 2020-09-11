package com.yumu.hexie.integration.notify;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PartnerNotification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2801379640385378652L;

	private String tel;
	@JsonProperty("valid_date")
	private String validDate;
	
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getValidDate() {
		return validDate;
	}
	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}
	@Override
	public String toString() {
		return "PartnerNotification [tel=" + tel + ", validDate=" + validDate + "]";
	}
	
	
}

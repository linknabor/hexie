package com.yumu.hexie.integration.wuye.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignInOutRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3990593875560346815L;

	@JsonProperty("person_id")
	private String personId;
	@JsonProperty("sign_in")
	private String signFlag;
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public String getSignFlag() {
		return signFlag;
	}
	public void setSignFlag(String signFlag) {
		this.signFlag = signFlag;
	}
	public String getFeeId() {
		return feeId;
	}
	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	@Override
	public String toString() {
		return "SignInOutRequest [personId=" + personId + ", signFlag=" + signFlag + "]";
	}
	
	
}

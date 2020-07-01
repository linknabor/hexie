package com.yumu.hexie.web.shequ.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignInOutVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2418662425483958659L;
	
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
	@Override
	public String toString() {
		return "SignInOutVO [personId=" + personId + ", signFlag=" + signFlag + "]";
	}
	

}

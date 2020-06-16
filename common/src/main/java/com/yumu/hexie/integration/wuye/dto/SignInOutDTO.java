package com.yumu.hexie.integration.wuye.dto;

import com.yumu.hexie.model.user.User;

public class SignInOutDTO {
	
	private User user;

	private String personId;
	private String signFlag;
	
	public User getUser() {
		return user;
	}
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

	public void setUser(User user) {
		this.user = user;
	}
	
	
}

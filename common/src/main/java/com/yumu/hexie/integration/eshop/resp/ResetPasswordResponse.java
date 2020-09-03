package com.yumu.hexie.integration.eshop.resp;

import java.io.Serializable;

public class ResetPasswordResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8713514921021309918L;

	private String tel;
	private String password;
	
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "ResetPasswordResponse [tel=" + tel + ", password=" + password + "]";
	}
	
	
	
	
}

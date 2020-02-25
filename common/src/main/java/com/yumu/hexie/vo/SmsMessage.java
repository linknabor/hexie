package com.yumu.hexie.vo;

import java.io.Serializable;

public class SmsMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2137461619542890242L;
	
	private String title;	//短信抬头。也叫签名，就是【】里的东西
	private String message;
	private String mobile;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@Override
	public String toString() {
		return "SmsMessage [title=" + title + ", message=" + message + ", mobile=" + mobile + "]";
	}
	
	

}

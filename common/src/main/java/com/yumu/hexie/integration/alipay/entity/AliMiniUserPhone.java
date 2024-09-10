package com.yumu.hexie.integration.alipay.entity;

import java.io.Serializable;

public class AliMiniUserPhone implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3129286843495909622L;
	
	private String msg;
	private String code;
	private String mobile;
	
	//如果错误，有以下
	private String subCode;
	private String subMsg;
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getSubCode() {
		return subCode;
	}
	public void setSubCode(String subCode) {
		this.subCode = subCode;
	}
	public String getSubMsg() {
		return subMsg;
	}
	public void setSubMsg(String subMsg) {
		this.subMsg = subMsg;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
	
}

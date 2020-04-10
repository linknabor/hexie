package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8838388457285618912L;

	private String result;
	@JsonProperty("err_msg")
	private String errMsg;
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	
}

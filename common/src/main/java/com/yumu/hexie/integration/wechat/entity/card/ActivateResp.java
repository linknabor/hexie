package com.yumu.hexie.integration.wechat.entity.card;

import java.io.Serializable;

public class ActivateResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 148192725773345951L;

	private String errcode;
	private String errmsg;
	public String getErrcode() {
		return errcode;
	}
	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	
	
	
	
	
}

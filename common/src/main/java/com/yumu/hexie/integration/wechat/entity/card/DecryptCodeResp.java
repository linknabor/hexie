package com.yumu.hexie.integration.wechat.entity.card;

import java.io.Serializable;

public class DecryptCodeResp implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3342607045891725578L;
	private String errcode;
	private String errmsg;
	private String code;
	
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Override
	public String toString() {
		return "DecryptCodeResp [errcode=" + errcode + ", errmsg=" + errmsg + ", code=" + code + "]";
	}
	

}

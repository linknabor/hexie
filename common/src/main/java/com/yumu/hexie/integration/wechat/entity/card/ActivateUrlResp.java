package com.yumu.hexie.integration.wechat.entity.card;

import java.io.Serializable;

public class ActivateUrlResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3473232759961230413L;
	
	private String errcode;	//0表示正常
	private String errmsg;
	private String url;
	
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "ActivateUrlResp [errcode=" + errcode + ", errmsg=" + errmsg + ", url=" + url + "]";
	}
	
	

}

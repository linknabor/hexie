package com.yumu.hexie.service.mpsetup.req;

import java.io.Serializable;

public class MpQueryReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5235050188894219588L;
	
	private String appid;

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	@Override
	public String toString() {
		return "MpQueryReq [appid=" + appid + "]";
	}
	

}

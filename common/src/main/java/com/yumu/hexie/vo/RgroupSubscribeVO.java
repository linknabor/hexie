package com.yumu.hexie.vo;

import java.io.Serializable;

public class RgroupSubscribeVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7230295292983780171L;
	
	private String infoId;	//订阅小区就是小区id,订阅团长就是团长 id，订阅某个团购就是团购id
	private int type;	//团购0, 团长1，小区2
	
	public String getInfoId() {
		return infoId;
	}
	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "RgroupSubscribeVO [infoId=" + infoId + ", type=" + type + "]";
	}

	
}

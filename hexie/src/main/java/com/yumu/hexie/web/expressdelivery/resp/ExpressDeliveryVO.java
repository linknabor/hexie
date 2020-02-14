package com.yumu.hexie.web.expressdelivery.resp;

import java.io.Serializable;

public class ExpressDeliveryVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1663279218388564263L;

	private String wuyeId;
	private String type;
	
	public String getWuyeId() {
		return wuyeId;
	}
	public void setWuyeId(String wuyeId) {
		this.wuyeId = wuyeId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}

package com.yumu.hexie.integration.eshop.resp;

import java.io.Serializable;

public class QueryRgroupSummaryResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4588272672757860047L;
	
	private int delivered;
	private int undelivered;
	
	public int getDelivered() {
		return delivered;
	}
	public void setDelivered(int delivered) {
		this.delivered = delivered;
	}
	public int getUndelivered() {
		return undelivered;
	}
	public void setUndelivered(int undelivered) {
		this.undelivered = undelivered;
	}
	
	
	
}

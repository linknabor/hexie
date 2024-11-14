package com.yumu.hexie.integration.wuye.resp;

import java.io.Serializable;

public class AlipayMarketingConsult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6813891443886601014L;
	
	private String channel_operation_info;
	private String order_id;
	
	public String getChannel_operation_info() {
		return channel_operation_info;
	}
	public void setChannel_operation_info(String channel_operation_info) {
		this.channel_operation_info = channel_operation_info;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	
}

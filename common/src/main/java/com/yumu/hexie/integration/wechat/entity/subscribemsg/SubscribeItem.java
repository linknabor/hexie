package com.yumu.hexie.integration.wechat.entity.subscribemsg;

import java.io.Serializable;

public class SubscribeItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5474556013526297481L;
	
	public SubscribeItem(String value) {
		super();
		this.value = value;
	}

	private String	value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "SubscribeItem [value=" + value + "]";
	}
	
	
}

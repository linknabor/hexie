package com.yumu.hexie.integration.eshop.req;

import java.io.Serializable;

public class QueryRgroupSect implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1010922928292372447L;
	
	private String sectIds;	//sect_id，逗号拼接

	public String getSectIds() {
		return sectIds;
	}

	public void setSectIds(String sectIds) {
		this.sectIds = sectIds;
	}

	@Override
	public String toString() {
		return "QueryRgroupSect [sectIds=" + sectIds + "]";
	}
	
	
	

}

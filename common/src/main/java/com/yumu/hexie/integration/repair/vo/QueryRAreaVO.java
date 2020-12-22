package com.yumu.hexie.integration.repair.vo;

import java.io.Serializable;
import java.util.List;

public class QueryRAreaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7685364809330576818L;
	
	private List<String> sectIds;

	public List<String> getSectIds() {
		return sectIds;
	}

	public void setSectIds(List<String> sectIds) {
		this.sectIds = sectIds;
	}
	
	

}

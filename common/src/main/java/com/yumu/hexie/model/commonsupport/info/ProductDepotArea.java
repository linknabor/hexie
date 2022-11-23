package com.yumu.hexie.model.commonsupport.info;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class ProductDepotArea extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3277001042291499880L;

	private long depotId;
	private long regionId;
	
	public long getDepotId() {
		return depotId;
	}
	public void setDepotId(long depotId) {
		this.depotId = depotId;
	}
	public long getRegionId() {
		return regionId;
	}
	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}
	
	
	
}

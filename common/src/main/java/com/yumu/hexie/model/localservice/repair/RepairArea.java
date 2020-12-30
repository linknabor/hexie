package com.yumu.hexie.model.localservice.repair;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;
@Entity
public class RepairArea extends BaseModel {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8803025677651091075L;
	
	private String cspId;
	private String sectId;
	private String sectName;
	public String getCspId() {
		return cspId;
	}
	public void setCspId(String cspId) {
		this.cspId = cspId;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getSectName() {
		return sectName;
	}
	public void setSectName(String sectName) {
		this.sectName = sectName;
	}
	
	
	
}

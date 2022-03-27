package com.yumu.hexie.integration.eshop.resp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RgroupRegionsVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4466707576286922393L;

	@JsonProperty("sect_id")
	private String sectId;
	@JsonProperty("sect_name_frst")
	private String sectName;
	@JsonProperty("sect_addr_frst")
	private String sectAddr;
	@JsonProperty("csp_id")
	private String cspId;
	@JsonProperty("csp_name")
	private String cspName;
	
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
	public String getSectAddr() {
		return sectAddr;
	}
	public void setSectAddr(String sectAddr) {
		this.sectAddr = sectAddr;
	}
	public String getCspId() {
		return cspId;
	}
	public void setCspId(String cspId) {
		this.cspId = cspId;
	}
	public String getCspName() {
		return cspName;
	}
	public void setCspName(String cspName) {
		this.cspName = cspName;
	}
	
	
}

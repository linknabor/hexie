package com.yumu.hexie.model.distribution.region;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class County extends BaseModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1809857881225213602L;
	
	private long countyId;
	private int status;
	private String name;
	private long cityId;
	private String remark;
	
	public long getCountyId() {
		return countyId;
	}
	public void setCountyId(long countyId) {
		this.countyId = countyId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getCityId() {
		return cityId;
	}
	public void setCityId(long cityId) {
		this.cityId = cityId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	

}

package com.yumu.hexie.model.distribution.region;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class Province extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8358440053158970344L;

	private long provinceId;
	private int status;
	private String name;
	private String shortName;
	private String remark;
	
	public long getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(long provinceId) {
		this.provinceId = provinceId;
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
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		return "Province [provinceId=" + provinceId + ", status=" + status + ", name=" + name + ", shortName="
				+ shortName + ", remark=" + remark + "]";
	}
	
	
	
}

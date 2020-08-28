package com.yumu.hexie.model.distribution.region;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class City extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8734908279599607769L;

	private long cityId;
	private int status;
	private String name;
	private String code;
	private long provinceId;
	private String remark;
	
	public long getCityId() {
		return cityId;
	}
	public void setCityId(long cityId) {
		this.cityId = cityId;
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public long getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(long provinceId) {
		this.provinceId = provinceId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		return "City [cityId=" + cityId + ", status=" + status + ", name=" + name + ", code=" + code + ", provinceId="
				+ provinceId + ", remark=" + remark + "]";
	}
	
	
	
}

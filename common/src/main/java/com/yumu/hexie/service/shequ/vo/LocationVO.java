package com.yumu.hexie.service.shequ.vo;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.integration.wuye.resp.RadiusSect;

public class LocationVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2872612247894954646L;

	private List<RadiusSect> sectList;
	private String province;	//省份
	private String provinceAbbr;	//省份缩写
	private String formattedAddress;	//格式化后的地址
	
	public List<RadiusSect> getSectList() {
		return sectList;
	}
	public void setSectList(List<RadiusSect> sectList) {
		this.sectList = sectList;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getProvinceAbbr() {
		return provinceAbbr;
	}
	public void setProvinceAbbr(String provinceAbbr) {
		this.provinceAbbr = provinceAbbr;
	}
	public String getFormattedAddress() {
		return formattedAddress;
	}
	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}
	@Override
	public String toString() {
		return "LocationVO [sectList=" + sectList + ", province=" + province + ", provinceAbbr=" + provinceAbbr
				+ ", formattedAddress=" + formattedAddress + "]";
	}
	
	
}

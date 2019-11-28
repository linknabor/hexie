package com.yumu.hexie.integration.baidu.vo;

import java.util.List;

import com.yumu.hexie.model.region.RegionUrl;

public class RegionVo {
	private List<RegionUrl> regionUrl;
	private String address;
	
	public List<RegionUrl> getRegionUrl() {
		return regionUrl;
	}
	public void setRegionurl(List<RegionUrl> regionUrl) {
		this.regionUrl = regionUrl;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	
}

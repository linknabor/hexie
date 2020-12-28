package com.yumu.hexie.integration.baidu.vo;

import java.util.List;

public class RegionVo {
	
	private List<RegionSelection> regionUrl;
	private String showAddress;
	private String address;
	
	public List<RegionSelection> getRegionUrl() {
		return regionUrl;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getShowAddress() {
		return showAddress;
	}

	public void setShowAddress(String showAddress) {
		this.showAddress = showAddress;
	}

	public void setRegionUrl(List<RegionSelection> regionUrl) {
		this.regionUrl = regionUrl;
	}
	
	public static class RegionSelection {
		
		private String regionName;
		private String showRegionName;

		public String getRegionName() {
			return regionName;
		}

		public void setRegionName(String regionName) {
			this.regionName = regionName;
		}

		public String getShowRegionName() {
			return showRegionName;
		}

		public void setShowRegionName(String showRegionName) {
			this.showRegionName = showRegionName;
		}

		
	}
	
}

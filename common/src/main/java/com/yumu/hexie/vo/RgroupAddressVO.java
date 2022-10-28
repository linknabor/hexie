package com.yumu.hexie.vo;

import java.io.Serializable;

import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.vo.RgroupVO.RegionVo;

public class RgroupAddressVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5924883856320278805L;
	
	private RegionVo region;
	private Address address;
	
	public RegionVo getRegion() {
		return region;
	}
	public void setRegion(RegionVo region) {
		this.region = region;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	
}

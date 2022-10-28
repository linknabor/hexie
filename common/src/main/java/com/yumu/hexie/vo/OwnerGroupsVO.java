package com.yumu.hexie.vo;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.vo.RgroupVO.RegionVo;

public class OwnerGroupsVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7226756946343900178L;
	
	private List<RgroupVO> groupList;
	private List<RegionVo> regionList;
	
	public List<RgroupVO> getGroupList() {
		return groupList;
	}
	public void setGroupList(List<RgroupVO> groupList) {
		this.groupList = groupList;
	}
	public List<RegionVo> getRegionList() {
		return regionList;
	}
	public void setRegionList(List<RegionVo> regionList) {
		this.regionList = regionList;
	}
	
	

}

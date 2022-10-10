package com.yumu.hexie.vo;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.vo.RgroupVO.RgroupOwnerVO;

public class RgroupLeaderVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4027976230890300451L;
	
	private RgroupOwnerVO groupLeader;
	private List<RgroupVO> groups;
	
	public RgroupOwnerVO getGroupLeader() {
		return groupLeader;
	}
	public void setGroupLeader(RgroupOwnerVO groupLeader) {
		this.groupLeader = groupLeader;
	}
	public List<RgroupVO> getGroups() {
		return groups;
	}
	public void setGroups(List<RgroupVO> groups) {
		this.groups = groups;
	}
	
	

}

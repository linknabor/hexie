package com.yumu.hexie.vo;

import java.io.Serializable;
import java.math.BigInteger;

import com.yumu.hexie.model.ModelConstant;

public class RgroupAreaRegionMapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6473136454951879602L;

	/*rgroupAreaItem*/
	private BigInteger ruleId;
	private Integer currentNum;	//售卖份数
	private Integer groupMinNum;	//最小成团份数
	private Integer groupStatus = ModelConstant.RGROUP_STAUS_GROUPING;//团购状态1 开始 2成功 3失败
	private String remark;
	
	/*region*/
	private BigInteger id;
	private String name;
	private String parentName;
	private Double latitude = 0.00;
	private Double longitude = 0.00;
	private String xiaoquAddress;
	private String sectId;
	
	public RgroupAreaRegionMapper(BigInteger ruleId, Integer currentNum, Integer groupMinNum, Integer groupStatus,
			String remark, BigInteger id, String name, String parentName, Double latitude, Double longitude,
			String xiaoquAddress, String sectId) {
		super();
		this.ruleId = ruleId;
		this.currentNum = currentNum;
		this.groupMinNum = groupMinNum;
		this.groupStatus = groupStatus;
		this.remark = remark;
		this.id = id;
		this.name = name;
		this.parentName = parentName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.xiaoquAddress = xiaoquAddress;
		this.sectId = sectId;
	}
	
	public BigInteger getRuleId() {
		return ruleId;
	}
	public void setRuleId(BigInteger ruleId) {
		this.ruleId = ruleId;
	}
	public Integer getCurrentNum() {
		return currentNum;
	}
	public void setCurrentNum(Integer currentNum) {
		this.currentNum = currentNum;
	}
	public Integer getGroupMinNum() {
		return groupMinNum;
	}
	public void setGroupMinNum(Integer groupMinNum) {
		this.groupMinNum = groupMinNum;
	}
	public Integer getGroupStatus() {
		return groupStatus;
	}
	public void setGroupStatus(Integer groupStatus) {
		this.groupStatus = groupStatus;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLogitude(Double logitude) {
		this.longitude = logitude;
	}
	public String getXiaoquAddress() {
		return xiaoquAddress;
	}
	public void setXiaoquAddress(String xiaoquAddress) {
		this.xiaoquAddress = xiaoquAddress;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	
	
}

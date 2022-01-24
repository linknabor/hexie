package com.yumu.hexie.model.distribution;

import javax.persistence.Entity;
import javax.persistence.Transient;

//团购上架管理
@Entity
public class RgroupAreaItem  extends RuleDistribution {
	
	private static final long serialVersionUID = 4808669460780339640L;

	@Transient
	private int process;//进度
	@Transient
	private int count;	//页面用
	@Transient
	private int totalCount;	//库存
	
	private boolean featured = false;
	private int productType;
	
	private long areaLeaderId;	//运营端用户ID
	private String areaLeader;	//区域leader(团长)
	private String areaLeaderOpenid;	//团长openid
	private String areaLeaderAddr;	//团长地址
	private String areaLeaderTel;	//团长电话
	private String areaLeaderImg;	//团长头像

	public boolean isFeatured() {
		return featured;
	}
	public void setFeatured(boolean featured) {
		this.featured = featured;
	}
	public int getProductType() {
		return productType;
	}
	public void setProductType(int productType) {
		this.productType = productType;
	}
	public int getProcess() {
		return process;
	}
	public void setProcess(int process) {
		this.process = process;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public String getAreaLeader() {
		return areaLeader;
	}
	public void setAreaLeader(String areaLeader) {
		this.areaLeader = areaLeader;
	}
	public String getAreaLeaderAddr() {
		return areaLeaderAddr;
	}
	public void setAreaLeaderAddr(String areaLeaderAddr) {
		this.areaLeaderAddr = areaLeaderAddr;
	}
	public String getAreaLeaderImg() {
		return areaLeaderImg;
	}
	public void setAreaLeaderImg(String areaLeaderImg) {
		this.areaLeaderImg = areaLeaderImg;
	}
	public long getAreaLeaderId() {
		return areaLeaderId;
	}
	public void setAreaLeaderId(long areaLeaderId) {
		this.areaLeaderId = areaLeaderId;
	}
	public String getAreaLeaderTel() {
		return areaLeaderTel;
	}
	public void setAreaLeaderTel(String areaLeaderTel) {
		this.areaLeaderTel = areaLeaderTel;
	}
	public String getAreaLeaderOpenid() {
		return areaLeaderOpenid;
	}
	public void setAreaLeaderOpenid(String areaLeaderOpenid) {
		this.areaLeaderOpenid = areaLeaderOpenid;
	}
	
}

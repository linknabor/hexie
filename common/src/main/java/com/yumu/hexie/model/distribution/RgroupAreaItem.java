package com.yumu.hexie.model.distribution;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.yumu.hexie.model.ModelConstant;

//团购上架管理
@Entity
public class RgroupAreaItem  extends RuleDistribution {
	
	private static final long serialVersionUID = 4808669460780339640L;

	@Transient
	private int count;	//页面用
	@Transient
	private int totalCount;	//库存
	
	private boolean featured = false;
	private int productType;
	
	private long areaLeaderId;	//运营端用户ID			//TODO delete after new pub
	private String areaLeader;	//区域leader(团长)		//TODO delete after new pub
	private String areaLeaderOpenid;	//团长openid	//TODO delete after new pub
	private String areaLeaderAddr;	//团长地址			//TODO delete after new pub
	private String areaLeaderTel;	//团长电话			//TODO delete after new pub
	private String areaLeaderImg;	//团长头像			//TODO delete after new pub
	
	private int currentNum;	//售卖份数
	private int groupMinNum;	//最小成团份数
	private int groupStatus = ModelConstant.RGROUP_STAUS_GROUPING;//团购状态1 开始 2成功 3失败
	
	private String remark;

	@Transient
	public int getProcess() {
    	if(groupMinNum<=0) {
    		return 0;
    	}
    	return (100*currentNum/groupMinNum);
	}
	
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
	public int getGroupMinNum() {
		return groupMinNum;
	}
	public void setGroupMinNum(int groupMinNum) {
		this.groupMinNum = groupMinNum;
	}
	public int getGroupStatus() {
		return groupStatus;
	}
	public void setGroupStatus(int groupStatus) {
		this.groupStatus = groupStatus;
	}
	public int getCurrentNum() {
		return currentNum;
	}
	public void setCurrentNum(int currentNum) {
		this.currentNum = currentNum;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}

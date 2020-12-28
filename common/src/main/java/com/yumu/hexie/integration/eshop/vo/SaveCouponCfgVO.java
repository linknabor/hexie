package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;

public class SaveCouponCfgVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7891551016006851276L;
	
	private String operType;	//add,edit;
	
	private String ruleId;	//规则id
	private String seedId;	//种子ID
	private String title;	//名称
	private String seedType;	//种子类型
	private String amount;	//优惠券金额
	private String usageCondition;	//最小使用金额
	private String totalCount;	//发放总量
	private String itemType;	//适用模块
	private String supportType;	//0全部支持,1部份支持，2部份不支持
	private String supported;	//支持的产品(包括服务)，逗号分割
	private String unsupported;	//不支持的产品(包括服务)，逗号分割
	private String expiredDays;	//超时天数
	private String startDate;	//发放开始日期
	private String endDate;	//发放结束日期
	private String useStartDate;	//可用日期开始	
	private String useEndDate;	//可用日期结束
	private String status;	//状态
	private String suggestUrl;	//跳转链接
	private String seedImg;	//种子图
	private String couponDesc;	//优惠券描述
	
	private String agentName;
	private String agentNo;
	private String supportAllAgent;	//全平台通用券, 0否，1是
	
	private String serviceSupportedSect;	//自定义服务支持的小区
	
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSeedType() {
		return seedType;
	}
	public void setSeedType(String seedType) {
		this.seedType = seedType;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getUsageCondition() {
		return usageCondition;
	}
	public void setUsageCondition(String usageCondition) {
		this.usageCondition = usageCondition;
	}
	public String getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public String getSupportType() {
		return supportType;
	}
	public void setSupportType(String supportType) {
		this.supportType = supportType;
	}
	public String getSupported() {
		return supported;
	}
	public void setSupported(String supported) {
		this.supported = supported;
	}
	public String getUnsupported() {
		return unsupported;
	}
	public void setUnsupported(String unsupported) {
		this.unsupported = unsupported;
	}
	public String getExpiredDays() {
		return expiredDays;
	}
	public void setExpiredDays(String expiredDays) {
		this.expiredDays = expiredDays;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getUseStartDate() {
		return useStartDate;
	}
	public void setUseStartDate(String useStartDate) {
		this.useStartDate = useStartDate;
	}
	public String getUseEndDate() {
		return useEndDate;
	}
	public void setUseEndDate(String useEndDate) {
		this.useEndDate = useEndDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSuggestUrl() {
		return suggestUrl;
	}
	public void setSuggestUrl(String suggestUrl) {
		this.suggestUrl = suggestUrl;
	}
	public String getSeedImg() {
		return seedImg;
	}
	public void setSeedImg(String seedImg) {
		this.seedImg = seedImg;
	}
	public String getCouponDesc() {
		return couponDesc;
	}
	public void setCouponDesc(String couponDesc) {
		this.couponDesc = couponDesc;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getAgentNo() {
		return agentNo;
	}
	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}
	public String getOperType() {
		return operType;
	}
	public void setOperType(String operType) {
		this.operType = operType;
	}
	public String getSeedId() {
		return seedId;
	}
	public void setSeedId(String seedId) {
		this.seedId = seedId;
	}
	public String getServiceSupportedSect() {
		return serviceSupportedSect;
	}
	public void setServiceSupportedSect(String serviceSupportedSect) {
		this.serviceSupportedSect = serviceSupportedSect;
	}
	public String getSupportAllAgent() {
		return supportAllAgent;
	}
	public void setSupportAllAgent(String supportAllAgent) {
		this.supportAllAgent = supportAllAgent;
	}
	@Override
	public String toString() {
		return "SaveCouponCfgVO [operType=" + operType + ", ruleId=" + ruleId + ", seedId=" + seedId + ", title="
				+ title + ", seedType=" + seedType + ", amount=" + amount + ", usageCondition=" + usageCondition
				+ ", totalCount=" + totalCount + ", itemType=" + itemType + ", supportType=" + supportType
				+ ", supported=" + supported + ", unsupported=" + unsupported + ", expiredDays=" + expiredDays
				+ ", startDate=" + startDate + ", endDate=" + endDate + ", useStartDate=" + useStartDate
				+ ", useEndDate=" + useEndDate + ", status=" + status + ", suggestUrl=" + suggestUrl + ", seedImg="
				+ seedImg + ", couponDesc=" + couponDesc + ", agentName=" + agentName + ", agentNo=" + agentNo
				+ ", supportAllAgent=" + supportAllAgent + ", serviceSupportedSect=" + serviceSupportedSect + "]";
	}
	

}

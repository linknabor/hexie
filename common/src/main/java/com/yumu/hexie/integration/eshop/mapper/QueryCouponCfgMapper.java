package com.yumu.hexie.integration.eshop.mapper;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryCouponCfgMapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2591585817266954366L;

	@JsonProperty("rule_id")
	private BigInteger ruleId;
	@JsonProperty("seed_id")
	private BigInteger seedId;
	private String title;
	@JsonProperty("seed_type")
	private Integer seedType;
	@JsonProperty("seed_str")
	private String seedStr;
	@JsonProperty("item_type")
	private Integer itemType;
	private Integer status;
	@JsonProperty("support_type")
	private Integer supportType;
	@JsonProperty("total_count")
	private Integer totalCount;
	@JsonProperty("received_count")
	private Integer receivedCount;	//已领
	@JsonProperty("used_count")
	private Integer usedCount;	//已使用
	private Float amount;
	@JsonProperty("usage_condition")
	private Float usageCondition;
	@JsonProperty("product_id")
	private String productId;
	@JsonProperty("uproduct_id")
	private String uProductId;
	@JsonProperty("start_date")
	private Timestamp startDate;
	@JsonProperty("end_date")
	private Timestamp endDate;
	@JsonProperty("use_start_date")
	private Timestamp useStartDate;
	@JsonProperty("use_end_date")
	private Timestamp useEndDate;
	@JsonProperty("expired_days")
	private Integer expiredDays;
	@JsonProperty("suggest_url")
	private String suggestUrl;
	@JsonProperty("seed_img")
	private String seedImg;
	@JsonProperty("agent_name")
	private String agentName;
	@JsonProperty("agent_no")
	private String agentNo;
	@JsonProperty("coupon_desc")
	private String couponDesc;
	
	public QueryCouponCfgMapper(BigInteger ruleId, BigInteger seedId, String title, Integer seedType, String seedStr,
			Integer itemType, Integer status, Integer supportType, Integer totalCount, Integer receivedCount, Integer usedCount, 
			Float amount, Float usageCondition, String productId, String uProductId, Timestamp startDate, 
			Timestamp endDate, Timestamp useStartDate, Timestamp useEndDate, Integer expiredDays,
			String suggestUrl, String seedImg, String agentName, String agentNo, String couponDesc) {
		super();
		this.ruleId = ruleId;
		this.seedId = seedId;
		this.title = title;
		this.seedType = seedType;
		this.seedStr = seedStr;
		this.itemType = itemType;
		this.status = status;
		this.supportType = supportType;
		this.totalCount = totalCount;
		this.receivedCount = receivedCount;
		this.usedCount = usedCount;
		this.amount = amount;
		this.usageCondition = usageCondition;
		this.productId = productId;
		this.uProductId = uProductId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.useStartDate = useStartDate;
		this.useEndDate = useEndDate;
		this.expiredDays = expiredDays;
		this.suggestUrl = suggestUrl;
		this.seedImg = seedImg;
		this.agentName = agentName;
		this.agentNo = agentNo;
		this.couponDesc = couponDesc;
	}

	public BigInteger getRuleId() {
		return ruleId;
	}

	public void setRuleId(BigInteger ruleId) {
		this.ruleId = ruleId;
	}

	public BigInteger getSeedId() {
		return seedId;
	}

	public void setSeedId(BigInteger seedId) {
		this.seedId = seedId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getSeedType() {
		return seedType;
	}

	public void setSeedType(Integer seedType) {
		this.seedType = seedType;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public Float getUsageCondition() {
		return usageCondition;
	}

	public void setUsageCondition(Float usageCondition) {
		this.usageCondition = usageCondition;
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

	public Integer getItemType() {
		return itemType;
	}

	public void setItemType(Integer itemType) {
		this.itemType = itemType;
	}

	public String getSuggestUrl() {
		return suggestUrl;
	}

	public void setSuggestUrl(String suggestUrl) {
		this.suggestUrl = suggestUrl;
	}

	public String getCouponDesc() {
		return couponDesc;
	}

	public void setCouponDesc(String couponDesc) {
		this.couponDesc = couponDesc;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public Timestamp getUseStartDate() {
		return useStartDate;
	}

	public void setUseStartDate(Timestamp useStartDate) {
		this.useStartDate = useStartDate;
	}

	public Timestamp getUseEndDate() {
		return useEndDate;
	}

	public void setUseEndDate(Timestamp useEndDate) {
		this.useEndDate = useEndDate;
	}

	public Integer getReceivedCount() {
		return receivedCount;
	}

	public void setReceivedCount(Integer receivedCount) {
		this.receivedCount = receivedCount;
	}

	public Integer getUsedCount() {
		return usedCount;
	}

	public void setUsedCount(Integer usedCount) {
		this.usedCount = usedCount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getExpiredDays() {
		return expiredDays;
	}

	public void setExpiredDays(Integer expiredDays) {
		this.expiredDays = expiredDays;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getuProductId() {
		return uProductId;
	}

	public void setuProductId(String uProductId) {
		this.uProductId = uProductId;
	}

	public String getSeedImg() {
		return seedImg;
	}

	public void setSeedImg(String seedImg) {
		this.seedImg = seedImg;
	}

	public Integer getSupportType() {
		return supportType;
	}

	public void setSupportType(Integer supportType) {
		this.supportType = supportType;
	}

	public String getSeedStr() {
		return seedStr;
	}

	public void setSeedStr(String seedStr) {
		this.seedStr = seedStr;
	}
	
	
}

package com.yumu.hexie.integration.eshop.mapper;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryCouponMapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2207429296753351416L;

	private BigInteger id;
	private String title;
	@JsonProperty("rule_id")
	private BigInteger ruleId;
	@JsonProperty("create_date")
	private BigInteger createDate;
	@JsonProperty("user_id")
	private BigInteger userId;
	private String tel;
	@JsonProperty("seed_type")
	private Integer seedType;
	private Integer status;
	private Float amount;
	@JsonProperty("use_start_date")
	private Timestamp useStartDate;
	@JsonProperty("expired_date")
	private Timestamp expiredDate;
	@JsonProperty("used_date")
	private Timestamp usedDate;
	@JsonProperty("coupon_desc")
	private String couponDesc;
	@JsonProperty("agent_name")
	private String agentName;
	@JsonProperty("agent_no")
	private String agentNo;
	
	public QueryCouponMapper(BigInteger id, String title, BigInteger ruleId, BigInteger createDate, BigInteger userId,
			String tel, Integer seedType, Integer status, Float amount, Timestamp useStartDate, Timestamp expiredDate,
			Timestamp usedDate, String couponDesc, String agentName, String agentNo) {
		super();
		this.id = id;
		this.title = title;
		this.ruleId = ruleId;
		this.createDate = createDate;
		this.userId = userId;
		this.tel = tel;
		this.seedType = seedType;
		this.status = status;
		this.amount = amount;
		this.useStartDate = useStartDate;
		this.expiredDate = expiredDate;
		this.usedDate = usedDate;
		this.couponDesc = couponDesc;
		this.agentName = agentName;
		this.agentNo = agentNo;
	}
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public BigInteger getRuleId() {
		return ruleId;
	}
	public void setRuleId(BigInteger ruleId) {
		this.ruleId = ruleId;
	}
	public BigInteger getCreateDate() {
		return createDate;
	}
	public void setCreateDate(BigInteger createDate) {
		this.createDate = createDate;
	}
	public BigInteger getUserId() {
		return userId;
	}
	public void setUserId(BigInteger userId) {
		this.userId = userId;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public Integer getSeedType() {
		return seedType;
	}
	public void setSeedType(Integer seedType) {
		this.seedType = seedType;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Timestamp getUseStartDate() {
		return useStartDate;
	}
	public void setUseStartDate(Timestamp useStartDate) {
		this.useStartDate = useStartDate;
	}
	public Timestamp getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(Timestamp expiredDate) {
		this.expiredDate = expiredDate;
	}
	public Timestamp getUsedDate() {
		return usedDate;
	}
	public void setUsedDate(Timestamp usedDate) {
		this.usedDate = usedDate;
	}
	public Float getAmount() {
		return amount;
	}
	public void setAmount(Float amount) {
		this.amount = amount;
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
	
	
	
}

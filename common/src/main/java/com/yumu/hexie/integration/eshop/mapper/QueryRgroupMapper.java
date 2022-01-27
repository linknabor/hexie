package com.yumu.hexie.integration.eshop.mapper;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

public class QueryRgroupMapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3315602004786412492L;
	
//	String sqlColumn1 = " rule.id, rule.createDate, rule.startDate, rule.endDate, rule.name, rule.price, "
//			+ "rule.currentNum, rule.groupStatus, ";
//	p.mainPicture, item.areaLeader, item.areaLeaderAddr, item.areaLeaderTel, item.areaLeaderId
	
	private BigInteger id;
	private BigInteger createDate;
	private Timestamp startDate;
	private Timestamp endDate;
	private String name;
	private Float price;
	private Integer currentNum;
	private Integer groupStatus;
	private String mainPicture;
	private String areaLeader;
	private String areaLeaderAddr;
	private String areaLeaderTel;
	private BigInteger areaLeaderId;
	private BigInteger groupFinishDate;
	private Integer freeShippingNum;
	private Integer limitNumOnce;
	private Float postageFee;
	
	public QueryRgroupMapper() {
		super();
	}
	
	public QueryRgroupMapper(BigInteger id, BigInteger createDate, Timestamp startDate, Timestamp endDate, String name,
			Float price, Integer currentNum, Integer groupStatus, String mainPicture, String areaLeader,
			String areaLeaderAddr, String areaLeaderTel, BigInteger areaLeaderId, BigInteger groupFinishDate,
			Integer freeShippingNum, Integer limitNumOnce, Float postageFee) {
		super();
		this.id = id;
		this.createDate = createDate;
		this.startDate = startDate;
		this.endDate = endDate;
		this.name = name;
		this.price = price;
		this.currentNum = currentNum;
		this.groupStatus = groupStatus;
		this.mainPicture = mainPicture;
		this.areaLeader = areaLeader;
		this.areaLeaderAddr = areaLeaderAddr;
		this.areaLeaderTel = areaLeaderTel;
		this.areaLeaderId = areaLeaderId;
		this.groupFinishDate = groupFinishDate;
		this.freeShippingNum = freeShippingNum;
		this.limitNumOnce = limitNumOnce;
		this.postageFee = postageFee;
	}


	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public BigInteger getCreateDate() {
		return createDate;
	}
	public void setCreateDate(BigInteger createDate) {
		this.createDate = createDate;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public Integer getCurrentNum() {
		return currentNum;
	}
	public void setCurrentNum(Integer currentNum) {
		this.currentNum = currentNum;
	}
	public Integer getGroupStatus() {
		return groupStatus;
	}
	public void setGroupStatus(Integer groupStatus) {
		this.groupStatus = groupStatus;
	}

	public String getMainPicture() {
		return mainPicture;
	}

	public void setMainPicture(String mainPicture) {
		this.mainPicture = mainPicture;
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

	public String getAreaLeaderTel() {
		return areaLeaderTel;
	}

	public void setAreaLeaderTel(String areaLeaderTel) {
		this.areaLeaderTel = areaLeaderTel;
	}

	public BigInteger getAreaLeaderId() {
		return areaLeaderId;
	}

	public void setAreaLeaderId(BigInteger areaLeaderId) {
		this.areaLeaderId = areaLeaderId;
	}

	public BigInteger getGroupFinishDate() {
		return groupFinishDate;
	}

	public void setGroupFinishDate(BigInteger groupFinishDate) {
		this.groupFinishDate = groupFinishDate;
	}

	public Integer getFreeShippingNum() {
		return freeShippingNum;
	}

	public void setFreeShippingNum(Integer freeShippingNum) {
		this.freeShippingNum = freeShippingNum;
	}

	public Integer getLimitNumOnce() {
		return limitNumOnce;
	}

	public void setLimitNumOnce(Integer limitNumOnce) {
		this.limitNumOnce = limitNumOnce;
	}

	public Float getPostageFee() {
		return postageFee;
	}

	public void setPostageFee(Float postageFee) {
		this.postageFee = postageFee;
	}
	

}

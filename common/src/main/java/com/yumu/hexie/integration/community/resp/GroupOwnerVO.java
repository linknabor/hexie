package com.yumu.hexie.integration.community.resp;

import java.io.Serializable;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.common.util.DateUtil;

public class GroupOwnerVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4830725952170812666L;

	private BigInteger id;
	@JsonProperty("user_id")
	private BigInteger userId;
	private String name;
	private String tel;
	private BigInteger createDate;
	private Integer attendees;	//跟团数
	private Integer members;	//浏览数
	@JsonProperty("fee_rate")
	private String feeRate;		//团长费率
	@JsonProperty("head_img_url")
	private String headImgUrl;
	
	
	@JsonProperty("create_date")
	private String createDateStr;
	
	public GroupOwnerVO(BigInteger id, BigInteger userId, String name, String tel, BigInteger createDate, Integer attendees,
			Integer members, String feeRate, String headImgUrl) {
		super();
		this.id = id;
		this.userId = userId;
		this.name = name;
		this.tel = tel;
		this.createDate = createDate;
		this.attendees = attendees;
		this.members = members;
		this.feeRate = feeRate;
		this.headImgUrl = headImgUrl;
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
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public BigInteger getCreateDate() {
		return createDate;
	}
	public void setCreateDate(BigInteger createDate) {
		this.createDate = createDate;
	}
	public Integer getAttendees() {
		return attendees;
	}
	public void setAttendees(Integer attendees) {
		this.attendees = attendees;
	}
	public Integer getMembers() {
		return members;
	}
	public void setMembers(Integer members) {
		this.members = members;
	}
	public String getFeeRate() {
		return feeRate;
	}
	public void setFeeRate(String feeRate) {
		this.feeRate = feeRate;
	}
	public String getHeadImgUrl() {
		return headImgUrl;
	}
	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}
	public String getCreateDateStr() {
		String dateStr = "";
		if (createDate != null) {
			dateStr = DateUtil.dtFormat(createDate.longValue(), DateUtil.dttmSimple);
		}
		return dateStr;
	}
	public void setCreateDateStr(String createDateStr) {
		this.createDateStr = createDateStr;
	}
	public BigInteger getUserId() {
		return userId;
	}
	public void setUserId(BigInteger userId) {
		this.userId = userId;
	}
	
	
	
}

package com.yumu.hexie.integration.community.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-05-06 13:42
 */
public class OutSidRelateGroupResp implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7696766440512152770L;
	private BigInteger id;
    @JsonProperty("owner_id")
    private BigInteger ownerId;
    @JsonProperty("owner_name")
    private String ownerName;
    @JsonProperty("owner_addr")
    private String ownerAddr;
    @JsonProperty("owner_img")
    private String ownerImg;
    @JsonProperty("owner_tel")
    private String ownerTel;
    private Float price;
    private String description;
    private Integer status;
    @JsonProperty("group_status")
    private Integer groupStatus;
    @JsonProperty("start_date")
    private Timestamp startDate;
    @JsonProperty("end_date")
    private Timestamp endDate;
    @JsonProperty("description_more")
    private String descriptionMore;
    @JsonProperty("product_num")
    private BigInteger productNum;
    @JsonProperty("user_name")
    private String userName;

    private String status_cn;
    @JsonProperty("show_status")
    private String showStatus;

    public OutSidRelateGroupResp(BigInteger id, BigInteger ownerId, String ownerName, String ownerAddr, String ownerImg, String ownerTel, Float price, String description, Integer status, Integer groupStatus, Timestamp startDate, Timestamp endDate, String descriptionMore, BigInteger productNum, String userName) {
        this.id = id;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerAddr = ownerAddr;
        this.ownerImg = ownerImg;
        this.ownerTel = ownerTel;
        this.price = price;
        this.description = description;
        this.status = status;
        this.groupStatus = groupStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.descriptionMore = descriptionMore;
        this.productNum = productNum;
        this.userName = userName;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(BigInteger ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerAddr() {
        return ownerAddr;
    }

    public void setOwnerAddr(String ownerAddr) {
        this.ownerAddr = ownerAddr;
    }

    public String getOwnerImg() {
        return ownerImg;
    }

    public void setOwnerImg(String ownerImg) {
        this.ownerImg = ownerImg;
    }

    public String getOwnerTel() {
        return ownerTel;
    }

    public void setOwnerTel(String ownerTel) {
        this.ownerTel = ownerTel;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(Integer groupStatus) {
        this.groupStatus = groupStatus;
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

    public String getDescriptionMore() {
        return descriptionMore;
    }

    public void setDescriptionMore(String descriptionMore) {
        this.descriptionMore = descriptionMore;
    }

    public BigInteger getProductNum() {
        return productNum;
    }

    public void setProductNum(BigInteger productNum) {
        this.productNum = productNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus_cn() {
        return status_cn;
    }

    public void setStatus_cn(String status_cn) {
        this.status_cn = status_cn;
    }

	public String getShowStatus() {
    	if ("跟团中".equals(status_cn)) {
			this.showStatus = "1";
		} else if ("未开始".equals(status_cn)) {
			this.showStatus = "2";
		} else if ("预览中".equals(status_cn)) {
			this.showStatus = "3";
		} else if ("已结束".equals(status_cn)) {
			this.showStatus = "4";
		} else if ("已删除".equals(status_cn)) {
			this.showStatus = "5";
		}
		return this.showStatus;
	}

	public void setShowStatus(String showStatus) {
		this.showStatus = showStatus;
	}
    
    
}

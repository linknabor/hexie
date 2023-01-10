package com.yumu.hexie.integration.community.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.common.util.DateUtil;

import java.io.Serializable;
import java.math.BigInteger;


public class OutSidDepotResp implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8650824148860656764L;

	//商品信息
	private BigInteger id;	//公众号的商品ID
	private String name;
	@JsonProperty("create_date")
	private BigInteger createDate;
	@JsonProperty("main_picture")
	private String mainPicture;	//封面图链接
	@JsonProperty("small_picture")
	private String smallPicture;	//小图
	private String pictures;	//轮播图，多个逗号分割
	@JsonProperty("mini_price")
	private Float miniPrice;	//结算价
	@JsonProperty("ori_price")
	private Float oriPrice;//市场价
	@JsonProperty("single_price")
	private Float singlePrice;	//销售价
	@JsonProperty("other_desc")
	private String otherDesc;	//描述
	@JsonProperty("total_count")
	private Integer totalCount;	//库存
	@JsonProperty("user_name")
	private String userName; //团长
	@JsonProperty("area_limit")
	private Integer areaLimit;	//商品上架区域限制
	@JsonProperty("group_count")
	private BigInteger groupCount; //团购数
	@JsonProperty("order_num")
	private BigInteger orderNum; //下单数
	
	public OutSidDepotResp(BigInteger id, String name, BigInteger createDate, String mainPicture, String smallPicture, String pictures, Float miniPrice, Float oriPrice, Float singlePrice, String otherDesc, Integer totalCount, String userName, Integer areaLimit, BigInteger groupCount, BigInteger orderNum) {
		this.id = id;
		this.name = name;
		this.createDate = createDate;
		this.mainPicture = mainPicture;
		this.smallPicture = smallPicture;
		this.pictures = pictures;
		this.miniPrice = miniPrice;
		this.oriPrice = oriPrice;
		this.singlePrice = singlePrice;
		this.otherDesc = otherDesc;
		this.totalCount = totalCount;
		this.userName = userName;
		this.areaLimit = areaLimit;
		this.groupCount = groupCount;
		this.orderNum = orderNum;
	}

	public OutSidDepotResp() {
		super();
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

	public String getMainPicture() {
		return mainPicture;
	}

	public void setMainPicture(String mainPicture) {
		this.mainPicture = mainPicture;
	}

	public String getSmallPicture() {
		return smallPicture;
	}

	public void setSmallPicture(String smallPicture) {
		this.smallPicture = smallPicture;
	}

	public String getPictures() {
		return pictures;
	}

	public void setPictures(String pictures) {
		this.pictures = pictures;
	}

	public Float getMiniPrice() {
		return miniPrice;
	}

	public void setMiniPrice(Float miniPrice) {
		this.miniPrice = miniPrice;
	}

	public Float getOriPrice() {
		return oriPrice;
	}

	public void setOriPrice(Float oriPrice) {
		this.oriPrice = oriPrice;
	}

	public Float getSinglePrice() {
		return singlePrice;
	}

	public void setSinglePrice(Float singlePrice) {
		this.singlePrice = singlePrice;
	}

	public String getOtherDesc() {
		return otherDesc;
	}

	public void setOtherDesc(String otherDesc) {
		this.otherDesc = otherDesc;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public BigInteger getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(BigInteger groupCount) {
		this.groupCount = groupCount;
	}

	public BigInteger getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(BigInteger orderNum) {
		this.orderNum = orderNum;
	}

	public String getCreateDate() {
		String formatted = "";
		if (createDate!=null) {
			formatted = DateUtil.dtFormat(createDate.longValue(), DateUtil.dttmSimple);
		}
		return formatted;
	}

	public void setCreateDate(BigInteger createDate) {
		this.createDate = createDate;
	}

	public Integer getAreaLimit() {
		return areaLimit;
	}

	public void setAreaLimit(Integer areaLimit) {
		this.areaLimit = areaLimit;
	}
	
	
}

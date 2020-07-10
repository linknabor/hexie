package com.yumu.hexie.integration.shelf.mapper;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryProductMapper implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8650824148860656764L;
	
	//商品信息
	private BigInteger id;	//公众号的商品ID
	private String name;
	@JsonProperty("product_type")		//驼峰形式的servplat 识别不了
	private String productType;	//产品类型
	@JsonProperty("ori_price")
	private Float oriPrice;//市场价
	@JsonProperty("mini_price")
	private Float miniPrice;	//结算价
	@JsonProperty("single_price")
	private Float singlePrice;	//销售价
	private Integer status;	//状态，0初始 1上架 2下架 3删除
	@JsonProperty("start_date")
	private Timestamp startDate;	//上架日期
	@JsonProperty("end_date")
	private Timestamp endDate;	//下架日期
	@JsonProperty("main_picture")
	private String mainPicture;	//封面图链接
	@JsonProperty("small_picture")
	private String smallPicture;	//小图
	private String pictures;	//轮播图，多个逗号分割
	@JsonProperty("service_desc")
	private String serviceDesc;	//描述
	
	//物业或代理商信息
	@JsonProperty("merchant_name")
	private String merchantName;
	@JsonProperty("merchant_no")
	private String merchantNo;
	
	@JsonProperty("limit_num_once")
	private Integer limitNumOnce;	//限购数
	
	@JsonProperty("sort_no")
	private Integer sortNo;	//商品显示位置
	private BigInteger counts;	//关联小区数
	public QueryProductMapper(BigInteger id, String name, String productType, Float oriPrice, Float miniPrice,
			Float singlePrice, Integer status, Timestamp startDate, Timestamp endDate, String mainPicture,
			String smallPicture, String pictures, String serviceDesc, String merchantName, String merchantNo,
			Integer limitNumOnce, Integer sortNo, BigInteger counts) {
		super();
		this.id = id;
		this.name = name;
		this.productType = productType;
		this.oriPrice = oriPrice;
		this.miniPrice = miniPrice;
		this.singlePrice = singlePrice;
		this.status = status;
		this.startDate = startDate;
		this.endDate = endDate;
		this.mainPicture = mainPicture;
		this.smallPicture = smallPicture;
		this.pictures = pictures;
		this.serviceDesc = serviceDesc;
		this.merchantName = merchantName;
		this.merchantNo = merchantNo;
		this.limitNumOnce = limitNumOnce;
		this.sortNo = sortNo;
		this.counts = counts;
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
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public Float getOriPrice() {
		return oriPrice;
	}
	public void setOriPrice(Float oriPrice) {
		this.oriPrice = oriPrice;
	}
	public Float getMiniPrice() {
		return miniPrice;
	}
	public void setMiniPrice(Float miniPrice) {
		this.miniPrice = miniPrice;
	}
	public Float getSinglePrice() {
		return singlePrice;
	}
	public void setSinglePrice(Float singlePrice) {
		this.singlePrice = singlePrice;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
	public String getServiceDesc() {
		return serviceDesc;
	}
	public void setServiceDesc(String serviceDesc) throws UnsupportedEncodingException {
		byte[]bytes = Base64.getEncoder().encode(serviceDesc.getBytes("utf-8"));
		this.serviceDesc = new String(bytes, "utf-8");
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public String getMerchantNo() {
		return merchantNo;
	}
	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}
	public Integer getLimitNumOnce() {
		return limitNumOnce;
	}
	public void setLimitNumOnce(Integer limitNumOnce) {
		this.limitNumOnce = limitNumOnce;
	}
	public Integer getSortNo() {
		return sortNo;
	}
	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}
	public BigInteger getCounts() {
		return counts;
	}
	public void setCounts(BigInteger counts) {
		this.counts = counts;
	}
	
	
	
	

}

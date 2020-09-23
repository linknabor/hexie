package com.yumu.hexie.integration.eshop.mapper;

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
	private Integer demo;	//是否样板，0否，1是
	@JsonProperty("total_count")
	private BigInteger totalCount;	//库存
	
	//物业或代理商信息
	@JsonProperty("agent_name")
	private String agentName;
	@JsonProperty("agent_no")
	private String agentNo;
	
	@JsonProperty("limit_num_once")
	private Integer limitNumOnce;	//限购数
	
	@JsonProperty("postage_fee")
	private Float postageFee;	//运费
	
	@JsonProperty("free_shipping_num")
	private Integer freeShippingNum;	//包邮件数
	
	@JsonProperty("group_min_num")
	private BigInteger groupMinNum;	//最小成团数量
	
	@JsonProperty("product_category_id")
	private BigInteger productCategoryId;	//商品分类ID
	
	@JsonProperty("sort_no")
	private Integer sortNo;	//商品显示位置
	private String appid;
	private BigInteger counts;	//关联小区数
	@JsonProperty("oper_counts")
	private BigInteger operCounts;	//已配置的服务人员数
	
	public QueryProductMapper(BigInteger id, String name, String productType, Float oriPrice, Float miniPrice,
			Float singlePrice, Integer status, Timestamp startDate, Timestamp endDate, String mainPicture,
			String smallPicture, String pictures, String serviceDesc, Integer demo, BigInteger totalCount, String agentName, String agentNo,
			Integer limitNumOnce, Float postageFee, Integer freeShippingNum, BigInteger groupMinNum, BigInteger productCategoryId, Integer sortNo, String appid, 
			BigInteger counts, BigInteger operCounts) {
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
		this.demo = demo;
		this.totalCount = totalCount;
		this.agentName = agentName;
		this.agentNo = agentNo;
		this.limitNumOnce = limitNumOnce;
		this.postageFee = postageFee;
		this.freeShippingNum = freeShippingNum;
		this.groupMinNum = groupMinNum;
		this.productCategoryId = productCategoryId;
		this.sortNo = sortNo;
		this.appid = appid;
		this.counts = counts;
		this.operCounts = operCounts;
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
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public BigInteger getOperCounts() {
		return operCounts;
	}
	public void setOperCounts(BigInteger operCounts) {
		this.operCounts = operCounts;
	}
	public Integer getDemo() {
		return demo;
	}
	public void setDemo(Integer demo) {
		this.demo = demo;
	}
	public BigInteger getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(BigInteger totalCount) {
		this.totalCount = totalCount;
	}
	public BigInteger getProductCategoryId() {
		return productCategoryId;
	}
	public void setProductCategoryId(BigInteger productCategoryId) {
		this.productCategoryId = productCategoryId;
	}
	public Float getPostageFee() {
		return postageFee;
	}
	public void setPostageFee(Float postageFee) {
		this.postageFee = postageFee;
	}
	public Integer getFreeShippingNum() {
		return freeShippingNum;
	}
	public void setFreeShippingNum(Integer freeShippingNum) {
		this.freeShippingNum = freeShippingNum;
	}
	public BigInteger getGroupMinNum() {
		return groupMinNum;
	}
	public void setGroupMinNum(BigInteger groupMinNum) {
		this.groupMinNum = groupMinNum;
	}
	
	

}

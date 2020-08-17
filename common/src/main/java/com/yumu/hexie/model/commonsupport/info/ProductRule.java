package com.yumu.hexie.model.commonsupport.info;

import java.io.Serializable;
import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.yumu.hexie.model.market.saleplan.SalePlan;

/**
 * 将商品和对应规则放一起。当作商品的缓存用，以免每次购物车操作数据库
 * 
 * @author david
 *
 */
public class ProductRule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5644044378216460486L;

	// 商品信息
	private long id; //规则id
	private String name;	//规则/产品名称
	
	private long merchantId;	//商户id
	private long productId;
	private String productType; // 产品类型
	private Float oriPrice;// 市场价
	private Float miniPrice; // 结算价
	private Float singlePrice; // 销售价
	private int status; // 状态，0初始 1上架 2下架 3删除
	private Date startDate; // 上架日期
	private Date endDate; // 下架日期
	private String mainPicture; // 封面图链接
	private String smallPicture; // 小图
	private String pictures; // 轮播图，多个逗号分割
	private String serviceDesc; // 描述
	private int demo; // 是否样板，0否，1是
	private int totalCount; // 库存
	// 物业或代理商信息
	private long agentId;

	private int salePlanType;
	private Float price;	//页面显示的原价
	private int limitNumOnce; // 限购数
	private Float postageFee; // 运费
	private int freeShippingNum; // 包邮件数
	private long productCategoryId; // 商品分类ID
	private int sortNo; // 商品显示位置
	private String appid;
	
	public <T extends SalePlan> ProductRule(Product product, T salePlan) {
		
		BeanUtils.copyProperties(salePlan, this);
		BeanUtils.copyProperties(product, this);
		
		this.salePlanType = salePlan.getSalePlanType();
		this.productId = product.getId();
		this.id = salePlan.getId();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
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

	public void setServiceDesc(String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}

	public int getDemo() {
		return demo;
	}

	public void setDemo(int demo) {
		this.demo = demo;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public long getAgentId() {
		return agentId;
	}

	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}

	public int getLimitNumOnce() {
		return limitNumOnce;
	}

	public void setLimitNumOnce(int limitNumOnce) {
		this.limitNumOnce = limitNumOnce;
	}

	public Float getPostageFee() {
		return postageFee;
	}

	public void setPostageFee(Float postageFee) {
		this.postageFee = postageFee;
	}

	public int getFreeShippingNum() {
		return freeShippingNum;
	}

	public void setFreeShippingNum(int freeShippingNum) {
		this.freeShippingNum = freeShippingNum;
	}

	public long getProductCategoryId() {
		return productCategoryId;
	}

	public void setProductCategoryId(long productCategoryId) {
		this.productCategoryId = productCategoryId;
	}

	public int getSortNo() {
		return sortNo;
	}

	public void setSortNo(int sortNo) {
		this.sortNo = sortNo;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public int getSalePlanType() {
		return salePlanType;
	}

	public void setSalePlanType(int salePlanType) {
		this.salePlanType = salePlanType;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}
	

}

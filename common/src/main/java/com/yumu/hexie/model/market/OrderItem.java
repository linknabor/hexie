package com.yumu.hexie.model.market;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yumu.hexie.model.BaseModel;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.market.saleplan.SalePlan;

@Entity
public class OrderItem  extends BaseModel {

	private static final long serialVersionUID = -6159495377390849171L;

	private Long collocationId;
	private Long ruleId;
	private Long userId;
	private Integer count = 1;

	private Long productId;
	private int orderType;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.REFRESH }, optional = true)
    @JoinColumn(name = "orderId")
	private ServiceOrder serviceOrder;

	private Float price;	//售价
	private Float oriPrice;	//原价
	
	private Float postageFee;	//商品邮费单价
	private int freeShippingNum;	//商品免邮件数
	
	private Float amount;	//单类商品总价 = price * count
	private Float shipFee;	//单类商品邮费 = postageFee * count，如果免邮费就是0
	
	//产品冗余信息
	private Long merchantId;
	private String productName;
	private String productPic;
	private String productThumbPic;
	private String ruleName;
	private Long productCategoryId;
	
	private Long agentId;
	private String agentName;
	private String agentNo;
	
	private Long couponId;
	private Float couponAmount;
	
	@Transient
	private long totalCount;	//总库存 

	private String code; //核销码
	private int verifyStatus; //1核销 0未核销

	public OrderItem(){}
	@Transient
	public void fillDetail(SalePlan plan,Product product){
//		ruleId = plan.getId();
		orderType = plan.getSalePlanType();
		price = plan.getPrice();
		amount = plan.getPrice() * count;
		ruleName = plan.getName();
		
		productId = product.getId();
		oriPrice = product.getOriPrice();
		merchantId = product.getMerchantId();
		productName = product.getName();
		productPic = product.getMainPicture();
		productThumbPic = product.getSmallPicture();
		productCategoryId = product.getProductCategoryId();
	}
	
	@Transient
	public void fillDetailV3(SalePlan plan,Product product){
		orderType = plan.getSalePlanType();
		price = product.getSinglePrice();
		amount = product.getSinglePrice() * count;
		ruleName = plan.getName();
		
		productId = product.getId();
		oriPrice = product.getOriPrice();
		merchantId = product.getMerchantId();
		productName = product.getName();
		productPic = product.getMainPicture();
		productThumbPic = product.getSmallPicture();
		productCategoryId = product.getProductCategoryId();
	}
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public int getOrderType() {
		return orderType;
	}
	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}
	public Long getRuleId() {
		return ruleId;
	}
	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}
	
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Float getAmount() {
		return amount;
	}
	public void setAmount(Float amount) {
		this.amount = amount;
	}
	public Long getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductPic() {
		return productPic;
	}
	public void setProductPic(String productPic) {
		this.productPic = productPic;
	}
	public String getProductThumbPic() {
		return productThumbPic;
	}
	public void setProductThumbPic(String productThumbPic) {
		this.productThumbPic = productThumbPic;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public ServiceOrder getServiceOrder() {
		return serviceOrder;
	}
	public void setServiceOrder(ServiceOrder serviceOrder) {
		this.serviceOrder = serviceOrder;
	}
	public Long getCollocationId() {
		return collocationId;
	}
	public void setCollocationId(Long collocationId) {
		this.collocationId = collocationId;
	}
	public Float getOriPrice() {
		return oriPrice;
	}
	public void setOriPrice(Float oriPrice) {
		this.oriPrice = oriPrice;
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
	public Float getShipFee() {
		return shipFee;
	}
	public void setShipFee(Float shipFee) {
		this.shipFee = shipFee;
	}
	public Long getProductCategoryId() {
		return productCategoryId;
	}
	public void setProductCategoryId(Long productCategoryId) {
		this.productCategoryId = productCategoryId;
	}
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
	public Float getCouponAmount() {
		return couponAmount;
	}
	public void setCouponAmount(Float couponAmount) {
		this.couponAmount = couponAmount;
	}
	public Long getAgentId() {
		return agentId;
	}
	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public int getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(int verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}

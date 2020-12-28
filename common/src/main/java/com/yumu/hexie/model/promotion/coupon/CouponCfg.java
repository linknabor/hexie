package com.yumu.hexie.model.promotion.coupon;

import java.io.Serializable;
import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yumu.hexie.model.promotion.PromotionConstant;

/**
 * 将红包规则和对应种子放一起。当作红包配置的缓存用，以免每次统计或者领取红包操作数据库
 * @author david
 */
public class CouponCfg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2718592567093917446L;

	private long id;	//规则id
	private String title;	//规则、种子名称
	private int status;	//状态
	
	private long seedId;	//种子ID
	private String seedStr;	//券标识--如果来自订单则适用MD5生成标识
	private int seedType;//1订单分裂  4活动发布
	private String seedImg;//种子图
	private double rate = 1;//总概率
	private Long userId;//如果来自订单，则需要订单拥有者
	private String userImgUrl;//如果来自订单，则需要订单拥有者
	private Long bizId;//原始种子，如订单号 根据seedType而有所区别
    
	private int totalCount = 0;	//总发放数
    private int receivedCount = 0;	//已领取数量
    private int usedCount = 0;	//已使用数量
    
    private float totalAmount = 0f;
    private float receivedAmount = 0f;
    private float usedAmount = 0f;
    
    private float amount;//现金券金额,金额设定即不可修改
    private float usageCondition;//最小金额
    
    private String couponDesc;	//描述
    private String suggestUrl;	//跳转链接
    
    private int itemType = PromotionConstant.COUPON_ITEM_TYPE_ALL;//支持的模块。全部，商品项，服务项，服务类型
    private int supportType;	//适用类型，0全部，1部分支持，2部分不支持
    private String productId;//支持的商品,对集市是商品ID，对服务是服务项
    private Long merchantId;//支持的商户
    private String uProductId;	//不支持的商品
    private Long uMerchantId;	//不支持的商户
    
    private int expiredDays;//现金券超时天数，与下面2项目二填一
    private Date useStartDate;
    private Date useEndDate;
    
	private Date startDate;//发放时间
	private Date endDate;//发放时间
	@JsonIgnore
	private long agentId;	//代理商、合伙人信息
	@JsonIgnore
	private String sectIds;	//支持的小区
	
	public CouponCfg(CouponRule couponRule, CouponSeed couponSeed) {
		super();
		BeanUtils.copyProperties(couponSeed, this);
		BeanUtils.copyProperties(couponRule, this);
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getSeedId() {
		return seedId;
	}
	public void setSeedId(long seedId) {
		this.seedId = seedId;
	}
	public String getSeedStr() {
		return seedStr;
	}
	public void setSeedStr(String seedStr) {
		this.seedStr = seedStr;
	}
	public int getSeedType() {
		return seedType;
	}
	public void setSeedType(int seedType) {
		this.seedType = seedType;
	}
	public String getSeedImg() {
		return seedImg;
	}
	public void setSeedImg(String seedImg) {
		this.seedImg = seedImg;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserImgUrl() {
		return userImgUrl;
	}
	public void setUserImgUrl(String userImgUrl) {
		this.userImgUrl = userImgUrl;
	}
	public Long getBizId() {
		return bizId;
	}
	public void setBizId(Long bizId) {
		this.bizId = bizId;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getReceivedCount() {
		return receivedCount;
	}
	public void setReceivedCount(int receivedCount) {
		this.receivedCount = receivedCount;
	}
	public int getUsedCount() {
		return usedCount;
	}
	public void setUsedCount(int usedCount) {
		this.usedCount = usedCount;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public float getUsageCondition() {
		return usageCondition;
	}
	public void setUsageCondition(float usageCondition) {
		this.usageCondition = usageCondition;
	}
	public String getCouponDesc() {
		return couponDesc;
	}
	public void setCouponDesc(String couponDesc) {
		this.couponDesc = couponDesc;
	}
	public String getSuggestUrl() {
		return suggestUrl;
	}
	public void setSuggestUrl(String suggestUrl) {
		this.suggestUrl = suggestUrl;
	}
	public int getItemType() {
		return itemType;
	}
	public void setItemType(int itemType) {
		this.itemType = itemType;
	}
	public int getSupportType() {
		return supportType;
	}
	public void setSupportType(int supportType) {
		this.supportType = supportType;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public Long getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}
	public String getuProductId() {
		return uProductId;
	}
	public void setuProductId(String uProductId) {
		this.uProductId = uProductId;
	}
	public Long getuMerchantId() {
		return uMerchantId;
	}
	public void setuMerchantId(Long uMerchantId) {
		this.uMerchantId = uMerchantId;
	}
	public int getExpiredDays() {
		return expiredDays;
	}
	public void setExpiredDays(int expiredDays) {
		this.expiredDays = expiredDays;
	}
	public Date getUseStartDate() {
		return useStartDate;
	}
	public void setUseStartDate(Date useStartDate) {
		this.useStartDate = useStartDate;
	}
	public Date getUseEndDate() {
		return useEndDate;
	}
	public void setUseEndDate(Date useEndDate) {
		this.useEndDate = useEndDate;
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
	public long getAgentId() {
		return agentId;
	}
	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}
	public String getSectIds() {
		return sectIds;
	}
	public void setSectIds(String sectIds) {
		this.sectIds = sectIds;
	}
	public float getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(float totalAmount) {
		this.totalAmount = totalAmount;
	}
	public float getReceivedAmount() {
		return receivedAmount;
	}
	public void setReceivedAmount(float receivedAmount) {
		this.receivedAmount = receivedAmount;
	}
	public float getUsedAmount() {
		return usedAmount;
	}
	public void setUsedAmount(float usedAmount) {
		this.usedAmount = usedAmount;
	}
	@Override
	public String toString() {
		return "CouponCfg [id=" + id + ", title=" + title + ", status=" + status + ", seedId=" + seedId + ", seedStr="
				+ seedStr + ", seedType=" + seedType + ", seedImg=" + seedImg + ", rate=" + rate + ", userId=" + userId
				+ ", userImgUrl=" + userImgUrl + ", bizId=" + bizId + ", totalCount=" + totalCount + ", receivedCount="
				+ receivedCount + ", usedCount=" + usedCount + ", totalAmount=" + totalAmount + ", receivedAmount="
				+ receivedAmount + ", usedAmount=" + usedAmount + ", amount=" + amount + ", usageCondition="
				+ usageCondition + ", couponDesc=" + couponDesc + ", suggestUrl=" + suggestUrl + ", itemType="
				+ itemType + ", supportType=" + supportType + ", productId=" + productId + ", merchantId=" + merchantId
				+ ", uProductId=" + uProductId + ", uMerchantId=" + uMerchantId + ", expiredDays=" + expiredDays
				+ ", useStartDate=" + useStartDate + ", useEndDate=" + useEndDate + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", agentId=" + agentId + ", sectIds=" + sectIds + "]";
	}
	
    
}

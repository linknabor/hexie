package com.yumu.hexie.model.promotion.coupon;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.springframework.beans.BeanUtils;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.model.BaseModel;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.promotion.PromotionConstant;
import com.yumu.hexie.model.user.User;
@Entity
public class Coupon extends BaseModel {

	private static final long serialVersionUID = -3121621621439075670L;

	//这两项确定一个现金券
	private long seedId;
	private long userId;
	//这两项随机获取
	private long ruleId;
	
	private boolean empty = false;
	private Long orderId;
	private Date useStartDate;
	private Date expiredDate;//现金券超时日期
	private int status = ModelConstant.COUPON_STATUS_AVAILABLE;//0可用 1已使用 2超时
	
	private Date usedDate;//使用日期

	@Transient
	private boolean selected = false;
	/**冗余信息**/
	@Column(updatable=false)
	private String title;//现金券名称
	@Column(updatable=false)
	private float amount;//现金券金额
	
	private String userHeadImg;
	private String userName;
	private String tel;
	private String openid;
	
	private int seedType;
	private String seedStr;
	private String couponDesc;
	/**冗余信息**/
	
	/**************现金券适用范围**************/
	private float usageCondition;//最小金额
    private boolean availableForAll = true;//与以下互斥
    private int supportType;	//适用类型，0全部，1部分支持，2部分不支持
    
    //支持项目，不支持的优先过滤
    private int itemType = PromotionConstant.COUPON_ITEM_TYPE_ALL;//全部，商品项，服务项，服务类型
    private Long subItemType;//子类型，默认为空  对服务是base的serviceType，对集市是销售方案
    private Long serviceType;//低于subItemType,如对洗衣是按件洗、按袋洗。对保洁是日常保洁，深度保洁。对特卖是频道
    private String productId;//对集市是商品ID，对服务是服务项
    private Long merchantId;//商户类型
    
    //不支持项目
    private Integer uItemType;
    private Long uSubItemType;
    private Long uServiceType;
    private String uProductId;
    private Long uMerchantId;
	/**************现金券适用范围**************/
    
	private String suggestUrl;
	private String appid;	//所属平台
	
	private long agentId;	//代理商
	private String agentName;
	private String agentNo;
	
	private String orderNo;	//物业订单号
	
	@Transient
	public String getUseStartDateStr(){
		return DateUtil.dtFormat(useStartDate, "yyyy.MM.dd");
	}

	@Transient
	public String getUseEndDateStr(){
		return DateUtil.dtFormat(expiredDate, "yyyy.MM.dd");
	}
	@Transient
	public String getLeftDayDes(){
		if(status == ModelConstant.COUPON_STATUS_USED) {
			return "已使用";
		}
		long currTime = System.currentTimeMillis();
		int days = DateUtil.getDurationDays(currTime, expiredDate.getTime());
		if(days<0) {
			return "已过期";
		} else if(days == 0) {
			if (currTime > expiredDate.getTime() ) {
				return "已过期";
			}
			return "今天到期";
		} else {
			return days+"天后过期";
		}
	}
	public Coupon(){}
	public static Coupon emptyCoupon(CouponSeed seed,User user) {
		Coupon c = new Coupon();
		c.seedId = seed.getId();
		c.seedType = seed.getSeedType();
		c.setEmpty(true);
		c.userId = user.getId();
		c.setUserName(user.getName());
		c.setUserHeadImg(user.getHeadimgurl());
		c.setTel(user.getTel());
		c.setOpenid(user.getOpenid());
		return c;
	}
	public Coupon(CouponSeed seed,CouponRule rule,User user) {
		seedId = seed.getId();
		seedType = seed.getSeedType();
		
		ruleId = rule.getId();
		
		BeanUtils.copyProperties(rule, this);
		if(rule.getExpiredDays()>0) {
			useStartDate = new Date();
			expiredDate = DateUtil.addDate(new Date(),rule.getExpiredDays());
		} else {
			useStartDate = rule.getUseStartDate();
			expiredDate = rule.getUseEndDate();
		}

		setId(0l);
		this.userId = user.getId();
		super.setCreateDate(System.currentTimeMillis());
		setUserName(user.getName());
		setUserHeadImg(user.getHeadimgurl());
		setTel(user.getTel());
		setOpenid(user.getOpenid());
	}

	@Transient
	public void lock(long orderId) {
		this.orderId = orderId;
		this.status = ModelConstant.COUPON_STATUS_LOCKED;
	}
	@Transient
	public void unlock() {
		this.orderId = 0l;
		this.status = ModelConstant.COUPON_STATUS_AVAILABLE;
	}
	@Transient
	public void cousume(long orderId) {
		this.orderId = orderId;
		this.status = ModelConstant.COUPON_STATUS_USED;
		this.usedDate = new Date();
	}
	
	@Transient
	public void cousume(long orderId, String orderNo) {
		this.orderId = orderId;
		this.status = ModelConstant.COUPON_STATUS_USED;
		this.usedDate = new Date();
		this.orderNo = orderNo;
	}
	
	public void timeout(){
		this.orderId = 0l;
		this.status = ModelConstant.COUPON_STATUS_TIMEOUT;
	}
	public long getSeedId() {
		return seedId;
	}
	public void setSeedId(long seedId) {
		this.seedId = seedId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Date getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public Date getUsedDate() {
		return usedDate;
	}
	public void setUsedDate(Date usedDate) {
		this.usedDate = usedDate;
	}
	public String getUserHeadImg() {
		return userHeadImg;
	}
	public void setUserHeadImg(String userHeadImg) {
		this.userHeadImg = userHeadImg;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public float getUsageCondition() {
		return usageCondition;
	}
	public void setUsageCondition(float usageCondition) {
		this.usageCondition = usageCondition;
	}
	public boolean isAvailableForAll() {
		return availableForAll;
	}
	public void setAvailableForAll(boolean availableForAll) {
		this.availableForAll = availableForAll;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public Date getUseStartDate() {
		return useStartDate;
	}
	public void setUseStartDate(Date useStartDate) {
		this.useStartDate = useStartDate;
	}
	public Long getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}
	public int getSeedType() {
		return seedType;
	}
	public void setSeedType(int seedType) {
		this.seedType = seedType;
	}
	public boolean isEmpty() {
		return empty;
	}
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getSeedStr() {
		return seedStr;
	}

	public void setSeedStr(String seedStr) {
		this.seedStr = seedStr;
	}

	public String getCouponDesc() {
		return couponDesc;
	}

	public void setCouponDesc(String couponDesc) {
		this.couponDesc = couponDesc;
	}
	public String getCreateDateStr(){
		return DateUtil.dtFormat(new Date(getCreateDate()), "yyyy-MM-dd HH:mm");
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
    public Long getSubItemType() {
        return subItemType;
    }

    public void setSubItemType(Long subItemType) {
        this.subItemType = subItemType;
    }

    public Long getServiceType() {
        return serviceType;
    }

    public void setServiceType(Long serviceType) {
        this.serviceType = serviceType;
    }

    public Integer getuItemType() {
        return uItemType;
    }

    public void setuItemType(Integer uItemType) {
        this.uItemType = uItemType;
    }

    public Long getuSubItemType() {
        return uSubItemType;
    }

    public void setuSubItemType(Long uSubItemType) {
        this.uSubItemType = uSubItemType;
    }

    public Long getuServiceType() {
        return uServiceType;
    }

    public void setuServiceType(Long uServiceType) {
        this.uServiceType = uServiceType;
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
    

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public long getAgentId() {
		return agentId;
	}

	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}

	public int getSupportType() {
		return supportType;
	}

	public void setSupportType(int supportType) {
		this.supportType = supportType;
	}
	
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
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

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Override
	public String toString() {
		return "Coupon [seedId=" + seedId + ", userId=" + userId + ", ruleId=" + ruleId + ", empty=" + empty
				+ ", orderId=" + orderId + ", useStartDate=" + useStartDate + ", expiredDate=" + expiredDate
				+ ", status=" + status + ", usedDate=" + usedDate + ", selected=" + selected + ", title=" + title
				+ ", amount=" + amount + ", userHeadImg=" + userHeadImg + ", userName=" + userName + ", tel=" + tel
				+ ", openid=" + openid + ", seedType=" + seedType + ", seedStr=" + seedStr + ", couponDesc="
				+ couponDesc + ", usageCondition=" + usageCondition + ", availableForAll=" + availableForAll
				+ ", supportType=" + supportType + ", itemType=" + itemType + ", subItemType=" + subItemType
				+ ", serviceType=" + serviceType + ", productId=" + productId + ", merchantId=" + merchantId
				+ ", uItemType=" + uItemType + ", uSubItemType=" + uSubItemType + ", uServiceType=" + uServiceType
				+ ", uProductId=" + uProductId + ", uMerchantId=" + uMerchantId + ", suggestUrl=" + suggestUrl
				+ ", appid=" + appid + ", agentId=" + agentId + ", agentName=" + agentName + ", agentNo=" + agentNo
				+ ", orderNo=" + orderNo + "]";
	}

	
}

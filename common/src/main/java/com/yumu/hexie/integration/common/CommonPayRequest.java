package com.yumu.hexie.integration.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.integration.customservice.dto.CustomerServiceOrderDTO;
import com.yumu.hexie.service.exception.BizValidateException;

public class CommonPayRequest extends CommonRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5559168361712488423L;
	@JsonProperty("user_id")
	private String userId;
	@JsonProperty("service_id")
	private String serviceId;
	@JsonProperty("sect_id")
	private String sectId;
	@JsonProperty("link_man")
	private String linkman;
	@JsonProperty("link_tel")
	private String linktel;
	@JsonProperty("service_addr")
	private String serviceAddr;
	private String appid;
	private String openid;
	@JsonProperty("tran_amt")
	private String tranAmt;
	@JsonProperty("trade_water_id")
	private String tradeWaterId;
	@JsonProperty("order_type")
	private String orderType;
	@JsonProperty("service_name")
	private String serviceName;
	@JsonProperty("agent_name")
	private String agentName;
	@JsonProperty("agent_no")
	private String agentNo;
	private String count;
	@JsonProperty("sub_orders")
	private List<SubOrder> subOrders;
	@JsonProperty("coupon_id")
	private String couponId;
	@JsonProperty("coupon_amt")
	private String couponAmt;
	@JsonProperty("ship_fee")
	private String shipFee;
	private String memo; //客户描述
	private String imgUrls; //客户上传图片地址
	@JsonProperty("pay_method")
	private String payMethod;
	private String miniopenid;
	private String miniappid;
	@JsonProperty("group_owner_id")
	private String ownerId;
	@JsonProperty("group_owner_name")
	private String ownerName;	//团长
	@JsonProperty("group_owner_tel")
	private String ownerTel;	//团长手机
	@JsonProperty("group_rule_id")
	private String ruleId;	//团id
	@JsonProperty("group_rule_name")
	private String ruleDescription;	//团名
	
	public CommonPayRequest() {
		super();
	}

	public CommonPayRequest(CustomerServiceOrderDTO dto) {
	
		BeanUtils.copyProperties(dto, this);
		this.appid = dto.getUser().getAppId();
		this.sectId = dto.getUser().getSectId();
		this.userId = dto.getUser().getWuyeId();
		this.openid = dto.getUser().getOpenid();
		//中文打码
		try {
			if (!StringUtils.isEmpty(dto.getLinkman())) {
				this.linkman = URLEncoder.encode(dto.getLinkman(),"GBK");
			}
			if (!StringUtils.isEmpty(dto.getServiceAddr())) {
				this.serviceAddr = URLEncoder.encode(dto.getServiceAddr(),"GBK");
			}
			if (!StringUtils.isEmpty(dto.getServiceName())) {
				this.serviceName = URLEncoder.encode(dto.getServiceName(),"GBK");
			}
			if (!StringUtils.isEmpty(dto.getMemo())) {
				this.memo = URLEncoder.encode(dto.getMemo(),"GBK");
			}
		} catch (UnsupportedEncodingException e) {
			throw new BizValidateException(e.getMessage(), e);	
		}
		
	}
	
	public static class SubOrder {
		
		@JsonProperty("sub_product_name")
		private String productName;
		@JsonProperty("sub_product_id")
		private Long productId;
		@JsonProperty("sub_agent_no")
		private String agentNo;
		@JsonProperty("sub_agent_name")
		private String agentName;
		@JsonProperty("sub_count")
		private int count;
		@JsonProperty("sub_amount")
		private Float amount;
		@JsonProperty("sub_coupon_id")
		private Long subCouponId;
		@JsonProperty("sub_coupon_amt")
		private Float subCouponAmt;
		@JsonProperty("sub_pic")
		private String subPic;
		@JsonProperty("sub_mini_price")
		private String miniPrice;
		
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public String getAgentNo() {
			return agentNo;
		}
		public void setAgentNo(String agentNo) {
			this.agentNo = agentNo;
		}
		public String getAgentName() {
			return agentName;
		}
		public void setAgentName(String agentName) {
			this.agentName = agentName;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public Float getAmount() {
			return amount;
		}
		public void setAmount(Float amount) {
			this.amount = amount;
		}
		public Long getProductId() {
			return productId;
		}
		public void setProductId(Long productId) {
			this.productId = productId;
		}
		public Long getSubCouponId() {
			return subCouponId;
		}
		public void setSubCouponId(Long subCouponId) {
			this.subCouponId = subCouponId;
		}
		public Float getSubCouponAmt() {
			return subCouponAmt;
		}
		public void setSubCouponAmt(Float subCouponAmt) {
			this.subCouponAmt = subCouponAmt;
		}
		public String getSubPic() {
			return subPic;
		}
		public void setSubPic(String subPic) {
			this.subPic = subPic;
		}
		public String getMiniPrice() {
			return miniPrice;
		}
		public void setMiniPrice(String miniPrice) {
			this.miniPrice = miniPrice;
		}
		@Override
		public String toString() {
			return "SubOrder [productName=" + productName + ", productId=" + productId + ", agentNo=" + agentNo
					+ ", agentName=" + agentName + ", count=" + count + ", amount=" + amount + ", subCouponId="
					+ subCouponId + ", subCouponAmt=" + subCouponAmt + ", subPic=" + subPic + ", miniPrice=" + miniPrice
					+ "]";
		}
		
	}
	
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getLinkman() {
		return linkman;
	}
	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}
	public String getLinktel() {
		return linktel;
	}
	public void setLinktel(String linktel) {
		this.linktel = linktel;
	}
	public String getServiceAddr() {
		return serviceAddr;
	}
	public void setServiceAddr(String serviceAddr) {
		this.serviceAddr = serviceAddr;
	}
	public String getTranAmt() {
		return tranAmt;
	}
	public void setTranAmt(String tranAmt) {
		this.tranAmt = tranAmt;
	}
	public String getTradeWaterId() {
		return tradeWaterId;
	}
	public void setTradeWaterId(String tradeWaterId) {
		this.tradeWaterId = tradeWaterId;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
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

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public List<SubOrder> getSubOrders() {
		return subOrders;
	}

	public void setSubOrders(List<SubOrder> subOrders) {
		this.subOrders = subOrders;
	}

	public String getCouponId() {
		return couponId;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}

	public String getCouponAmt() {
		return couponAmt;
	}

	public void setCouponAmt(String couponAmt) {
		this.couponAmt = couponAmt;
	}

	public String getShipFee() {
		return shipFee;
	}

	public void setShipFee(String shipFee) {
		this.shipFee = shipFee;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getImgUrls() {
		return imgUrls;
	}

	public void setImgUrls(String imgUrls) {
		this.imgUrls = imgUrls;
	}

	public String getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}

	public String getMiniopenid() {
		return miniopenid;
	}

	public void setMiniopenid(String miniopenid) {
		this.miniopenid = miniopenid;
	}

	public String getMiniappid() {
		return miniappid;
	}

	public void setMiniappid(String miniappid) {
		this.miniappid = miniappid;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerTel() {
		return ownerTel;
	}

	public void setOwnerTel(String ownerTel) {
		this.ownerTel = ownerTel;
	}

	public String getRuleDescription() {
		return ruleDescription;
	}

	public void setRuleDescription(String ruleDescription) {
		this.ruleDescription = ruleDescription;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public String toString() {
		return "CommonPayRequest [userId=" + userId + ", serviceId=" + serviceId + ", sectId=" + sectId + ", linkman="
				+ linkman + ", linktel=" + linktel + ", serviceAddr=" + serviceAddr + ", appid=" + appid + ", openid="
				+ openid + ", tranAmt=" + tranAmt + ", tradeWaterId=" + tradeWaterId + ", orderType=" + orderType
				+ ", serviceName=" + serviceName + ", agentName=" + agentName + ", agentNo=" + agentNo + ", count="
				+ count + ", subOrders=" + subOrders + ", couponId=" + couponId + ", couponAmt=" + couponAmt
				+ ", shipFee=" + shipFee + ", memo=" + memo + ", imgUrls=" + imgUrls + ", payMethod=" + payMethod
				+ ", miniopenid=" + miniopenid + ", miniappid=" + miniappid + ", ownerId=" + ownerId + ", ownerName="
				+ ownerName + ", ownerTel=" + ownerTel + ", ruleId=" + ruleId + ", ruleDescription=" + ruleDescription
				+ "]";
	}

	
}

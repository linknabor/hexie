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
		} catch (UnsupportedEncodingException e) {
			throw new BizValidateException(e.getMessage(), e);	
		}
		
	}
	
	public static class SubOrder {
		
		@JsonProperty("sub_product_name")
		private String productName;
		@JsonProperty("sub_product_id")
		private String productId;
		@JsonProperty("sub_agent_no")
		private String agentNo;
		@JsonProperty("sub_agent_name")
		private String agentName;
		@JsonProperty("sub_count")
		private int count;
		@JsonProperty("sub_amount")
		private float amount;
		
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
		public float getAmount() {
			return amount;
		}
		public void setAmount(float amount) {
			this.amount = amount;
		}
		public String getProductId() {
			return productId;
		}
		public void setProductId(String productId) {
			this.productId = productId;
		}
		@Override
		public String toString() {
			return "SubOrder [productName=" + productName + ", productId=" + productId + ", agentNo=" + agentNo
					+ ", agentName=" + agentName + ", count=" + count + ", amount=" + amount + "]";
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

	@Override
	public String toString() {
		return "CommonPayRequest [userId=" + userId + ", serviceId=" + serviceId + ", sectId=" + sectId + ", linkman="
				+ linkman + ", linktel=" + linktel + ", serviceAddr=" + serviceAddr + ", appid=" + appid + ", openid="
				+ openid + ", tranAmt=" + tranAmt + ", tradeWaterId=" + tradeWaterId + ", orderType=" + orderType
				+ ", serviceName=" + serviceName + ", agentName=" + agentName + ", agentNo=" + agentNo + ", count="
				+ count + ", subOrders=" + subOrders + "]";
	}

	
}

package com.yumu.hexie.integration.notify;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.model.user.User;

public class PayNotifyDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2053345074969873725L;
	
	private String orderId;
	private String cardNo;
	private String quickToken;
	private String wuyeId;
	private String couponId;
	private String points;
	private String bindSwitch = "1";
	
	@JsonProperty("account_notify")
	private AccountNotify accountNotify;	//入账通知
	@JsonProperty("receivOrder")
	private ServiceNotify serviceNotify;	//服务通知
	
	public AccountNotify getAccountNotify() {
		return accountNotify;
	}

	public void setAccountNotify(AccountNotify accountNotify) {
		this.accountNotify = accountNotify;
	}

	public ServiceNotify getServiceNotify() {
		return serviceNotify;
	}

	public void setServiceNotify(ServiceNotify serviceNotify) {
		this.serviceNotify = serviceNotify;
	}

	/**
	 * 物业入账通知
	 * @author david
	 *
	 */
	public static class AccountNotify {
		
		private User user;
		@JsonProperty("tran_date")
		private String tranDate;
		private BigDecimal feePrice;
		@JsonProperty("pay_method")
		private String payMethod;	//支付方式
		@JsonProperty("fee_name")
		private String feeName;	//费项名称
		private String remark;	//备注
		private List<Map<String, String>> openids;	//本次支付需要通知的用户id列表
		public User getUser() {
			return user;
		}
		public void setUser(User user) {
			this.user = user;
		}
		public String getPayMethod() {
			return payMethod;
		}
		public void setPayMethod(String payMethod) {
			this.payMethod = payMethod;
		}
		public String getFeeName() {
			return feeName;
		}
		public void setFeeName(String feeName) {
			this.feeName = feeName;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		public List<Map<String, String>> getOpenids() {
			return openids;
		}
		public void setOpenids(List<Map<String, String>> openids) {
			this.openids = openids;
		}
		public String getTranDate() {
			return tranDate;
		}
		public void setTranDate(String tranDate) {
			this.tranDate = tranDate;
		}
		public BigDecimal getFeePrice() {
			return feePrice;
		}
		public void setFeePrice(BigDecimal feePrice) {
			this.feePrice = BigDecimal.ZERO;
			if (!StringUtils.isEmpty(feePrice)) {
				this.feePrice = feePrice.divide(new BigDecimal("100"));
			}
		}
		
		@Override
		public String toString() {
			return "AccountNotify [user=" + user + ", tranDate=" + tranDate + ", feePrice=" + feePrice + ", payMethod="
					+ payMethod + ", feeName=" + feeName + ", remark=" + remark + ", openids=" + openids + "]";
		}
			
		
	}
	
	/**
	 * 服务通知
	 * @author david
	 *
	 */
	public static class ServiceNotify {
		
		private User user;
		private String orderId;
		private List<Map<String, String>> openids;	//本次支付需要通知的用户id列表 

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		public List<Map<String, String>> getOpenids() {
			return openids;
		}

		public void setOpenids(List<Map<String, String>> openids) {
			this.openids = openids;
		}

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		@Override
		public String toString() {
			return "ServiceNotify [user=" + user + ", orderId=" + orderId + ", openids=" + openids + "]";
		}

	}
	
	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getQuickToken() {
		return quickToken;
	}

	public void setQuickToken(String quickToken) {
		this.quickToken = quickToken;
	}

	public String getWuyeId() {
		return wuyeId;
	}

	public void setWuyeId(String wuyeId) {
		this.wuyeId = wuyeId;
	}

	public String getCouponId() {
		return couponId;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}
	
	public String getBindSwitch() {
		return bindSwitch;
	}

	public void setBindSwitch(String bindSwitch) {
		this.bindSwitch = bindSwitch;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Override
	public String toString() {
		return "PayNotifyDTO [orderId=" + orderId + ", cardNo=" + cardNo + ", quickToken=" + quickToken + ", wuyeId="
				+ wuyeId + ", couponId=" + couponId + ", points=" + points + ", bindSwitch=" + bindSwitch
				+ ", accountNotify=" + accountNotify + ", serviceNotify=" + serviceNotify + "]";
	}
	
	
}

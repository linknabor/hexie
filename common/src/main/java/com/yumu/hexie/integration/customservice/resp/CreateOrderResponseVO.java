package com.yumu.hexie.integration.customservice.resp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.integration.notify.PayNotification.ServiceNotification;

public class CreateOrderResponseVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2249103405156659481L;
	
	@JsonProperty("package")
	private String pack;
	@JsonProperty("token_id")
	private String token;
	@JsonProperty("trade_water_id")
	private String tradeWaterId;
	private String appid;
	private String signtype;
	private String paysign;
	private String noncestr;
	private String timestamp;
	@JsonProperty("user_pay_type")
	private String payType;
	private String orderId;
	@JsonProperty("receivOrder")
	private ServiceNotification serviceNotification;	//服务通知
	
	public String getPack() {
		return pack;
	}
	public void setPack(String pack) {
		this.pack = pack;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getSigntype() {
		return signtype;
	}
	public void setSigntype(String signtype) {
		this.signtype = signtype;
	}
	public String getPaysign() {
		return paysign;
	}
	public void setPaysign(String paysign) {
		this.paysign = paysign;
	}
	public String getNoncestr() {
		return noncestr;
	}
	public void setNoncestr(String noncestr) {
		this.noncestr = noncestr;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getTradeWaterId() {
		return tradeWaterId;
	}
	public void setTradeWaterId(String tradeWaterId) {
		this.tradeWaterId = tradeWaterId;
	}
	public ServiceNotification getServiceNotification() {
		return serviceNotification;
	}
	public void setServiceNotification(ServiceNotification serviceNotification) {
		this.serviceNotification = serviceNotification;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	@Override
	public String toString() {
		return "CreateOrderResponseVO [pack=" + pack + ", token=" + token + ", tradeWaterId=" + tradeWaterId
				+ ", appid=" + appid + ", signtype=" + signtype + ", paysign=" + paysign + ", noncestr=" + noncestr
				+ ", timestamp=" + timestamp + ", payType=" + payType + ", orderId=" + orderId
				+ ", serviceNotification=" + serviceNotification + "]";
	}
	

}

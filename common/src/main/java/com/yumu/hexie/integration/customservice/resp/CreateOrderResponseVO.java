package com.yumu.hexie.integration.customservice.resp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	private String orderId;
	private String appid;
	private String signtype;
	private String paysign;
	private String noncestr;
	private String timestamp;
	@JsonProperty("user_pay_type")
	private String payType;
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
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
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
	@Override
	public String toString() {
		return "CreateOrderResponseVO [pack=" + pack + ", token=" + token + ", orderId=" + orderId + ", appid=" + appid
				+ ", signtype=" + signtype + ", paysign=" + paysign + ", noncestr=" + noncestr + ", timestamp="
				+ timestamp + ", payType=" + payType + "]";
	}
	
	

}

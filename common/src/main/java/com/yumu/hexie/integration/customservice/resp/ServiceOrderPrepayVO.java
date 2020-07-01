package com.yumu.hexie.integration.customservice.resp;

import java.io.Serializable;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceOrderPrepayVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3491495123484805322L;

	@JsonProperty("package")
	private String pack;
	@JsonProperty("token_id")
	private String token;
	private String appid;
	private String signtype;
	private String paysign;
	private String noncestr;
	private String timestamp;
	private String orderId;
	
	public ServiceOrderPrepayVO() {
	
	}
	
	public ServiceOrderPrepayVO(CreateOrderResponseVO createOrderResponseVO) {
		
		BeanUtils.copyProperties(createOrderResponseVO, this);
	}
	
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
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	@Override
	public String toString() {
		return "ServiceOrderPrepayVO [pack=" + pack + ", token=" + token + ", appid=" + appid + ", signtype=" + signtype
				+ ", paysign=" + paysign + ", noncestr=" + noncestr + ", timestamp=" + timestamp + ", orderId="
				+ orderId + "]";
	}
	
	
}

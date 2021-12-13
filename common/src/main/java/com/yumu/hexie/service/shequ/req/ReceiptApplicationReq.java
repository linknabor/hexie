package com.yumu.hexie.service.shequ.req;

import java.io.Serializable;

public class ReceiptApplicationReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4537874723456689605L;
	
	private String tradeWaterId;
	private String mobile;
	private String vericode;
	private String openid;
	private String event;
	private String appid;
	
	public String getTradeWaterId() {
		return tradeWaterId;
	}
	public void setTradeWaterId(String tradeWaterId) {
		this.tradeWaterId = tradeWaterId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getVericode() {
		return vericode;
	}
	public void setVericode(String vericode) {
		this.vericode = vericode;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	@Override
	public String toString() {
		return "ReceiptApplicationReq [tradeWaterId=" + tradeWaterId + ", mobile=" + mobile + ", vericode=" + vericode
				+ ", openid=" + openid + ", event=" + event + ", appid=" + appid + "]";
	}
	

}

package com.yumu.hexie.service.shequ.req;

import java.io.Serializable;

public class InvoiceApplicationReq implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4065290843408664883L;
	
	private String mobile;
	private String invoice_title;
	private String invoice_title_type;
	private String yzm;
	private String trade_water_id;
	private String credit_code;
	private String openid;
	private String event;
	private String appid;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getInvoice_title() {
		return invoice_title;
	}
	public void setInvoice_title(String invoice_title) {
		this.invoice_title = invoice_title;
	}
	public String getInvoice_title_type() {
		return invoice_title_type;
	}
	public void setInvoice_title_type(String invoice_title_type) {
		this.invoice_title_type = invoice_title_type;
	}
	public String getYzm() {
		return yzm;
	}
	public void setYzm(String yzm) {
		this.yzm = yzm;
	}
	public String getTrade_water_id() {
		return trade_water_id;
	}
	public void setTrade_water_id(String trade_water_id) {
		this.trade_water_id = trade_water_id;
	}
	public String getCredit_code() {
		return credit_code;
	}
	public void setCredit_code(String credit_code) {
		this.credit_code = credit_code;
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
		return "InvoiceApplicationReq [mobile=" + mobile + ", invoice_title=" + invoice_title + ", invoice_title_type="
				+ invoice_title_type + ", yzm=" + yzm + ", trade_water_id=" + trade_water_id + ", credit_code="
				+ credit_code + ", openid=" + openid + ", event=" + event + ", appid=" + appid + "]";
	}
	
	
	
}

package com.yumu.hexie.integration.wuye.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BillDetailRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1449819414042950864L;
	
	@JsonProperty("user_id")
	private String wuyeId;
	@JsonProperty("stmt_id")
	private String stmtId;
	@JsonProperty("bill_id")
	private String billId;	//多个，以逗号分隔
	private String openid;
	private String appid;
	
	public String getWuyeId() {
		return wuyeId;
	}
	public void setWuyeId(String wuyeId) {
		this.wuyeId = wuyeId;
	}
	public String getStmtId() {
		return stmtId;
	}
	public void setStmtId(String stmtId) {
		this.stmtId = stmtId;
	}
	public String getBillId() {
		return billId;
	}
	public void setBillId(String billId) {
		this.billId = billId;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	
	
}

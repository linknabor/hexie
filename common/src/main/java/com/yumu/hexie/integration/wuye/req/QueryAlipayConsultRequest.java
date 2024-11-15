package com.yumu.hexie.integration.wuye.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryAlipayConsultRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2093197329478233981L;

	@JsonProperty("appid")
	private String aliAppId;
	@JsonProperty("user_id")
	private String aliUserId;
	@JsonProperty("sect_id")
	private String sectId;
	@JsonProperty("tran_amt")
	private String tran_amt;
	@JsonProperty("out_openid")
	private String outOpenid;
	
	public String getAliAppId() {
		return aliAppId;
	}
	public void setAliAppId(String aliAppId) {
		this.aliAppId = aliAppId;
	}
	public String getAliUserId() {
		return aliUserId;
	}
	public void setAliUserId(String aliUserId) {
		this.aliUserId = aliUserId;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getTran_amt() {
		return tran_amt;
	}
	public void setTran_amt(String tran_amt) {
		this.tran_amt = tran_amt;
	}
	public String getOutOpenid() {
		return outOpenid;
	}
	public void setOutOpenid(String outOpenid) {
		this.outOpenid = outOpenid;
	}
	@Override
	public String toString() {
		return "QueryAlipayConsultRequest [aliAppId=" + aliAppId + ", aliUserId=" + aliUserId + ", sectId=" + sectId
				+ ", tran_amt=" + tran_amt + ", outOpenid=" + outOpenid + "]";
	}
	
	
}

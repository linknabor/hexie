package com.yumu.hexie.integration.wechat.entity.card;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateUserCardResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7301193071043309544L;
	
	private String errcode;
	private String errmsg;
	@JsonProperty("result_bonus")
	private String resultBonus;
	@JsonProperty("result_balance")
	private String resultBalance;
	private String openid;
	
	public String getErrcode() {
		return errcode;
	}
	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	public String getResultBonus() {
		return resultBonus;
	}
	public void setResultBonus(String resultBonus) {
		this.resultBonus = resultBonus;
	}
	public String getResultBalance() {
		return resultBalance;
	}
	public void setResultBalance(String resultBalance) {
		this.resultBalance = resultBalance;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	@Override
	public String toString() {
		return "UpdateUserCardResp [errcode=" + errcode + ", errmsg=" + errmsg + ", resultBonus=" + resultBonus
				+ ", resultBalance=" + resultBalance + ", openid=" + openid + "]";
	}
	
	

}

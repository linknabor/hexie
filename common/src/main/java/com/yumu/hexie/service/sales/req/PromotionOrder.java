package com.yumu.hexie.service.sales.req;

import java.io.Serializable;

public class PromotionOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8690857153975384483L;
	
	private Long province;
	private Long city;
	private Long county;
	private String sectName;
	private String name;	//用户姓名
	private String mobile;	//手机号
	private String code;	//验证码
	private Long ruleId;	//规则ID
	private String shareCode;	//分享链接的userId
	
	public Long getProvince() {
		return province;
	}
	public void setProvince(Long province) {
		this.province = province;
	}
	public Long getCity() {
		return city;
	}
	public void setCity(Long city) {
		this.city = city;
	}
	public Long getCounty() {
		return county;
	}
	public void setCounty(Long county) {
		this.county = county;
	}
	public String getSectName() {
		return sectName;
	}
	public void setSectName(String sectName) {
		this.sectName = sectName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Long getRuleId() {
		return ruleId;
	}
	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}
	public String getShareCode() {
		return shareCode;
	}
	public void setShareCode(String shareCode) {
		this.shareCode = shareCode;
	}
	@Override
	public String toString() {
		return "PromotionOrder [province=" + province + ", city=" + city + ", county=" + county + ", sectName="
				+ sectName + ", name=" + name + ", mobile=" + mobile + ", code=" + code + ", ruleId=" + ruleId
				+ ", shareCode=" + shareCode + "]";
	}
	
	
	

}

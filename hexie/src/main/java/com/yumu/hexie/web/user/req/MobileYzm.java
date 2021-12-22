package com.yumu.hexie.web.user.req;

import java.io.Serializable;

public class MobileYzm implements Serializable{
	
	private static final long serialVersionUID = -2090643413772467559L;
	private String mobile;
	private String yzm;
	private int type;		//101 用户注册短信，  102 发票验证码获取， 103推广支付， 104重置密码, 105电子收据申请验证码
	private String appid;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getYzm() {
		return yzm;
	}
	public void setYzm(String yzm) {
		this.yzm = yzm;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	
	
}

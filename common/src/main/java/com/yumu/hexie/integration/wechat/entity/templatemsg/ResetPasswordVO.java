package com.yumu.hexie.integration.wechat.entity.templatemsg;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResetPasswordVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6633207036699036393L;

	@JsonProperty("first")
	private TemplateItem title;	//标题
	
	@JsonProperty("keyword1")
	private TemplateItem userName;	//用户姓名
	
	@JsonProperty("keyword2")
	private TemplateItem password;	//重置密码
	
	@JsonProperty("keyword3")
	private TemplateItem resetTime;	//重置事件
	
	@JsonProperty("remark")
	private TemplateItem remark;//备注

	public TemplateItem getTitle() {
		return title;
	}

	public void setTitle(TemplateItem title) {
		this.title = title;
	}

	public TemplateItem getUserName() {
		return userName;
	}

	public void setUserName(TemplateItem userName) {
		this.userName = userName;
	}

	public TemplateItem getPassword() {
		return password;
	}

	public void setPassword(TemplateItem password) {
		this.password = password;
	}

	public TemplateItem getResetTime() {
		return resetTime;
	}

	public void setResetTime(TemplateItem resetTime) {
		this.resetTime = resetTime;
	}

	public TemplateItem getRemark() {
		return remark;
	}

	public void setRemark(TemplateItem remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "RestPasswordVO [title=" + title + ", userName=" + userName + ", password=" + password + ", resetTime="
				+ resetTime + ", remark=" + remark + "]";
	}
	
	
	
}

package com.yumu.hexie.integration.wechat.entity.templatemsg;

import java.io.Serializable;

public class HaoJiaAnCommentVO implements Serializable{
	
	private TemplateItem title;//标题
	private TemplateItem complainTime;//投诉时间
	private TemplateItem content;//投诉内容
	private TemplateItem serviceName;//投诉服务
	private TemplateItem fromName;//投诉人
	
	public TemplateItem getTitle() {
		return title;
	}
	public void setTitle(TemplateItem title) {
		this.title = title;
	}
	
	public TemplateItem getComplainTime() {
		return complainTime;
	}
	public void setComplainTime(TemplateItem complainTime) {
		this.complainTime = complainTime;
	}
	public TemplateItem getContent() {
		return content;
	}
	public void setContent(TemplateItem content) {
		this.content = content;
	}
	public TemplateItem getServiceName() {
		return serviceName;
	}
	public void setServiceName(TemplateItem serviceName) {
		this.serviceName = serviceName;
	}
	public TemplateItem getFromName() {
		return fromName;
	}
	public void setFromName(TemplateItem fromName) {
		this.fromName = fromName;
	}
	
	
}

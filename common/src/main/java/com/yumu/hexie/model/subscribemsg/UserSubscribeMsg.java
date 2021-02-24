package com.yumu.hexie.model.subscribemsg;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class UserSubscribeMsg extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2742238029980694174L;

	private String templateId;	//订阅模板ID
	private int type;	//模板类型, 0模板消息用，1订阅消息用
	private int bizType;//模板业务类型，0.普通用户的模板，1工作人员模板
	private int status;	//模板状态:accept 1, reject 2, off 0
	private Long userId;	//暂时没用
	private String openid;
	
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public int getBizType() {
		return bizType;
	}
	public void setBizType(int bizType) {
		this.bizType = bizType;
	}
	@Override
	public String toString() {
		return "UserSubscribeMsg [templateId=" + templateId + ", type=" + type + ", bizType=" + bizType + ", status="
				+ status + ", userId=" + userId + ", openid=" + openid + "]";
	}
	
	
	
}

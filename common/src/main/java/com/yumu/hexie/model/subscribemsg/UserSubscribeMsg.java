package com.yumu.hexie.model.subscribemsg;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

/**
 * 订阅消息
 * @author david
 *
 */
@Entity
public class UserSubscribeMsg extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2742238029980694174L;

	private String templateId;	//订阅模板ID
	private int type;	//模板类型, 0模板消息用，1订阅消息用，20支付宝订阅消息
	private int bizType;//模板业务类型，0.普通用户的模板，1工作人员模板
	private int status;	//模板状态:accept 1, reject 2, off 0
	private Long userId;	//暂时没用
	private String openid;
	private String appid;
	private String aliuserid;
	
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
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getAliuserid() {
		return aliuserid;
	}
	public void setAliuserid(String aliuserid) {
		this.aliuserid = aliuserid;
	}
	
	
}

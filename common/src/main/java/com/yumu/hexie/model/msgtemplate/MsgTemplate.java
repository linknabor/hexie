package com.yumu.hexie.model.msgtemplate;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class MsgTemplate extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3552826398269882657L;

	private String name;
	private String appid;
	private String value;
	private int status;	//0禁用，1可用
	private int type;	//0模板消息用的模板，1订阅消息用的模板
	private int bizType;//0普通用户用的模板，1工作人员用模板
	private int subscribeType;	//订阅类型，2为一次性订阅，3为长期订阅
	private String remark;	//备注
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getBizType() {
		return bizType;
	}
	public void setBizType(int bizType) {
		this.bizType = bizType;
	}
	public int getSubscribeType() {
		return subscribeType;
	}
	public void setSubscribeType(int subscribeType) {
		this.subscribeType = subscribeType;
	}
	
	
	
}

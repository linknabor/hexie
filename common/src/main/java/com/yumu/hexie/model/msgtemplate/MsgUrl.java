package com.yumu.hexie.model.msgtemplate;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class MsgUrl extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 736657908065534726L;
	
	private String name;
	private String appid;
	private String value;
	private int status;
	private String remark;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
	@Override
	public String toString() {
		return "MsgUrl [name=" + name + ", appid=" + appid + ", value=" + value + ", status=" + status + ", remark="
				+ remark + "]";
	}
	
	

}

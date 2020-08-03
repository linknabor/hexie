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
	
	
	
	
}

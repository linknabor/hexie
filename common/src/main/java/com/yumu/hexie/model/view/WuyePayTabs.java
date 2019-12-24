package com.yumu.hexie.model.view;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class WuyePayTabs extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7134137096593878685L;
	
	private String name;	//选项卡名称
	private String value;	//选项卡值
	private String sort;	//选项卡顺序
	private String appId;	//公众号APPID
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "WuyePayTabs [name=" + name + ", value=" + value + ", sort=" + sort + ", appId=" + appId + "]";
	}
	
}

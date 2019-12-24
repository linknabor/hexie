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
	@Override
	public String toString() {
		return "WuyePayTabs [name=" + name + ", sort=" + sort + ", appId=" + appId + "]";
	}
	

}

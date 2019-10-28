package com.yumu.hexie.model.view;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class BottomIcon extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8422693309475336542L;

	private String iconName;
	private String iconLink;
	private String iconClass;
	private String appId;
	private Integer sort;
	
	public String getIconName() {
		return iconName;
	}
	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
	public String getIconLink() {
		return iconLink;
	}
	public void setIconLink(String iconLink) {
		this.iconLink = iconLink;
	}
	public String getIconClass() {
		return iconClass;
	}
	public void setIconClass(String iconClass) {
		this.iconClass = iconClass;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	@Override
	public String toString() {
		return "BottomIcon [iconName=" + iconName + ", iconLink=" + iconLink + ", iconClass=" + iconClass + ", appId="
				+ appId + ", sort=" + sort + "]";
	}
	
	
	
}

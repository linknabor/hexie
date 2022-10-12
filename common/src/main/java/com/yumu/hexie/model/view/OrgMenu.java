package com.yumu.hexie.model.view;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class OrgMenu extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8546219309573353904L;

	private String status;	//菜单是否启用,0否1是
	private String code;
	private String name;	//菜单中文名称
	private String url;	//菜单跳转页
	private String image;	//显示的图标
	private String description;	//中文描述
	private int type;	//是正常url还是小程序链接, 0:h5链接, 1:小程序链接
	private String oriId;	//小程序原始id,gh_xxxxx
	private int sort;	//排序
	private String parentCode; //上级code
	private String menuLevel; //0:1级菜单 1:2级菜单

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getOriId() {
		return oriId;
	}

	public void setOriId(String oriId) {
		this.oriId = oriId;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getMenuLevel() {
		return menuLevel;
	}

	public void setMenuLevel(String menuLevel) {
		this.menuLevel = menuLevel;
	}
}

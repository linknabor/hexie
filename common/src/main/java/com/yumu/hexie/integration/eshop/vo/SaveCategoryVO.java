package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;

public class SaveCategoryVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9135569375909398661L;
	
	private long id;
	private String name;
	private int sort;
	private int parentId;
	private int level;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "SaveCategoryVo [id=" + id + ", name=" + name + ", sort=" + sort + ", parentId=" + parentId + ", level="
				+ level + "]";
	}
	
	

}

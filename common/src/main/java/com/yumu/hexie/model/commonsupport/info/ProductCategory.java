package com.yumu.hexie.model.commonsupport.info;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

/**
 * 产品分类
 * @author david
 *
 */
@Entity
public class ProductCategory extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3446370308896482115L;
	
	private String name;	//分类名称
	private int level;	//分类级别
	private int sort;	//排序位置
	private int parentId;	//上级id
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
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

	
	
	
}

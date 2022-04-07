package com.yumu.hexie.model.user;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.yumu.hexie.model.BaseModel;

@Entity
@Table(name = "MiniUserPageAccess", uniqueConstraints = {@UniqueConstraint(columnNames = {"page", "roleId"})})
public class MiniUserPageAccess extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7153266371130255737L;
	
	private String page;
	private String roleId;
	
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	

}

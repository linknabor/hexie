package com.yumu.hexie.model.user;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

@Entity
public class OrgOperator extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6226978735379704832L;

	private long userId;
	private String roleId;
	private String orgName;	//机构名称，机构操作员有这个字段
    private String orgId;	//机构ID，同上
    private String orgType; //机构类型 01:运营商 04:服务商 06:社区运营商
    private String orgOperName;	//机构操作员名称
    private String orgOperId;	//机构操作员ID
    
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getOrgType() {
		return orgType;
	}
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}
	public String getOrgOperName() {
		return orgOperName;
	}
	public void setOrgOperName(String orgOperName) {
		this.orgOperName = orgOperName;
	}
	public String getOrgOperId() {
		return orgOperId;
	}
	public void setOrgOperId(String orgOperId) {
		this.orgOperId = orgOperId;
	}
	@Override
	public String toString() {
		return "OrgOperator [userId=" + userId + ", roleId=" + roleId + ", orgName=" + orgName + ", orgId=" + orgId
				+ ", orgType=" + orgType + ", orgOperName=" + orgOperName + ", orgOperId=" + orgOperId + "]";
	}
    
    	

}

package com.yumu.hexie.model.view;

import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-09-16 17:23
 */
@Entity
public class OrgRoleMenu extends BaseModel {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8594248147045270328L;
	private String orgType;
    private String roleId;
    private String menuCode;

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }
}

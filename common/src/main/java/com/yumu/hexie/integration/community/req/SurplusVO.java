package com.yumu.hexie.integration.community.req;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-11 11:28
 */
public class SurplusVO {
    private String surplusAmt;
    private String bankNo;
    private String orgOperId;
    private String orgId;
    private String roleId;
    public String getSurplusAmt() {
        return surplusAmt;
    }

    public void setSurplusAmt(String surplusAmt) {
        this.surplusAmt = surplusAmt;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getOrgOperId() {
        return orgOperId;
    }

    public void setOrgOperId(String orgOperId) {
        this.orgOperId = orgOperId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "SurplusVO{" +
                "surplusAmt='" + surplusAmt + '\'' +
                ", bankNo='" + bankNo + '\'' +
                ", orgOperId='" + orgOperId + '\'' +
                ", orgId='" + orgId + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }
}

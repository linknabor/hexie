package com.yumu.hexie.integration.community.req;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-11 15:54
 */
public class BankVO {
    private String ownerName;
    private String bankNo;
    private String bankName;
    private String branchBank;
    private String ownerTel;
    private String orgOperId;
    private String orgId;
    private String roleId;

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchBank() {
        return branchBank;
    }

    public void setBranchBank(String branchBank) {
        this.branchBank = branchBank;
    }

    public String getOwnerTel() {
        return ownerTel;
    }

    public void setOwnerTel(String ownerTel) {
        this.ownerTel = ownerTel;
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
        return "BankVO{" +
                "ownerName='" + ownerName + '\'' +
                ", bankNo='" + bankNo + '\'' +
                ", bankName='" + bankName + '\'' +
                ", branchBank='" + branchBank + '\'' +
                ", ownerTel='" + ownerTel + '\'' +
                ", orgOperId='" + orgOperId + '\'' +
                ", orgId='" + orgId + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }
}

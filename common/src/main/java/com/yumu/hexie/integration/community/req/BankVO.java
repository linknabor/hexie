package com.yumu.hexie.integration.community.req;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-11 15:54
 */
public class BankVO {
	
	private static Logger logger = LoggerFactory.getLogger(BankVO.class);
	
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
    	if (!StringUtils.isEmpty(ownerName)) {
			try {
				ownerName = URLEncoder.encode(ownerName, "gbk");
			} catch (UnsupportedEncodingException e) {
				logger.info(e.getMessage(), e);
			}
		}
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
    	if (!StringUtils.isEmpty(bankName)) {
			try {
				bankName = URLEncoder.encode(bankName, "gbk");
			} catch (UnsupportedEncodingException e) {
				logger.info(e.getMessage(), e);
			}
		}
        this.bankName = bankName;
    }

    public String getBranchBank() {
        return branchBank;
    }

    public void setBranchBank(String branchBank) {
    	if (!StringUtils.isEmpty(branchBank)) {
			try {
				branchBank = URLEncoder.encode(branchBank, "gbk");
			} catch (UnsupportedEncodingException e) {
				logger.info(e.getMessage(), e);
			}
		}
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

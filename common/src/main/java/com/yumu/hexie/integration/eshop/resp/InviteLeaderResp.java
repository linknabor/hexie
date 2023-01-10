package com.yumu.hexie.integration.eshop.resp;

import java.io.Serializable;

public class InviteLeaderResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6065748574201246753L;

	private String feeRate;
	private String orgId;
	private String orgName;
	private String orgType;
	private String orgOperId;
	
	public String getFeeRate() {
		return feeRate;
	}
	public void setFeeRate(String feeRate) {
		this.feeRate = feeRate;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getOrgType() {
		return orgType;
	}
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}
	public String getOrgOperId() {
		return orgOperId;
	}
	public void setOrgOperId(String orgOperId) {
		this.orgOperId = orgOperId;
	}
	@Override
	public String toString() {
		return "InviteLeaderResp [feeRate=" + feeRate + ", orgId=" + orgId + ", orgName=" + orgName + ", orgType="
				+ orgType + ", orgOperId=" + orgOperId + "]";
	}
	
}

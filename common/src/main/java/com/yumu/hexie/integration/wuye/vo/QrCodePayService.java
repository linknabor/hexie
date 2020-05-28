package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QrCodePayService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1494959491405615055L;
	
	@JsonProperty("service_list")
	private List<PayCfg> serviceList;

	public static class PayCfg {
		
		@JsonProperty("sect_id")
		private String sectId;
		@JsonProperty("sect_name")
		private String sectName;
		@JsonProperty("cfg_id")
		private String cfgId;
		@JsonProperty("qrcode_id")
		private String qrcodeId;
		@JsonProperty("oper_type")
		private String operType;
		@JsonProperty("service_type_cn")
		private String serviceTypeCn;
		@JsonProperty("signin_flag")
		private String signinFlag;
		@JsonProperty("fee_id")
		private String feeId;
		
		public String getSectId() {
			return sectId;
		}
		public void setSectId(String sectId) {
			this.sectId = sectId;
		}
		public String getSectName() {
			return sectName;
		}
		public void setSectName(String sectName) {
			this.sectName = sectName;
		}
		public String getCfgId() {
			return cfgId;
		}
		public void setCfgId(String cfgId) {
			this.cfgId = cfgId;
		}
		public String getQrcodeId() {
			return qrcodeId;
		}
		public void setQrcodeId(String qrcodeId) {
			this.qrcodeId = qrcodeId;
		}
		public String getOperType() {
			return operType;
		}
		public void setOperType(String operType) {
			this.operType = operType;
		}
		public String getServiceTypeCn() {
			return serviceTypeCn;
		}
		public void setServiceTypeCn(String serviceTypeCn) {
			this.serviceTypeCn = serviceTypeCn;
		}
		public String getSigninFlag() {
			return signinFlag;
		}
		public void setSigninFlag(String signinFlag) {
			this.signinFlag = signinFlag;
		}
		public String getFeeId() {
			return feeId;
		}
		public void setFeeId(String feeId) {
			this.feeId = feeId;
		}
		@Override
		public String toString() {
			return "PayCfg [sectId=" + sectId + ", sectName=" + sectName + ", cfgId=" + cfgId + ", qrcodeId=" + qrcodeId
					+ ", operType=" + operType + ", serviceTypeCn=" + serviceTypeCn + ", signinFlag=" + signinFlag
					+ ", feeId=" + feeId + "]";
		}
		
	}

	public List<PayCfg> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<PayCfg> serviceList) {
		this.serviceList = serviceList;
	}
	
}

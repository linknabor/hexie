package com.yumu.hexie.integration.customservice.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceCfgDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -439592087613660435L;

	@JsonProperty("data")
	private List<ServiceCfg> cfgList;
	
	public static class ServiceCfg{
		
		@JsonProperty("service_id")
		private String serviceId;
		@JsonProperty("service_name")
		private String serviceName;
		@JsonProperty("oper_tpye")
		private String operType;
		
		public String getServiceId() {
			return serviceId;
		}
		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}
		public String getServiceName() {
			return serviceName;
		}
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}
		public String getOperType() {
			return operType;
		}
		public void setOperType(String operType) {
			this.operType = operType;
		}
		@Override
		public String toString() {
			return "ServiceCfgDTO [serviceId=" + serviceId + ", serviceName=" + serviceName + ", operType=" + operType
					+ "]";
		}
		
	}

	public List<ServiceCfg> getCfgList() {
		return cfgList;
	}

	public void setCfgList(List<ServiceCfg> cfgList) {
		this.cfgList = cfgList;
	}

	@Override
	public String toString() {
		return "ServiceCfgDTO [cfgList=" + cfgList + "]";
	}
	
	
}

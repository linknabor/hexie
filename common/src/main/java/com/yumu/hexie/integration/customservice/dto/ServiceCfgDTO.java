package com.yumu.hexie.integration.customservice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceCfgDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -439592087613660435L;

	@JsonProperty("data")
	private ServiceCfg serviceCfg;
	
	public static class ServiceCfg{
		
		@JsonProperty("service_id")
		private String serviceId;
		@JsonProperty("service_name")
		private String serviceName;
		@JsonProperty("oper_type")
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

	public ServiceCfg getServiceCfg() {
		return serviceCfg;
	}
	public void setServiceCfg(ServiceCfg serviceCfg) {
		this.serviceCfg = serviceCfg;
	}
	@Override
	public String toString() {
		return "ServiceCfgDTO [serviceCfg=" + serviceCfg + "]";
	}
	
	
	
}

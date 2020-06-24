package com.yumu.hexie.integration.customservice.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OperatorDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1282486771633521580L;
	
	@JsonProperty("data")
	private List<Operator> operatorList;
	
	public static class Operator {
		
		private String openid;
		private String tel;
		@JsonProperty("service_id")
		private String serviceId;
		
		public String getOpenid() {
			return openid;
		}
		public void setOpenid(String openid) {
			this.openid = openid;
		}
		public String getTel() {
			return tel;
		}
		public void setTel(String tel) {
			this.tel = tel;
		}
		public String getServiceId() {
			return serviceId;
		}
		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}
		@Override
		public String toString() {
			return "Operator [openid=" + openid + ", tel=" + tel + ", serviceId=" + serviceId + "]";
		}
		
	}

	public List<Operator> getOperatorList() {
		return operatorList;
	}

	public void setOperatorList(List<Operator> operatorList) {
		this.operatorList = operatorList;
	}

	@Override
	public String toString() {
		return "OperatorDTO [operatorList=" + operatorList + "]";
	}
	
	
}

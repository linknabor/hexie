package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SaveOperVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -121766136771005460L;
	
	private int operatorType;
	private Long serviceId;
	private List<Oper> opers;
	private String agentNo;

	public static class Oper {
		
		private long id;
		private String name;
		@JsonProperty("openid")
		private String openId;
		private String tel;
		private int type;
		@JsonProperty("userid")
		private long userId;
		@JsonProperty("regionid")
		private long regionId;
		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getOpenId() {
			return openId;
		}
		public void setOpenId(String openId) {
			this.openId = openId;
		}
		public String getTel() {
			return tel;
		}
		public void setTel(String tel) {
			this.tel = tel;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public long getUserId() {
			return userId;
		}
		public void setUserId(long userId) {
			this.userId = userId;
		}

		public long getRegionId() {
			return regionId;
		}

		public void setRegionId(long regionId) {
			this.regionId = regionId;
		}

		@Override
		public String toString() {
			return "Oper{" +
					"id=" + id +
					", name='" + name + '\'' +
					", openId='" + openId + '\'' +
					", tel='" + tel + '\'' +
					", type=" + type +
					", userId=" + userId +
					", regionId=" + regionId +
					'}';
		}
	}
	
	public int getOperatorType() {
		return operatorType;
	}
	public void setOperatorType(int operatorType) {
		this.operatorType = operatorType;
	}
	public List<Oper> getOpers() {
		return opers;
	}
	public void setOpers(List<Oper> opers) {
		this.opers = opers;
	}
	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	public String getAgentNo() {
		return agentNo;
	}
	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}
	@Override
	public String toString() {
		return "SaveOperVO [operatorType=" + operatorType + ", serviceId=" + serviceId + ", opers=" + opers
				+ ", agentNo=" + agentNo + "]";
	}
	
	
}

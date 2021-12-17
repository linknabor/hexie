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
	private String agentName;

	public static class Oper {
		
		private String name;
		@JsonProperty("userid")
		private long userId;
		@JsonProperty("region_id")
		private long regionId;
		@JsonProperty("group_addr")
		private String groupAddr;

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public long getUserId() {
			return userId;
		}
		public void setUserId(long userId) {
			this.userId = userId;
		}

		public String getGroupAddr() {
			return groupAddr;
		}

		public void setGroupAddr(String groupAddr) {
			this.groupAddr = groupAddr;
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
					"name='" + name + '\'' +
					", userId=" + userId +
					", regionId=" + regionId +
					", groupAddr='" + groupAddr + '\'' +
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

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	@Override
	public String toString() {
		return "SaveOperVO{" +
				"operatorType=" + operatorType +
				", serviceId=" + serviceId +
				", opers=" + opers +
				", agentNo='" + agentNo + '\'' +
				", agentName='" + agentName + '\'' +
				'}';
	}
}

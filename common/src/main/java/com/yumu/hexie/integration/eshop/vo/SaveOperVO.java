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
		private long regionId;
		@JsonProperty("groupAddr")
		private String leaderAddr;
		private String mobile;	//TODO 备用

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
		public long getRegionId() {
			return regionId;
		}
		public void setRegionId(long regionId) {
			this.regionId = regionId;
		}
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		public String getLeaderAddr() {
			return leaderAddr;
		}
		public void setLeaderAddr(String leaderAddr) {
			this.leaderAddr = leaderAddr;
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

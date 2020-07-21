package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SaveOperVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -121766136771005460L;
	
	private String operType;
	private Long serviceId;
	private List<Oper> opers;

	public static class Oper {
		
		private long id;
		private String name;
		@JsonProperty("openid")
		private String openId;
		private String tel;
		private int type;
		@JsonProperty("userid")
		private long userId;
		
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
		@Override
		public String toString() {
			return "Oper [id=" + id + ", name=" + name + ", openId=" + openId + ", tel=" + tel + ", type=" + type
					+ ", userId=" + userId + "]";
		}
		
		
	}
	
	public String getOperType() {
		return operType;
	}
	public void setOperType(String operType) {
		this.operType = operType;
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
	@Override
	public String toString() {
		return "SaveOperVO [operType=" + operType + ", serviceId=" + serviceId + ", opers=" + opers + "]";
	}
	
	
}

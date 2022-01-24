package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;

public class QueryOperVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8051570429911719099L;
	
	private long id;
	private String name;
	private String tel;
	private int type;
	private String appid;
	private String agentNo;
	private Long serviceId;
	
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
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
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
		return "QueryOperVO [id=" + id + ", name=" + name + ", tel=" + tel + ", type=" + type + ", appid=" + appid
				+ ", agentNo=" + agentNo + ", serviceId=" + serviceId + "]";
	}
	
	
	
}

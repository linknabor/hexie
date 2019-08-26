package com.eshequ.hexie.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ComponentAcessToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1827269769285082880L;

	@JsonProperty("component_access_token")
	private String componentAcessToken;
	
	@JsonProperty("expires_in")
	private String expiresIn;
	
	private Long createTime;

	public String getComponentAcessToken() {
		return componentAcessToken;
	}

	public void setComponentAcessToken(String componentAcessToken) {
		this.componentAcessToken = componentAcessToken;
	}

	public String getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "ComponentAcessToken [componentAcessToken=" + componentAcessToken + ", expiresIn=" + expiresIn
				+ ", createTime=" + createTime + "]";
	}

	
	
	
}

package com.yumu.hexie.integration.customservice.resp;

import java.io.Serializable;
import java.util.Base64;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomServiceVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8743335179863369630L;
	
	@JsonProperty("service_title")
	private String serviceTitle;
	private String image;
	private String price;
	@JsonProperty("service_id")
	private String serviceId;
	@JsonProperty("service_level")
	private String serviceLevel;
	@JsonProperty("context")
	private String content;
	@JsonProperty("org_id")
	private String agentNo;
	@JsonProperty("org_name")
	private String agentName;
	
	public String getServiceTitle() {
		return serviceTitle;
	}
	public void setServiceTitle(String serviceTitle) {
		this.serviceTitle = serviceTitle;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceLevel() {
		return serviceLevel;
	}
	public void setServiceLevel(String serviceLevel) {
		this.serviceLevel = serviceLevel;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		String retStr = "";
		if (!StringUtils.isEmpty(content)) {
			byte[]bytes = Base64.getDecoder().decode(content);
			retStr = new String(bytes);
		}
		this.content = retStr;
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
		return "CustomServiceVO [serviceTitle=" + serviceTitle + ", image=" + image + ", price=" + price
				+ ", serviceId=" + serviceId + ", serviceLevel=" + serviceLevel + ", content=" + content + ", agentNo="
				+ agentNo + ", agentName=" + agentName + "]";
	}
	

}

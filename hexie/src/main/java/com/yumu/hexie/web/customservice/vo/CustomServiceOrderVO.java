package com.yumu.hexie.web.customservice.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomServiceOrderVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1948239935143263321L;
	
	@JsonProperty("service_id")
	private String serviceId;
	@JsonProperty("service_title")
	private String serviceName;
	@JsonProperty("image")
	private String image;
	@JsonProperty("sect_id")
	private String sectId;
	@JsonProperty("link_man")
	private String linkman;
	@JsonProperty("link_tel")
	private String linktel;
	@JsonProperty("service_addr")
	private String serviceAddr;
	@JsonProperty("tran_amt")
	private String tranAmt;
	@JsonProperty("trade_water_id")
	private String tradeWaterId;
	private String memo;
	@JsonProperty("sect_name")
	private String sectName;
	
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getLinkman() {
		return linkman;
	}
	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}
	public String getLinktel() {
		return linktel;
	}
	public void setLinktel(String linktel) {
		this.linktel = linktel;
	}
	public String getServiceAddr() {
		return serviceAddr;
	}
	public void setServiceAddr(String serviceAddr) {
		this.serviceAddr = serviceAddr;
	}
	public String getTranAmt() {
		return tranAmt;
	}
	public void setTranAmt(String tranAmt) {
		this.tranAmt = tranAmt;
	}
	public String getTradeWaterId() {
		return tradeWaterId;
	}
	public void setTradeWaterId(String tradeWaterId) {
		this.tradeWaterId = tradeWaterId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getSectName() {
		return sectName;
	}
	public void setSectName(String sectName) {
		this.sectName = sectName;
	}
	@Override
	public String toString() {
		return "CustomServiceOrderVO [serviceId=" + serviceId + ", serviceName=" + serviceName + ", image=" + image
				+ ", sectId=" + sectId + ", linkman=" + linkman + ", linktel=" + linktel + ", serviceAddr="
				+ serviceAddr + ", tranAmt=" + tranAmt + ", tradeWaterId=" + tradeWaterId + ", memo=" + memo
				+ ", sectName=" + sectName + "]";
	}
	

}

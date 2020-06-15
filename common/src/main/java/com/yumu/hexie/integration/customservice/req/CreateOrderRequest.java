package com.yumu.hexie.integration.customservice.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateOrderRequest extends CustomServiceRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5559168361712488423L;

	@JsonProperty("service_id")
	private String serviceId;
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
	@Override
	public String toString() {
		return "CreateOrderRequest [serviceId=" + serviceId + ", sectId=" + sectId + ", linkman=" + linkman
				+ ", linktel=" + linktel + ", serviceAddr=" + serviceAddr + ", tranAmt=" + tranAmt + ", tradeWaterId="
				+ tradeWaterId + "]";
	}
	

}

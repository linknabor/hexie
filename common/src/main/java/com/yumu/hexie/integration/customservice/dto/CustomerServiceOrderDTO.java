package com.yumu.hexie.integration.customservice.dto;

import com.yumu.hexie.model.user.User;

public class CustomerServiceOrderDTO {

	private User user;
	
	private String serviceId;
	private String serviceName;
	private String image;
	private String sectId;
	private String linkman;
	private String linktel;
	private String serviceAddr;
	private String tranAmt;
	private String tradeWaterId;
	private String memo;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
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
	@Override
	public String toString() {
		return "CustomerServiceOrderDTO [user=" + user + ", serviceId=" + serviceId + ", serviceName=" + serviceName
				+ ", image=" + image + ", sectId=" + sectId + ", linkman=" + linkman + ", linktel=" + linktel
				+ ", serviceAddr=" + serviceAddr + ", tranAmt=" + tranAmt + ", tradeWaterId=" + tradeWaterId + ", memo="
				+ memo + "]";
	}
	
}

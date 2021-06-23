package com.yumu.hexie.integration.workorder.resp;

import java.io.Serializable;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.common.util.DateUtil;

public class WorkOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6462103291393840615L;
	
	@JsonProperty("order_id")
	private String orderId;
	@JsonProperty("workorder_status")
	private String workOrderStatus;
	@JsonProperty("dist_type")
	private String distType;
	@JsonProperty("serve_address")
	private String serveAddress;
	private String content;
	@JsonProperty("image_urls")
	private String imageUrls;
	@JsonProperty("sect_name")
	private String sectName;
	@JsonProperty("csp_name")
	private String cspName;
	@JsonProperty("create_date")
	private String createDate;
	@JsonProperty("create_time")
	private String createTime;
	private String acceptor;
	@JsonProperty("acceptor_contact")
	private String acceptorContact;
	@JsonProperty("accept_date")
	private String acceptDate;
	@JsonProperty("accept_time")
	private String acceptTime;
	private String finisher;
	@JsonProperty("finisher_contact")
	private String finisherContact;
	@JsonProperty("finish_date")
	private String finishDate;
	@JsonProperty("finish_time")
	private String finishTime;
	@JsonProperty("pay_method")
	private String payMethod;
	@JsonProperty("payorder_id")
	private String payorderId;
	@JsonProperty("order_amt")
	private String orderAmt;
	
	private String createDateStr;
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getWorkOrderStatus() {
		return workOrderStatus;
	}
	public void setWorkOrderStatus(String workOrderStatus) {
		this.workOrderStatus = workOrderStatus;
	}
	public String getDistType() {
		return distType;
	}
	public void setDistType(String distType) {
		this.distType = distType;
	}
	public String getServeAddress() {
		return serveAddress;
	}
	public void setServeAddress(String serveAddress) {
		this.serveAddress = serveAddress;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImageUrls() {
		return imageUrls;
	}
	public void setImageUrls(String imageUrls) {
		this.imageUrls = imageUrls;
	}
	public String getSectName() {
		return sectName;
	}
	public void setSectName(String sectName) {
		this.sectName = sectName;
	}
	public String getCspName() {
		return cspName;
	}
	public void setCspName(String cspName) {
		this.cspName = cspName;
	}
	public String getAcceptor() {
		return acceptor;
	}
	public void setAcceptor(String acceptor) {
		this.acceptor = acceptor;
	}
	public String getAcceptorContact() {
		return acceptorContact;
	}
	public void setAcceptorContact(String acceptorContact) {
		this.acceptorContact = acceptorContact;
	}
	public String getAcceptDate() {
		return acceptDate;
	}
	public void setAcceptDate(String acceptDate) {
		this.acceptDate = acceptDate;
	}
	public String getAcceptTime() {
		return acceptTime;
	}
	public void setAcceptTime(String acceptTime) {
		this.acceptTime = acceptTime;
	}
	public String getFinisher() {
		return finisher;
	}
	public void setFinisher(String finisher) {
		this.finisher = finisher;
	}
	public String getFinisherContact() {
		return finisherContact;
	}
	public void setFinisherContact(String finisherContact) {
		this.finisherContact = finisherContact;
	}
	public String getFinishDate() {
		return finishDate;
	}
	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}
	public String getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}
	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	public String getPayorderId() {
		return payorderId;
	}
	public void setPayorderId(String payorderId) {
		this.payorderId = payorderId;
	}
	public String getOrderAmt() {
		return orderAmt;
	}
	public void setOrderAmt(String orderAmt) {
		this.orderAmt = orderAmt;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getCreateDateStr() {
		if(!StringUtils.isEmpty(createDate) && !StringUtils.isEmpty(createTime)) {
			createDateStr = DateUtil.formatFromDB(createDate, createTime);
		}
		return createDateStr;
	}
	public void setCreateDateStr(String createDateStr) {
		this.createDateStr = createDateStr;
	}
	@Override
	public String toString() {
		return "WorkOrdersVO [orderId=" + orderId + ", workOrderStatus=" + workOrderStatus + ", distType=" + distType
				+ ", serveAddress=" + serveAddress + ", content=" + content + ", imageUrls=" + imageUrls + ", sectName="
				+ sectName + ", cspName=" + cspName + ", acceptor=" + acceptor + ", acceptorContact=" + acceptorContact
				+ ", acceptDate=" + acceptDate + ", acceptTime=" + acceptTime + ", finisher=" + finisher
				+ ", finisherContact=" + finisherContact + ", finishDate=" + finishDate + ", finishTime=" + finishTime
				+ ", payMethod=" + payMethod + ", payorderId=" + payorderId + ", orderAmt=" + orderAmt + "]";
	}
}

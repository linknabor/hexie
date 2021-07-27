package com.yumu.hexie.integration.notify;

import java.io.Serializable;
import java.util.List;

public class WorkOrderNotification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8956418556393998839L;
	
	private String orderId;
	private String orderType;	//01维修 02保洁 03服务
	private String orderStatus;
	private String operateDate;	//下单时间
	private String sectName;	//订单所在小区
	private String content;	//订单内容
	private String serveAddress;	//订单地址
	private String orderSource;	//订单来源,网络或者其他等
	private String distType;	//订单区域类型
	private String reason;	//驳回原因
	private String acceptor;
	private String rejector;	//拒绝人
	private String finisher;	//完工人
	
	private String operation;	//01创建 03受理05接单07完工
	private List<Operator> operatorList;
	
	private String corpid;
	private String agentId;
	
	private String timestamp;

	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public List<Operator> getOperatorList() {
		return operatorList;
	}
	public void setOperatorList(List<Operator> operatorList) {
		this.operatorList = operatorList;
	}
	public String getCorpid() {
		return corpid;
	}
	public void setCorpid(String corpid) {
		this.corpid = corpid;
	}
	public String getAgentId() {
		return agentId;
	}
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	public String getOperateDate() {
		return operateDate;
	}
	public void setOperateDate(String operateDate) {
		this.operateDate = operateDate;
	}
	public String getSectName() {
		return sectName;
	}
	public void setSectName(String sectName) {
		this.sectName = sectName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getServeAddress() {
		return serveAddress;
	}
	public void setServeAddress(String serveAddress) {
		this.serveAddress = serveAddress;
	}
	public String getOrderSource() {
		return orderSource;
	}
	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}
	public String getDistType() {
		return distType;
	}
	public void setDistType(String distType) {
		this.distType = distType;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getAcceptor() {
		return acceptor;
	}
	public void setAcceptor(String acceptor) {
		this.acceptor = acceptor;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getRejector() {
		return rejector;
	}
	public void setRejector(String rejector) {
		this.rejector = rejector;
	}
	public String getFinisher() {
		return finisher;
	}
	public void setFinisher(String finisher) {
		this.finisher = finisher;
	}
	@Override
	public String toString() {
		return "WorkOrderNotification [orderId=" + orderId + ", orderType=" + orderType + ", orderStatus=" + orderStatus
				+ ", operateDate=" + operateDate + ", sectName=" + sectName + ", content=" + content + ", serveAddress="
				+ serveAddress + ", orderSource=" + orderSource + ", distType=" + distType + ", reason=" + reason
				+ ", acceptor=" + acceptor + ", rejector=" + rejector + ", finisher=" + finisher + ", operation="
				+ operation + ", operatorList=" + operatorList + ", corpid=" + corpid + ", agentId=" + agentId
				+ ", timestamp=" + timestamp + "]";
	}
	
	
}

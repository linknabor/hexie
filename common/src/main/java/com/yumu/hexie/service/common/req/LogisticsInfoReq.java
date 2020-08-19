package com.yumu.hexie.service.common.req;

import java.io.Serializable;

public class LogisticsInfoReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7193976460383639887L;
	
	private long orderId;
	private int logisticType;	//0商户派送 1用户自提 2第三方配送
	private String logisticNo;	//快递单号
	private String logisticCode;	//快递公司编号
	private String logisticName;	//快递公司名称
	
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public int getLogisticType() {
		return logisticType;
	}
	public void setLogisticType(int logisticType) {
		this.logisticType = logisticType;
	}
	public String getLogisticNo() {
		return logisticNo;
	}
	public void setLogisticNo(String logisticNo) {
		this.logisticNo = logisticNo;
	}
	public String getLogisticCode() {
		return logisticCode;
	}
	public void setLogisticCode(String logisticCode) {
		this.logisticCode = logisticCode;
	}
	public String getLogisticName() {
		return logisticName;
	}
	public void setLogisticName(String logisticName) {
		this.logisticName = logisticName;
	}
	@Override
	public String toString() {
		return "LogisticsInfoReq [orderId=" + orderId + ", logisticType=" + logisticType + ", logisticNo=" + logisticNo
				+ ", logisticCode=" + logisticCode + ", logisticName=" + logisticName + "]";
	}
	
	

}

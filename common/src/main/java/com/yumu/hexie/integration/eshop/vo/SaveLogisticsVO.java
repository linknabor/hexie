package com.yumu.hexie.integration.eshop.vo;

import java.io.Serializable;
import java.util.List;

public class SaveLogisticsVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3651454343317721269L;
	
	private String orderId;
	private List<LogisticInfo> logistics;
	
	public static class LogisticInfo {
		
		private String logisticType;	//0商户派送 1用户自提 2第三方配送
		private String logisticNo;	//快递单号
		private String logisticCode;	//快递公司编号
		private String logisticName;	//快递公司名称
		
		public String getLogisticType() {
			return logisticType;
		}
		public void setLogisticType(String logisticType) {
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
			return "LogisticInfo [logisticType=" + logisticType + ", logisticNo=" + logisticNo + ", logisticCode="
					+ logisticCode + ", logisticName=" + logisticName + "]";
		}
		
	}
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public List<LogisticInfo> getLogistics() {
		return logistics;
	}
	public void setLogistics(List<LogisticInfo> logistics) {
		this.logistics = logistics;
	}
	@Override
	public String toString() {
		return "SaveLogisticsRequest [orderId=" + orderId + ", logistics=" + logistics + "]";
	}
}

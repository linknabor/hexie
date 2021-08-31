package com.yumu.hexie.integration.notify;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConversionNotification implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5676388722912204252L;
	
	@JsonProperty("order_id")
	private String orderId;
	@JsonProperty("source_id")
	private String sourceId;
	@JsonProperty("source_type")
	private String sourceType;
	private String timestamp;
	@JsonProperty("is_reverse")
	private String reversed;
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getReversed() {
		return reversed;
	}
	public void setReversed(String reversed) {
		this.reversed = reversed;
	}
	@Override
	public String toString() {
		return "ConversionNotification [orderId=" + orderId + ", sourceId=" + sourceId + ", sourceType=" + sourceType
				+ ", timestamp=" + timestamp + ", reversed=" + reversed + "]";
	}
	
	
}

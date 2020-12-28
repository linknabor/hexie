package com.yumu.hexie.integration.notify;

import java.io.Serializable;

public class CommonNotificationResponse<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3344127152281761034L;
	
	private String result;
	private T data;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "CommonNotificationResponse [result=" + result + ", data=" + data + "]";
	}
	
	

}

package com.yumu.hexie.integration.beyondsoft.resp;

import java.io.Serializable;

public class BeyondSoftResp<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4899488523635586895L;
	
	private String code;
	private String msg;
	private T data;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "BeyondSoftResp [code=" + code + ", msg=" + msg + ", data=" + data + "]";
	}

}

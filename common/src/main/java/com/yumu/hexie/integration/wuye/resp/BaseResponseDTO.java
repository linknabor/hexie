package com.yumu.hexie.integration.wuye.resp;

import com.yumu.hexie.integration.wuye.base.BaseQuery;

public class BaseResponseDTO<T> extends BaseQuery {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1637582137500635252L;
	
	private String return_code;	//返回码	
	private String return_message;	//返回提示语
	private String sign;	//签名
	private T data;	//数据
	
	public String getReturn_code() {
		return return_code;
	}
	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}
	public String getReturn_message() {
		return return_message;
	}
	public void setReturn_message(String return_message) {
		this.return_message = return_message;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	

}

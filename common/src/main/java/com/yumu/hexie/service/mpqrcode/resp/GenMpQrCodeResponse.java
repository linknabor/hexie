package com.yumu.hexie.service.mpqrcode.resp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenMpQrCodeResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5499531987157281832L;
	
	private String ticket;
	@JsonProperty("expire_seconds")
	private String expireSeconds;
	private String url;
	
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public String getExpireSeconds() {
		return expireSeconds;
	}
	public void setExpireSeconds(String expireSeconds) {
		this.expireSeconds = expireSeconds;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "GenQrCodeResponse [ticket=" + ticket + ", expireSeconds=" + expireSeconds + ", url=" + url + "]";
	}
	
	
}

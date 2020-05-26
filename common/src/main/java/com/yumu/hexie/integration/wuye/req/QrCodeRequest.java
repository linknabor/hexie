package com.yumu.hexie.integration.wuye.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QrCodeRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6482946745741440625L;
	
	@JsonProperty("qrcode_id")
	private String qrCodeId;

	public String getQrCodeId() {
		return qrCodeId;
	}

	public void setQrCodeId(String qrCodeId) {
		this.qrCodeId = qrCodeId;
	}

	@Override
	public String toString() {
		return "QrCodeRequest [qrCodeId=" + qrCodeId + "]";
	}
	

}

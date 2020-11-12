package com.yumu.hexie.service.user.dto;

public class CheckCouponDTO {
	
	private boolean valid = false;
	private String errMsg;
	
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	
	

}

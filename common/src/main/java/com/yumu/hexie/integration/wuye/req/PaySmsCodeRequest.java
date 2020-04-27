package com.yumu.hexie.integration.wuye.req;

public class PaySmsCodeRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8732313390026273936L;
	
	private String mobile;
	private String quickToken;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getQuickToken() {
		return quickToken;
	}
	public void setQuickToken(String quickToken) {
		this.quickToken = quickToken;
	}
	@Override
	public String toString() {
		return "PaySmsCodeRequest [mobile=" + mobile + ", quickToken=" + quickToken + "]";
	}
	
	
	

}

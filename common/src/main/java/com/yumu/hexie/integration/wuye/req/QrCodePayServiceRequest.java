package com.yumu.hexie.integration.wuye.req;

public class QrCodePayServiceRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4384712234127791903L;
	
	private String openid;
	private String tel;
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	@Override
	public String toString() {
		return "QrCodePayServiceRequest [openid=" + openid + ", tel=" + tel + "]";
	}
	
	

}

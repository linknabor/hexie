package com.yumu.hexie.integration.wuye.req;

public class PaySmsCodeRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8732313390026273936L;
	
	private String orderNo;
	private String mobile;
	
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@Override
	public String toString() {
		return "UnionPaySmsRequest [orderNo=" + orderNo + ", mobile=" + mobile + "]";
	}
	
	

}

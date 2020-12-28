package com.yumu.hexie.integration.eshop.req;

import com.yumu.hexie.integration.common.CommonRequest;

public class EshopServiceRequest extends CommonRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2786659964230980952L;
	
	private String tel;

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	@Override
	public String toString() {
		return "EshopServiceRequest [tel=" + tel + "]";
	}
	

}

package com.yumu.hexie.integration.wuye.resp;

import java.io.Serializable;

public class MpQrCodeParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8960909514082477613L;
	
    private String trade_water_id;
    private String tran_amt;
    private String shop_name;
    private String appid;
    
	public String getTrade_water_id() {
		return trade_water_id;
	}
	public void setTrade_water_id(String trade_water_id) {
		this.trade_water_id = trade_water_id;
	}
	public String getTran_amt() {
		return tran_amt;
	}
	public void setTran_amt(String tran_amt) {
		this.tran_amt = tran_amt;
	}
	public String getShop_name() {
		return shop_name;
	}
	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
    
    

}

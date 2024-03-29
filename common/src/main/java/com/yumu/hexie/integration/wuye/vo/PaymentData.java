package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;
import java.util.List;

//支付订单信息（合并了支付项，包含了房子信息）
public class PaymentData implements Serializable {

	private static final long serialVersionUID = 9017390740835998429L;
	private String sect_name;
	private String city_name;
	private String ver_no;//户号
	private List<PaymentItem> fee_name;
	private String fee_price;
	private String cell_addr;
	private String cnst_area;
	
	public String getSect_name() {
		return sect_name;
	}
	public void setSect_name(String sect_name) {
		this.sect_name = sect_name;
	}
	public String getCity_name() {
		return city_name;
	}
	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
	public String getVer_no() {
		return ver_no;
	}
	public void setVer_no(String ver_no) {
		this.ver_no = ver_no;
	}
	public List<PaymentItem> getFee_name() {
		return fee_name;
	}
	public void setFee_name(List<PaymentItem> fee_name) {
		this.fee_name = fee_name;
	}
	public String getFee_price() {
		return fee_price;
	}
	public void setFee_price(String fee_price) {
		this.fee_price = fee_price;
	}
	public String getCell_addr() {
		return cell_addr;
	}
	public void setCell_addr(String cell_addr) {
		this.cell_addr = cell_addr;
	}
	public String getCnst_area() {
		return cnst_area;
	}
	public void setCnst_area(String cnst_area) {
		this.cnst_area = cnst_area;
	}
	

}

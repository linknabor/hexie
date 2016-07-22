package com.yumu.hexie.integration.wuye.resp;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.integration.wuye.vo.BillInfo;

public class BillListVO implements Serializable {

	private static final long serialVersionUID = -3218697946940987315L;
	
	private int total_count;
	private List<BillInfo> bill_info;
	private List<BillInfo> car_bill_info;
	private int permit_skip_pay;//0?
	private int permit_skip_car_pay;
	private String meet_the_number;
	private int bills_size;

	
	public String getMeet_the_number() {
		return meet_the_number;
	}
	public void setMeet_the_number(String meet_the_number) {
		this.meet_the_number = meet_the_number;
	}
	public int getPermit_skip_car_pay() {
		return permit_skip_car_pay;
	}
	public void setPermit_skip_car_pay(int permit_skip_car_pay) {
		this.permit_skip_car_pay = permit_skip_car_pay;
	}
	public int getBills_size() {
		return bills_size;
	}
	public void setBills_size(int bills_size) {
		this.bills_size = bills_size;
	}
	public int getTotal_count() {
		return total_count;
	}
	public void setTotal_count(int total_count) {
		this.total_count = total_count;
	}
	public List<BillInfo> getBill_info() {
		return bill_info;
	}
	public void setBill_info(List<BillInfo> bill_info) {
		this.bill_info = bill_info;
		this.bills_size = bill_info==null?0:bill_info.size();
	}
	public int getPermit_skip_pay() {
		return permit_skip_pay;
	}
	public void setPermit_skip_pay(int permit_skip_pay) {
		this.permit_skip_pay = permit_skip_pay;
	}
	public List<BillInfo> getCar_bill_info() {
		return car_bill_info;
	}
	public void setCar_bill_info(List<BillInfo> car_bill_info) {
		this.car_bill_info = car_bill_info;
	}
	
}

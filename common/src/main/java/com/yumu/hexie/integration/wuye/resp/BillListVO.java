package com.yumu.hexie.integration.wuye.resp;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.integration.wuye.vo.BillInfo;

public class BillListVO implements Serializable {

	private static final long serialVersionUID = 1592900991660863594L;

	private int total_count;
	private List<BillInfo> bill_info;
	private int permit_skip_pay;//0?
	private int bills_size;
	
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
	
	
}

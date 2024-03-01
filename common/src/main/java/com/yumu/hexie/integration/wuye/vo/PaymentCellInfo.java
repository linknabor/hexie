package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;

public class PaymentCellInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6587654217480799601L;

	private String cell_addr;
	private String cnst_area;

	private String cust_name;
	private String car_no;
	
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

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

	public String getCar_no() {
		return car_no;
	}

	public void setCar_no(String car_no) {
		this.car_no = car_no;
	}

	@Override
	public String toString() {
		return "PaymentCellInfo{" +
				"cell_addr='" + cell_addr + '\'' +
				", cnst_area='" + cnst_area + '\'' +
				", cust_name='" + cust_name + '\'' +
				", car_no='" + car_no + '\'' +
				'}';
	}
}

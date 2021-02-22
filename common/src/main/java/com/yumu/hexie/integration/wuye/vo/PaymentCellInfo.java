package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;

public class PaymentCellInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6587654217480799601L;

	private String cell_addr;
	private String cnst_area;
	
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
	@Override
	public String toString() {
		return "PaymentCellInfo [cell_addr=" + cell_addr + ", cnst_area=" + cnst_area + "]";
	}
	
	
}

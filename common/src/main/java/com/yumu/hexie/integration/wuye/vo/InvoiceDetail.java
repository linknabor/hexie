package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;


public class InvoiceDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5024819534602439352L;
	private String make_date;	//开票日期
	private String apply_date;	//申请日期
	private String invoice_no;	//发票号
	private String invoice_title;	//抬头
	private String tran_amt;	//交易金额
	private String pdf_addr;	//pdf地址
	
	public String getMake_date() {
		return make_date;
	}
	public void setMake_date(String make_date) {
		this.make_date = make_date;
	}
	public String getApply_date() {
		return apply_date;
	}
	public void setApply_date(String apply_date) {
		this.apply_date = apply_date;
	}
	public String getInvoice_no() {
		return invoice_no;
	}
	public void setInvoice_no(String invoice_no) {
		this.invoice_no = invoice_no;
	}
	public String getInvoice_title() {
		return invoice_title;
	}
	public void setInvoice_title(String invoice_title) {
		this.invoice_title = invoice_title;
	}
	public String getTran_amt() {
		return tran_amt;
	}
	public void setTran_amt(String tran_amt) {
		this.tran_amt = tran_amt;
	}
	public String getPdf_addr() {
		return pdf_addr;
	}
	public void setPdf_addr(String pdf_addr) {
		this.pdf_addr = pdf_addr;
	}
	
	
}

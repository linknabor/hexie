package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Transient;

import com.yumu.hexie.integration.wuye.util.KeyToNameUtil;

//单笔支付信息
public class PaymentInfo implements Serializable {

	private static final long serialVersionUID = 1176023527465139811L;
	private String tran_time;;
	private String bill_tran_amt;
	private String sect_name;
	private String office_tel;
	private String csp_name;
	private String paymethod;
	private String show_invoice_flag;//是否开通电子发票功能
	private String show_com_flag;//是否允许公司开票功能
	private String show_invoice;//是否开票
	private String invoice_title;//发票抬头（回显使用）
	private List<PaymentData> fee_data;
	private List<PaymentCellInfo> pay_cell;
	private String support_card_pay;	//是否支持绑卡支付
	private String is_create_qrcode;
	private String is_default_invoice; //是否强制开票
	private String total_fee_price;
	
	@Transient
	public String getPaymethodStr(){
		return KeyToNameUtil.keyToName(KeyToNameUtil.PAYMETHOD_TYPE, paymethod);
	}
	public String getTran_time() {
		return tran_time;
	}
	public void setTran_time(String tran_time) {
		this.tran_time = tran_time;
	}
	public String getBill_tran_amt() {
		return bill_tran_amt;
	}
	public void setBill_tran_amt(String bill_tran_amt) {
		this.bill_tran_amt = bill_tran_amt;
	}
	public String getSect_name() {
		return sect_name;
	}
	public void setSect_name(String sect_name) {
		this.sect_name = sect_name;
	}
	public String getPaymethod() {
		return paymethod;
	}
	public void setPaymethod(String paymethod) {
		this.paymethod = paymethod;
	}
	public List<PaymentData> getFee_data() {
		return fee_data;
	}
	public void setFee_data(List<PaymentData> fee_data) {
		this.fee_data = fee_data;
	}
	public String getShow_invoice_flag() {
		return show_invoice_flag;
	}
	public void setShow_invoice_flag(String show_invoice_flag) {
		this.show_invoice_flag = show_invoice_flag;
	}
	public String getShow_com_flag() {
		return show_com_flag;
	}
	public void setShow_com_flag(String show_com_flag) {
		this.show_com_flag = show_com_flag;
	}
	public String getInvoice_title() {
		return invoice_title;
	}
	public void setInvoice_title(String invoice_title) {
		this.invoice_title = invoice_title;
	}
	public String getShow_invoice() {
		return show_invoice;
	}
	public void setShow_invoice(String show_invoice) {
		this.show_invoice = show_invoice;
	}
	public String getSupport_card_pay() {
		return support_card_pay;
	}
	public void setSupport_card_pay(String support_card_pay) {
		this.support_card_pay = support_card_pay;
	}
	public String getIs_create_qrcode() {
		return is_create_qrcode;
	}
	public void setIs_create_qrcode(String is_create_qrcode) {
		this.is_create_qrcode = is_create_qrcode;
	}
	public List<PaymentCellInfo> getPay_cell() {
		return pay_cell;
	}
	public void setPay_cell(List<PaymentCellInfo> pay_cell) {
		this.pay_cell = pay_cell;
	}
	public String getTotal_fee_price() {
		return total_fee_price;
	}
	public void setTotal_fee_price(String total_fee_price) {
		this.total_fee_price = total_fee_price;
	}
	public String getCsp_name() {
		return csp_name;
	}
	public void setCsp_name(String csp_name) {
		this.csp_name = csp_name;
	}
	public String getOffice_tel() {
		return office_tel;
	}
	public void setOffice_tel(String office_tel) {
		this.office_tel = office_tel;
	}

	public String getIs_default_invoice() {
		return is_default_invoice;
	}

	public void setIs_default_invoice(String is_default_invoice) {
		this.is_default_invoice = is_default_invoice;
	}
}

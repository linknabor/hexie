package com.yumu.hexie.integration.wuye.req;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.integration.wuye.dto.PrepayRequestDTO;

public class PrepayRequest extends WuyeRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9222329098194603371L;
	
	public PrepayRequest() {
		super();
	}
	public PrepayRequest(PrepayRequestDTO prepayRequestDTO) {
		
		BeanUtils.copyProperties(prepayRequestDTO, this);
		this.wuyeId = prepayRequestDTO.getUser().getWuyeId();
		this.mobile = prepayRequestDTO.getUser().getTel();
		this.openid = prepayRequestDTO.getUser().getOpenid();
	}
	
	//公用参数
	@JsonProperty("user_id")
	private String wuyeId;
	@JsonProperty("from_sys")
	private String fromSys;
	@JsonProperty("coupon_unit")
	private String couponUnit;
	@JsonProperty("coupon_num")
	private String couponNum;
	@JsonProperty("conpoin_id")
	private String couponId;
	private String mianBill;
	private String mianAmt;
	private String reduceAmt;
	@JsonProperty("fee_mianBill")
	private String feeMianBill;
	@JsonProperty("fee_mianAmt")
	private String feeMianAmt;
	private String openid;
	
	//开票参数
	private String mobile;
	@JsonProperty("invoice_title")
	private String invoiceTitle;
	@JsonProperty("invoice_title_type")
	private String invoiceTitleType;
	@JsonProperty("credit_code")
	private String creditCode;
	
	//专业版参数
	@JsonProperty("bill_id")
	private String billId;
	@JsonProperty("stmt_id")
	private String stmtId;
	
	//标准版参数
	@JsonProperty("mng_cell_id")
	private String houseId;
	@JsonProperty("start_date")
	private String startDate;
	@JsonProperty("end_date")
	private String endDate;
	
	private String payType;	//0微信支付，1银行卡支付
	
	//银行卡支付参数
	private String customerName;	//持卡人姓名
	private String certType;	//证件类型
	private String certId;		//证件号
	private String acctNo;		//银行卡号
	private String phoneNo;		//银行预留手机
	
	private String quickToken;	//快捷支付token
	private String veriCode;	//手机验证码
	
	public String getCouponUnit() {
		return couponUnit;
	}
	public void setCouponUnit(String couponUnit) {
		this.couponUnit = couponUnit;
	}
	public String getCouponNum() {
		return couponNum;
	}
	public void setCouponNum(String couponNum) {
		this.couponNum = couponNum;
	}
	public String getCouponId() {
		return couponId;
	}
	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}
	public String getMianBill() {
		return mianBill;
	}
	public void setMianBill(String mianBill) {
		this.mianBill = mianBill;
	}
	public String getMianAmt() {
		return mianAmt;
	}
	public void setMianAmt(String mianAmt) {
		this.mianAmt = mianAmt;
	}
	public String getReduceAmt() {
		return reduceAmt;
	}
	public void setReduceAmt(String reduceAmt) {
		this.reduceAmt = reduceAmt;
	}
	public String getFeeMianBill() {
		return feeMianBill;
	}
	public void setFeeMianBill(String feeMianBill) {
		this.feeMianBill = feeMianBill;
	}
	public String getFeeMianAmt() {
		return feeMianAmt;
	}
	public void setFeeMianAmt(String feeMianAmt) {
		this.feeMianAmt = feeMianAmt;
	}
	public String getInvoiceTitle() {
		return invoiceTitle;
	}
	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}
	public String getInvoiceTitleType() {
		return invoiceTitleType;
	}
	public void setInvoiceTitleType(String invoiceTitleType) {
		this.invoiceTitleType = invoiceTitleType;
	}
	public String getCreditCode() {
		return creditCode;
	}
	public void setCreditCode(String creditCode) {
		this.creditCode = creditCode;
	}
	public String getBillId() {
		return billId;
	}
	public void setBillId(String billId) {
		this.billId = billId;
	}
	public String getStmtId() {
		return stmtId;
	}
	public void setStmtId(String stmtId) {
		this.stmtId = stmtId;
	}
	public String getHouseId() {
		return houseId;
	}
	public void setHouseId(String houseId) {
		this.houseId = houseId;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCertType() {
		return certType;
	}
	public void setCertType(String certType) {
		this.certType = certType;
	}
	public String getCertId() {
		return certId;
	}
	public void setCertId(String certId) {
		this.certId = certId;
	}
	public String getAcctNo() {
		return acctNo;
	}
	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getWuyeId() {
		return wuyeId;
	}
	public void setWuyeId(String wuyeId) {
		this.wuyeId = wuyeId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getFromSys() {
		return fromSys;
	}
	public void setFromSys(String fromSys) {
		this.fromSys = fromSys;
	}
	public String getQuickToken() {
		return quickToken;
	}
	public void setQuickToken(String quickToken) {
		this.quickToken = quickToken;
	}
	public String getVeriCode() {
		return veriCode;
	}
	public void setVeriCode(String veriCode) {
		this.veriCode = veriCode;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	
	
}

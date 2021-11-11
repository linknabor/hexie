package com.yumu.hexie.integration.wuye.req;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.integration.wuye.dto.PrepayRequestDTO;
import com.yumu.hexie.service.exception.BizValidateException;

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

		if (!StringUtils.isEmpty(prepayRequestDTO.getCustomerName())) {
			try {
				//中文打码
				this.customerName = URLEncoder.encode(prepayRequestDTO.getCustomerName(),"GBK");
				this.invoiceTitle = URLEncoder.encode(prepayRequestDTO.getInvoiceTitle(), "GBK");
			} catch (UnsupportedEncodingException e) {
				throw new BizValidateException(e.getMessage(), e);	
			}
		}
		if (!StringUtils.isEmpty(prepayRequestDTO.getInvoiceTitle())) {
			try {
				//中文打码
				this.invoiceTitle = URLEncoder.encode(prepayRequestDTO.getInvoiceTitle(), "GBK");
			} catch (UnsupportedEncodingException e) {
				throw new BizValidateException(e.getMessage(), e);	
			}
		}
		if (!StringUtils.isEmpty(prepayRequestDTO.getInvoiceTitle())) {
			try {
				//中文打码
				this.invoiceTitle = URLEncoder.encode(prepayRequestDTO.getInvoiceTitle(), "GBK");
			} catch (UnsupportedEncodingException e) {
				throw new BizValidateException(e.getMessage(), e);	
			}
		}
		if (!StringUtils.isEmpty(prepayRequestDTO.getInvoiceTitle())) {
			try {
				//中文打码
				this.invoiceTitle = URLEncoder.encode(prepayRequestDTO.getInvoiceTitle(), "GBK");
			} catch (UnsupportedEncodingException e) {
				throw new BizValidateException(e.getMessage(), e);	
			}
		}
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
	@JsonProperty("coupon_id")
	private String couponId;
	private String reduceAmt;
	private String openid;
	private String appid;
	private String payee_openid; //收款人的openid
	
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
	
	//银行卡支付参数
	@JsonProperty("pay_type")
	private String payType;	//0微信支付，1银行卡支付
	private String customerName;	//持卡人姓名
	private String certType;	//证件类型
	private String certId;		//证件号
	private String acctNo;		//银行卡号
	private String phoneNo;		//银行预留手机
  
	//下列参数在绑卡记住卡号二次支付时使用
	private String quickToken;	//快捷支付token
	private String veriCode;	//手机验证码
	private String orderNo;		//绑卡支付非首次需要传
	
	//减免参数
	@JsonProperty("rule_type")
	private String ruleType;	//减免类型，多个以半角逗号分隔
	@JsonProperty("reduction_amt")
	private String reductionAmt;	//减免金额，多个以半角逗号分隔
	@JsonProperty("pay_fee_type")
	private String payFeeType;	//01：管理费 02：停车费
	
	//是否二维码支付
	@JsonProperty("is_qrcode")
	private String isQrcode;

	//短信催缴
	@JsonProperty("batch_no")
	private String batchNo;	//短信批次号
	@JsonProperty("cell_id")
	private String cellId;	//房屋id
	
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
	public String getReduceAmt() {
		return reduceAmt;
	}
	public void setReduceAmt(String reduceAmt) {
		this.reduceAmt = reduceAmt;
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
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getRuleType() {
		return ruleType;
	}
	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}
	public String getReductionAmt() {
		return reductionAmt;
	}
	public void setReductionAmt(String reductionAmt) {
		this.reductionAmt = reductionAmt;
	}
	public String getPayFeeType() {
		return payFeeType;
	}
	public void setPayFeeType(String payFeeType) {
		this.payFeeType = payFeeType;
	}
	public String getIsQrcode() {
		return isQrcode;
	}
	public void setIsQrcode(String isQrcode) {
		this.isQrcode = isQrcode;
	}
	public String getPayee_openid() {
		return payee_openid;
	}
	public void setPayee_openid(String payee_openid) {
		this.payee_openid = payee_openid;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getCellId() {
		return cellId;
	}
	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	@Override
	public String toString() {
		return "PrepayRequest [wuyeId=" + wuyeId + ", fromSys=" + fromSys + ", couponUnit=" + couponUnit
				+ ", couponNum=" + couponNum + ", couponId=" + couponId + ", reduceAmt=" + reduceAmt + ", openid="
				+ openid + ", appid=" + appid + ", payee_openid=" + payee_openid + ", mobile=" + mobile
				+ ", invoiceTitle=" + invoiceTitle + ", invoiceTitleType=" + invoiceTitleType + ", creditCode="
				+ creditCode + ", billId=" + billId + ", stmtId=" + stmtId + ", payType=" + payType + ", customerName="
				+ customerName + ", certType=" + certType + ", certId=" + certId + ", acctNo=" + acctNo + ", phoneNo="
				+ phoneNo + ", quickToken=" + quickToken + ", veriCode=" + veriCode + ", orderNo=" + orderNo
				+ ", ruleType=" + ruleType + ", reductionAmt=" + reductionAmt + ", payFeeType=" + payFeeType
				+ ", isQrcode=" + isQrcode + ", batchNo=" + batchNo + ", cellId=" + cellId + "]";
	}
	
}

package com.yumu.hexie.integration.wuye.dto;

import java.io.Serializable;

import com.yumu.hexie.model.user.User;

public class PrepayRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9222329098194603371L;

	private User user;
	
	//优惠减免
	private String couponUnit;
	private String couponNum;
	private String couponId;
	private String reduceAmt;
	
	//发票
	private String invoiceTitle;
	private String invoiceTitleType;
	private String creditCode;
	private String needInvoice;
	private String invoiceType;
	
	//定位用
	private String regionUrl;
	private String regionName;
	
	//专业版参数
	private String billId;
	private String stmtId;
	
	//银行卡支付参数
	private String payType;	//支付类型，0微信支付，1银行卡支付
	private String customerName;	//持卡人姓名
	private String certType;	//证件类型
	private String certId;		//证件号
	private String acctNo;		//银行卡号
	private String phoneNo;		//银行预留手机
	
	private String quickToken;	//快捷支付token
	private String veriCode;	//手机验证码
	
	private String remember;	//是否记住持卡人信息，0否1是
	private String cardId;	//选卡支付标记，选中的记录卡号
	private String orderNo;	//绑卡支付非首次支付需要传入
	
	//减免参数
	private String ruleType;	//减免规则类型
	private String reductionAmt;	//减免金额
	private String payFeeType;	//费用类型，01：管理费 02：停车费
	
	//是否二维码支付
	private String isQrcode;
	private String appid;
	private String payee_openid; //收款人的openid
	
	//短信催缴
	private String cellId;
	private String batchNo;	//批次号
	
	private Boolean tpPrepay;	//是否第三方预支付

	private String payScenarios; //场景
	private String parkName; //停车场名称
	
	private String channelInfo;	//吱口令渠道信息
	private String bindHouse;	//是否绑定房屋，0否1是

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
	public String getRegionUrl() {
		return regionUrl;
	}
	public void setRegionUrl(String regionUrl) {
		this.regionUrl = regionUrl;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
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
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
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
	public String getRemember() {
		return remember;
	}
	public void setRemember(String remember) {
		this.remember = remember;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
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
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getPayee_openid() {
		return payee_openid;
	}
	public void setPayee_openid(String payee_openid) {
		this.payee_openid = payee_openid;
	}
	public String getCellId() {
		return cellId;
	}
	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getNeedInvoice() {
		return needInvoice;
	}

	public void setNeedInvoice(String needInvoice) {
		this.needInvoice = needInvoice;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}
	
	public Boolean getTpPrepay() {
		return tpPrepay;
	}
	
	public void setTpPrepay(Boolean tpPrepay) {
		this.tpPrepay = tpPrepay;
	}

	public String getPayScenarios() {
		return payScenarios;
	}

	public void setPayScenarios(String payScenarios) {
		this.payScenarios = payScenarios;
	}

	public String getParkName() {
		return parkName;
	}

	public void setParkName(String parkName) {
		this.parkName = parkName;
	}
	
	public String getChannelInfo() {
		return channelInfo;
	}
	
	public void setChannelInfo(String channelInfo) {
		this.channelInfo = channelInfo;
	}
	
	public String getBindHouse() {
		return bindHouse;
	}
	
	public void setBindHouse(String bindHouse) {
		this.bindHouse = bindHouse;
	}
	
	@Override
	public String toString() {
		return "PrepayRequestDTO [user=" + user + ", couponUnit=" + couponUnit + ", couponNum=" + couponNum
				+ ", couponId=" + couponId + ", reduceAmt=" + reduceAmt + ", invoiceTitle=" + invoiceTitle
				+ ", invoiceTitleType=" + invoiceTitleType + ", creditCode=" + creditCode + ", needInvoice="
				+ needInvoice + ", invoiceType=" + invoiceType + ", regionUrl=" + regionUrl + ", regionName="
				+ regionName + ", billId=" + billId + ", stmtId=" + stmtId + ", payType=" + payType + ", customerName="
				+ customerName + ", certType=" + certType + ", certId=" + certId + ", acctNo=" + acctNo + ", phoneNo="
				+ phoneNo + ", quickToken=" + quickToken + ", veriCode=" + veriCode + ", remember=" + remember
				+ ", cardId=" + cardId + ", orderNo=" + orderNo + ", ruleType=" + ruleType + ", reductionAmt="
				+ reductionAmt + ", payFeeType=" + payFeeType + ", isQrcode=" + isQrcode + ", appid=" + appid
				+ ", payee_openid=" + payee_openid + ", cellId=" + cellId + ", batchNo=" + batchNo + ", tpPrepay="
				+ tpPrepay + ", payScenarios=" + payScenarios + ", parkName=" + parkName + ", channelInfo="
				+ channelInfo + ", bindHouse=" + bindHouse + "]";
	}
	
	
}

package com.yumu.hexie.model.user;

import com.yumu.hexie.model.BaseModel;

/**
 * 银行卡信息
 * @author david
 *
 */
public class BankCard extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4553934353165385867L;

	private String acctNo;	//卡号
	private String acctName;	//持卡人名称
	private String phoneNo;	//银行预留手机号
	private String quickToken;	//快捷支付token，首次绑卡成功后产生
	private String bankName;	//开卡银行名称
	private String bankCode;
	private long userId;	//对应系统内的用户信息
	private String userName;	//用户公众号名称
	private String branchName;	//卡所属支行名称
	private String branchNo;	//支行编号
	public String getAcctNo() {
		return acctNo;
	}
	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}
	public String getAcctName() {
		return acctName;
	}
	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getQuickToken() {
		return quickToken;
	}
	public void setQuickToken(String quickToken) {
		this.quickToken = quickToken;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getBranchNo() {
		return branchNo;
	}
	public void setBranchNo(String branchNo) {
		this.branchNo = branchNo;
	}
	
	

}


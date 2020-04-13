package com.yumu.hexie.model.user;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.yumu.hexie.common.util.desensitize.annotation.Sensitive;
import com.yumu.hexie.common.util.desensitize.enums.SensitiveType;
import com.yumu.hexie.model.BaseModel;

/**
 * 银行卡信息
 * @author david
 *
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames="acctNo")})	
public class BankCard extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4553934353165385867L;

	@Sensitive(SensitiveType.BANK_CARD)
	private String acctNo;	//卡号
	@Sensitive(SensitiveType.CHINESE_NAME)
	private String acctName;	//持卡人名称
	private String certType;	//证件类型
	@Sensitive(SensitiveType.ID_CARD)
	private String certId;	//证件号码
	@Sensitive(SensitiveType.MOBILE)
	private String phoneNo;	//银行预留手机号
	private String quickToken;	//快捷支付token，首次绑卡成功后产生
	private String bankName;	//开卡银行名称
	private String bankCode;
	private long userId;	//对应系统内的用户信息
	private String userName;	//用户公众号名称
	private String branchName;	//卡所属支行名称
	private String branchNo;	//支行编号
	private int cardType;
	
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
	public int getCardType() {
		return cardType;
	}
	public void setCardType(int cardType) {
		this.cardType = cardType;
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
	

}


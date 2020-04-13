package com.yumu.hexie.web.user.resp;

import java.io.Serializable;

import com.yumu.hexie.common.util.desensitize.annotation.Sensitive;
import com.yumu.hexie.common.util.desensitize.enums.SensitiveType;

public class BankCardVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 245639007892412390L;
	@Sensitive(SensitiveType.BANK_CARD)
	private String acctNo;	//卡号
	@Sensitive(SensitiveType.CHINESE_NAME)
	private String acctName;	//持卡人名称
	@Sensitive(SensitiveType.MOBILE)
	private String phoneNo;	//银行预留手机号
	private int cardType;	//1借记卡，2贷记卡
	
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
	public int getCardType() {
		return cardType;
	}
	public void setCardType(int cardType) {
		this.cardType = cardType;
	}
	
	
}

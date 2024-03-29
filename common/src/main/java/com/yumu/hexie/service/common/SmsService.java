package com.yumu.hexie.service.common;

import java.util.Date;

import com.yumu.hexie.model.user.User;


/**
 * Created by Administrator on 2014/12/1.
 */
public interface SmsService {
	
	public boolean sendMsg(User user,String mobile,String msg,long id);
	
	public boolean sendMsg(User user, String mobile, String msg, long id, int msgType);
	
    public boolean sendVerificationCode(User user, String mobilePhone, String requestIp, int msgType);

    public boolean checkVerificationCode(String mobilePhone, String verificationCode);

	public int getByPhoneAndMesssageTypeInOneMonth(String mobilePhone, int messageType, Date date);

	String getRandomToken();
	
	String saveAndGetInvoiceToken(String tradeWaterId);
	
	boolean verifySmsToken(String tradeWaterId, int type, String token, String appid);
	
	String saveAndGetReceiptToken(String tradeWaterId, String appid);

	boolean sendInvoiceVerificationCode(User user, String mobilePhone, String requestIp, int msgType,
			String tradeWaterId);

	

}

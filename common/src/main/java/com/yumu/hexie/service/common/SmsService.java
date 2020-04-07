package com.yumu.hexie.service.common;

import java.util.Date;

import com.yumu.hexie.model.user.User;
import com.yumu.hexie.vo.SmsMessage;


/**
 * Created by Administrator on 2014/12/1.
 */
public interface SmsService {
	
	public boolean sendMsg(User user, SmsMessage smsMessage, long id);
	
	public boolean sendMsg(User user, SmsMessage smsMessage, long id, int msgType);
	
    public boolean sendVerificationCode(User user, String mobilePhone, String requestIp);

    public boolean checkVerificationCode(String mobilePhone, String verificationCode);

	public int getByPhoneAndMesssageTypeInOneMonth(String mobilePhone, int messageType, Date date);

	String getRandomToken();
	
	String saveAndGetInvoiceToken(String tradeWaterId);
	
	boolean verifySmsToken(String tradeWaterId, String token);

}

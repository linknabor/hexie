package com.yumu.hexie.service.common.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.yumu.hexie.common.util.AppUtil;
import com.yumu.hexie.common.util.RandomStringUtils;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.eucp.CreateBlueUtil;
import com.yumu.hexie.integration.eucp.YimeiUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.commonsupport.gotong.SmsHis;
import com.yumu.hexie.model.commonsupport.gotong.SmsHisRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SmsService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.exception.BizValidateException;

/**
 * Created by Administrator on 2014/12/1.
 */
@Service(value = "smsService")
public class SmsServiceImpl implements SmsService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SmsServiceImpl.class);
	private static final String VERICODE_MESSAGE = "短信验证码{0}，在30分钟内输入有效。";

	@Inject
	private SystemConfigService systemConfigService;
    @Inject
    private SmsHisRepository smsHisRepository;
    @Value(value = "${sms.expire.minutes}")
    private Long expireMinutes;
    @Value(value = "${testMode}")
    private String testMode;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private CreateBlueUtil createBlueUtil;
    @Autowired
    private YimeiUtil yimeiUtil;
    
    /**
     * 发送短信验证码
     */
    @Override
    public boolean sendVerificationCode(User user, String mobilePhone) {
      
    	String code = RandomStringUtils.randomNumeric(6);
    	String message = MessageFormat.format(VERICODE_MESSAGE, code);
    	checkMsgFrequency(mobilePhone);
    	return sendMessage(user, mobilePhone, message, code);
    }

    /**
     * 校验短信验证码
     */
    @Override
    public boolean checkVerificationCode(String mobilePhone, String verificationCode) {
        SmsHis smsHis = getSmsFromCache(mobilePhone);
        return smsHis != null && verificationCode.equalsIgnoreCase(smsHis.getCode());
    }
    
    @Override
    public int getByPhoneAndMesssageTypeInOneMonth(String mobilePhone, int messageType, Date date){
    	
    	return smsHisRepository.findByPhoneAndMesssageTypeInOneMonth(mobilePhone, messageType, date);
    }

	@Override
	public boolean sendMsg(User user,String mobile, String msg,long id) {
		
		return sendMsg(user, mobile, msg, id, 0);
	}
	
	@Override
	public boolean sendMsg(User user,String mobile, String msg,long id, int msgType) {
		
		return sendMessage(user, mobile, msg, null);
	}
	
	private String getMsgSignature(String appId){
		
		//是否使用自定义签名
		String use_default_sign = systemConfigService.getSysConfigByKey("USE_DEFINED_MSG_SIGN"); 
		
		//1：自定义签名。0或者空：供应商签合协社区
		if (!"1".equals(use_default_sign)) {
			return ""; 
		}
		
		String sign = "";
		String key = "DEFAULT_SIGN";
		if (AppUtil.isMainApp(appId)||StringUtil.isEmpty(appId)) {
			//do nothing
		}else {
			key = key + "_" + appId;
		}
		sign = systemConfigService.getSysConfigByKey(key);	//形如：DEFAULT_SIGN_wxa48ca61b68163483
		
		sign = "【"+sign+"】";
		
		if (StringUtil.isEmpty(sign)) {
			LOGGER.warn("未配置系统参数DEFAULT_SIGN，默认值：合协社区");
			sign = "【合协社区】";
		}
		return sign;
	}
	
	/**
	 * 发送短信公共函数
	 * @param user
	 * @param mobilePhone
	 * @param message
	 * @return
	 */
	private boolean sendMessage(User user, String mobilePhone, String message, String code) {
		
		SmsHis smsHis = getSmsFromCache(mobilePhone);
        if (smsHis == null) {
			String sign = getMsgSignature(user.getAppId());
	        message = sign.concat(message);
	        smsHis = new SmsHis();
	        smsHis.setId(0l);
	        smsHis.setCode(code);
	        smsHis.setMsg(message);
	        smsHis.setSendDate(new Date());
	        smsHis.setPhone(mobilePhone);
	        smsHis.setUserId(user.getId());
	        saveSms2cache(smsHis);
		}
        
		String sendMsg = systemConfigService.getSysConfigByKey("SEND_MSG"); //TODO cache  
		boolean ret = false;
        if(!"0".equals(sendMsg)){
        	if (systemConfigService.querySmsChannel()==0) {
        		ret = yimeiUtil.sendMessage(mobilePhone, smsHis.getMsg(), smsHis.getId());//.sendBatchMessage(account, password, mobilePhone, message);
			}else {
				ret = createBlueUtil.sendMessage(mobilePhone, smsHis.getMsg());
			}
        } else {
        	ret = true;
        }
        return ret;
		
		
	}
	
	/**
	 * 从缓存服务器中获取短信验证码
	 * @param mobile
	 * @return
	 */
	private SmsHis getSmsFromCache(String mobile) {
		
		String key = ModelConstant.KEY_MOBILE_VERICODE + mobile;
		SmsHis smsHis = (SmsHis) redisTemplate.opsForValue().get(key);
		return smsHis;
		
	}
	
	/**
	 * 短信缓存到redis
	 * @param smsHis
	 * @return
	 */
	private void saveSms2cache(SmsHis smsHis) {
		String key = ModelConstant.KEY_MOBILE_VERICODE + smsHis.getPhone();
		redisTemplate.opsForValue().set(key, smsHis, 30, TimeUnit.MINUTES);	//30分钟超时
	}
	
	/**
	 * 校验短信发送频率
	 */
	public void checkMsgFrequency(String mobile) {
		
		String key = ModelConstant.KEY_VERICODE_FREQUENCY + mobile;
		Object lastSent = redisTemplate.opsForValue().get(key);
		if (lastSent != null) {
			throw new BizValidateException("发送过于频繁，请稍后再试");
		}else {
			redisTemplate.opsForValue().set(key, 1, 1, TimeUnit.MINUTES);	//设置1分钟超时，如果一分钟内访问，提示发送过于频繁
		}
		
	}
	
}

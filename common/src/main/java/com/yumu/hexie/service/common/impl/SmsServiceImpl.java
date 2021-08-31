package com.yumu.hexie.service.common.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.AppUtil;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.MD5Util;
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
    @Autowired
    private StringRedisTemplate stringRedisTemplate; 
    @Autowired
    private CreateBlueUtil createBlueUtil;
    @Autowired
    private YimeiUtil yimeiUtil;
    
    /**
     * 发送短信验证码
     */
    @Override
    public boolean sendVerificationCode(User user, String mobilePhone, String requestIp, int msgType) {
      
    	String code = RandomStringUtils.randomNumeric(6);
    	String message = MessageFormat.format(VERICODE_MESSAGE, code);
    	checkIpFrequency(requestIp);
    	checkMsgFrequency(mobilePhone);
    	checkMsgTotalLimit(mobilePhone);
    	return sendMessage(user, mobilePhone, message, code, msgType);

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
		
		return sendMessage(user, mobile, msg, null, msgType);

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
		
		if (StringUtil.isEmpty(sign)) {
			LOGGER.warn("未配置系统参数DEFAULT_SIGN，默认值：合协社区");
			sign = "合协社区";
		}
		sign = "【"+sign+"】";
		return sign;
	}
	
	/**
	 * 发送短信公共函数
	 * @param user
	 * @param mobilePhone
	 * @param message
	 * @return
	 */
	private boolean sendMessage(User user, String mobilePhone, String message, String code, int msgType) {
		
		SmsHis smsHis = null;
		if (ModelConstant.SMS_TYPE_REG == msgType || ModelConstant.SMS_TYPE_INVOICE == msgType) {
			smsHis = getSmsFromCache(mobilePhone);
		}
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
	        if (!StringUtils.isEmpty(user.getName())) {
	        	smsHis.setUserName(user.getName());
			}
	        if (ModelConstant.SMS_TYPE_REG == msgType || ModelConstant.SMS_TYPE_INVOICE == msgType || 
	        		ModelConstant.SMS_TYPE_PROMOTION_PAY == msgType || ModelConstant.SMS_TYPE_RESET_PASSWORD == msgType) {
	        	saveSms2Cache(smsHis);
	        }
	        smsHisRepository.save(smsHis);	//TODO 这个以后去掉
		}
        
		String sendMsg = systemConfigService.getSysConfigByKey("SEND_MSG");
		boolean ret = false;
        if("1".equals(sendMsg)){
        	if (systemConfigService.querySmsChannel()==0) {
        		ret = yimeiUtil.sendMessage(mobilePhone, smsHis.getMsg(), smsHis.getId());//.sendBatchMessage(account, password, mobilePhone, message);
			}else {
				ret = createBlueUtil.sendMessage(mobilePhone, smsHis.getMsg());
			}
        } else {
        	ret = true;
        }
        LOGGER.info("sendMessage ret :" + ret);
        return ret;
		
		
	}
	
	/**
	 * 从缓存服务器中获取短信验证码
	 * @param mobile
	 * @return
	 */
	private SmsHis getSmsFromCache(String mobile) {
		
		String key = ModelConstant.KEY_MOBILE_VERICODE + mobile;
		String content = stringRedisTemplate.opsForValue().get(key);
		SmsHis smsHis = null;
		try {
			if (!StringUtils.isEmpty(content)) {
				smsHis = JacksonJsonUtil.getMapperInstance(false).readValue(content, SmsHis.class);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return smsHis;
		
	}
	
	/**
	 * 短信缓存到redis
	 * @param smsHis
	 * @return
	 */
	private void saveSms2Cache(SmsHis smsHis) {
		String key = ModelConstant.KEY_MOBILE_VERICODE + smsHis.getPhone();
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		try {
			String content = objectMapper.writeValueAsString(smsHis);	//这里实体转成json字符串存储，因为backend需要再查出来，不能直接序列化成指定类型的实体
			stringRedisTemplate.opsForValue().set(key, content, 30, TimeUnit.MINUTES);	//30分钟超时
		} catch (JsonProcessingException e) {
			LOGGER.error(e.getMessage(), e);
		}	
		
	}
	
	/**
	 * 校验短信发送频率
	 * 1.校验同一手机号一定时间段内（1分钟内）的发送次数
	 */
	public void checkMsgFrequency(String mobile) {
		
		String key = ModelConstant.KEY_VERICODE_FREQUENCY + mobile;
		Object lastSent = stringRedisTemplate.opsForValue().get(key);
		if (lastSent != null) {
			throw new BizValidateException("发送过于频繁，请稍后再试");
		}else {
			stringRedisTemplate.opsForValue().set(key, "1", 5, TimeUnit.MINUTES);	//设置1分钟超时，如果一分钟内访问，提示发送过于频繁
		}
		
	}
	
	 /**
	 * 校验短信发送频率
	 *  1.校验同一个手机号一天内的发送次数（10条）
	 */
	public void checkMsgTotalLimit(String mobile) {
		
		String key = ModelConstant.KEY_VERICODE_TOTAL_LIMIT + mobile;
		Object totalSent = stringRedisTemplate.opsForValue().get(key);
		if (totalSent != null) {
			Long sent = stringRedisTemplate.opsForValue().increment(key, 1);
			if (sent > 20) {
				throw new BizValidateException("当日短信验证码发送次数超限，请联系社区客服。");
			}
		}else {
			stringRedisTemplate.opsForValue().set(key, "1", 24, TimeUnit.HOURS);	//设置一天内10条
		}
		
	}
	
	/**
	 * 校验短信发送频率
	 * 2.校验同一IP一定时间内的发送次数
	 */
	public void checkIpFrequency(String requestIp) {
		
		String key = ModelConstant.KEY_VERICODE_IP_FREQUENCY + requestIp;
		Object totalSent = stringRedisTemplate.opsForValue().get(key);
		if (totalSent != null) {
			Long sent = stringRedisTemplate.opsForValue().increment(key, 1);
			if (sent > 3) {
				throw new BizValidateException("发送过于频繁，请稍后再试");
			}
		}else {
			stringRedisTemplate.opsForValue().increment(key, 1);
			stringRedisTemplate.expire(key, 24, TimeUnit.HOURS);	//对于同一IP，设置30分钟内的访问次数限制
		}
		
	}
	
	public static void main(String[] args) {
		
		String trade_water_id = "191216135426990186";
		String str = MD5Util.MD5Encode(trade_water_id, "");
		System.out.println(str);
	}
	
	/**
	 * 校验申请发票短信
	 *	给传上来的交易号打个码，打完码的token放在服务器端。请求验证码时需要前端将下发的token码重新带回来，否则不予以发短信
	 */
	@Override
	public String saveAndGetInvoiceToken(String tradeWaterId) {
		
		String key = ModelConstant.KEY_VERICODE_TRADE_ID + tradeWaterId;
		Object value = stringRedisTemplate.opsForValue().get(key);
		String token = "";
		if (value == null) {
			token = MD5Util.MD5Encode(tradeWaterId, "");
			stringRedisTemplate.opsForValue().set(key, token, 30, TimeUnit.MINUTES);
		}else {
			token = (String) value;
		}
		return token;
		
	}

	@Override
	public boolean verifySmsToken(String tradeWaterId, String token) {

		if (StringUtils.isEmpty(token)) {
			return false;
		}
		String key = ModelConstant.KEY_VERICODE_TRADE_ID + tradeWaterId;
		String serverToken = (String)stringRedisTemplate.opsForValue().get(key);
		LOGGER.info("token : " + token + ", serverToken : " + serverToken);
		if (!token.equals(serverToken)) {
			return false;
		}
		return true;
	}

	/**
	 * 获取随机无效token
	 */
	@Override
	public String getRandomToken() {

		String random = RandomStringUtils.random(10);
		return MD5Util.MD5Encode(random, "");
	}
	
	
}

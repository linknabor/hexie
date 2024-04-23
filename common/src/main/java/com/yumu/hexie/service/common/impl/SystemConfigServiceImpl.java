/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.common.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.AppUtil;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.integration.wechat.entity.AccessToken;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.system.SystemConfig;
import com.yumu.hexie.model.system.SystemConfigRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.common.pojo.dto.ActiveApp;
import com.yumu.hexie.service.exception.BizValidateException;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: SystemConfigServiceImpl.java, v 0.1 2016年4月7日 下午4:39:59  Exp $
 */
@Service("systemConfigService")
public class SystemConfigServiceImpl implements SystemConfigService {
	
	private static final Logger log = LoggerFactory.getLogger(SystemConfigServiceImpl.class);

    private static final String JS_TOKEN = "JS_TOKEN";
    private static final String ACC_TOKEN = "ACCESS_TOKEN";
    private static final String KEY_APP_SYS = "APP_SYS_";
    private static final String KEY_APPID_ACTIVE = "APPID_ACTIVE_";	//key后面接公众号的APPID，value是当前起作用的公众号或者小程序的的appid
    private static final String KEY_APP_MAPPING = "MP_MAPPING_";	//小程序对应的公众号，key后面接小程序APPID，value是对应公众号的APPID
	private static Map<String, String> sysMap = new HashMap<>();
	private static Map<String, SystemConfig> sysConfigParam = new HashMap<>();
    private static String REQUEST_URL;
	
	
    @Inject
    private SystemConfigRepository systemConfigRepository;
    @Inject
    private RedisRepository redisRepository;
    @Value("${requestUrl}")
	private String requestUrl;
    
    
    /**
     * 启动时加载到redis缓存中
     */
    @PostConstruct
    public void initSystemConfig() {
    	
    	List<SystemConfig> configList = systemConfigRepository.findAll();
    	if (configList == null || configList.isEmpty()) {
			log.error("未配置系统参数表systemConfig!");
		}
    	for (SystemConfig systemConfig : configList) {
    		String sysKey = systemConfig.getSysKey();
    		
    		sysConfigParam.put(sysKey, systemConfig);
    		
    		
    		try {
				if (sysKey.indexOf(KEY_APP_SYS) > -1) {
					String key = sysKey.substring(sysKey.indexOf(KEY_APP_SYS) + KEY_APP_SYS.length(), sysKey.length());
					sysMap.put(key, systemConfig.getSysValue());
				}
			} catch (Exception e) {
				log.error("未配置系统参数表systemConfig中的APP_SYS_!");
			}
    		
		}
    	
    	REQUEST_URL = requestUrl;
    	
    	log.info("系统appId映射： " + sysMap);
    	
    	
    }
    
    /**
     * 获取短信渠道
     */
    @Override
    public int querySmsChannel() {
    	
        String sysValue = getSysConfigByKey("SMS_CHANNEL");
        if (!"0".equals(sysValue)) {
            return 1;
        }
        return 0;
        
    }
    
    /**
     * 获取红包活动时间段
     */
    @Override
    public String[] queryActPeriod() {
    	
    	String datePeriod = getSysConfigByKey("ACT_PERIOD");
    	if (!StringUtil.isEmpty(datePeriod)) {
			return datePeriod.split(",");
		}else {
			return new String[0];
		}
    	
    }
    
    /**
     * 不参与红包活动的项
     */
    @Override
    public Set<String> getUnCouponItems() {
    	
    	String sysValue = getSysConfigByKey("NOCOUPON_ITEMS");
        Set<String> res = new HashSet<String>();
        if (!StringUtil.isEmpty(sysValue)) {
        	for(String idStr: sysValue.split(",")) {
                res.add(idStr.trim());
            }
		}
        return res;
    }
    
    /**
     * 从缓存中去系统配置参数
     * @param key
     * @return
     */
    private SystemConfig getConfigFromCache(String key){
    	
    	SystemConfig systemConfig = redisRepository.getSystemConfig(key);
    	if (systemConfig == null) {
    		systemConfig = new SystemConfig();
    	}
    	return systemConfig;
    
    }
    
    /**
     * 根据appid获取微信公众号的accessToken
     * @param appId
     * @return
     */
    public String queryWXAToken(String appId) {
    	
    	if (AppUtil.isMainApp(appId) || StringUtils.isEmpty(appId)) {
    		SystemConfig config = getConfigFromCache(ACC_TOKEN);
            if (config != null) {
                try {
                    AccessToken at = (AccessToken) JacksonJsonUtil.jsonToBean(config.getSysValue(), AccessToken.class);
                    return at.getToken();
                } catch (JSONException e) {
                   log.error("queryWXAccToken failed :", e);
                }
            }
            throw new BizValidateException("未获取到token, appid : " + appId);
		
    	} else {
    		
    		String authorizerAccessToken = "";
			try {
				authorizerAccessToken = redisRepository.getAuthorizerAccessToken(ConstantWeChat.KEY_AUTHORIZER_ACCESS_TOKEN + appId);
			} catch (Exception e) {
				log.error("queryWXAccToken failed :", e);
			}
    		return authorizerAccessToken;
    		
		}
    	
    }
    
    /**
     * 根据appid获取微信公众号的jsticket
     * @param appId
     * @return
     */
    @Override
	public String queryJsTickets(String appId) {
        
    	String tickets = "";
    	log.info("appId is : " + appId + ", is main :" + AppUtil.isMainApp(appId));
        if (AppUtil.isMainApp(appId) || StringUtils.isEmpty(appId)) {
        	SystemConfig config = getConfigFromCache(JS_TOKEN);
	        if (config != null) {
	            tickets = config.getSysValue();
	        }
        } else {
        	
        	tickets = redisRepository.getAuthorizerJsTicket(ConstantWeChat.KEY_AUTHORIZER_JS_TICKET + appId);
        }
        return tickets;
    }
   
    @Override
	public String getSysConfigByKey(String key) {
		
    	String value = "";
    	SystemConfig systemConfig = sysConfigParam.get(key);
    	if (systemConfig != null) {
			value = systemConfig.getSysValue();
		}
		return value;
	}
    
    /**
     * 重新加载系统参数
     */
    @Override
    public void reloadSysConfigCache() {
    	
    	sysConfigParam = null;
    	sysConfigParam = new HashMap<>();
    	List<SystemConfig> configList = systemConfigRepository.findAll();
    	if (configList == null) {
			log.error("未配置系统参数表systemConfig!");
			return;
		}
    	for (SystemConfig systemConfig : configList) {
    		
    		String sysKey = systemConfig.getSysKey();
    		sysConfigParam.put(sysKey, systemConfig);
    		try {
				if (sysKey.indexOf(KEY_APP_SYS) > -1) {
					String key = sysKey.substring(sysKey.indexOf(KEY_APP_SYS) + KEY_APP_SYS.length(), sysKey.length());
					sysMap.put(key, systemConfig.getSysValue());
				}
			} catch (Exception e) {
				log.error("未配置系统参数表systemConfig中的APP_SYS_!");
			}
    	
    	}
    	log.info("系统appId映射： " + sysMap);
    }

	public static Map<String, String> getSysMap() {
		return sysMap;
	}
	
	/**
	 * 公众号是否开启卡券功能
	 */
	@Override
	public boolean isCardServiceAvailable (String appId){
		
		if (StringUtils.isEmpty(appId)) {	//小程序用户
			return false;
		}
		String appIds = getSysConfigByKey("CARD_SERVICE_APPS");
		boolean isAvailable = false;
		if (!StringUtils.isEmpty(appIds)) {
			if (appIds.indexOf(appId) > -1) {
				isAvailable = true;
			}
		}
		return isAvailable;
		
	}
	
	/**
	 * 公众号是否开肺炎抗疫板块
	 */
	@Override
	public boolean coronaPreventionAvailable (String appId){
		
		if (StringUtils.isEmpty(appId)) {	//小程序用户
			return false;
		}
		
		String appIds = getSysConfigByKey("CORONA_PREVENTION_APPS");
		boolean isAvailable = false;
		if (!StringUtils.isEmpty(appIds)) {
			if (appIds.indexOf(appId) > -1) {
				isAvailable = true;
			}
		}
		return isAvailable;
	}
	
	/**
	 * 物业首页社区生活板块是否开启。目前只有东湖是关闭
	 */
	@Override
	public boolean isDonghu (String appId){
		
		if (StringUtils.isEmpty(appId)) {	//小程序用户
			return false;
		}
		
		String appIds = getSysConfigByKey("DONGHU_LIKE_APPS");	//类似东湖这种性质的公众号列表
		boolean isAvailable = false;
		if (!StringUtils.isEmpty(appIds)) {
			if (appIds.indexOf(appId) > -1) {
				isAvailable = true;
			}
		}
		return isAvailable;
	}
	
	/**
	 * 当前公众号是否发放注册红包
	 */
	@Override
	public boolean registerCouponServiceAvailabe (String appId){
		
		if (StringUtils.isEmpty(appId)) {	//小程序用户
			return false;
		}
		
		String appIds = getSysConfigByKey("REGISTER_COUPON_SERVICE_APPS");	//开启注册发优惠券的APP列表
		boolean isAvailable = false;
		if (!StringUtils.isEmpty(appIds)) {
			if (appIds.indexOf(appId) > -1) {
				isAvailable = true;
			}
		}
		return isAvailable;
	}
	
	/**
	 * 当前公众号是否开启银行卡支付
	 */
	@Override
	public boolean isCardPayServiceAvailabe (String appId){
		
		if (StringUtils.isEmpty(appId)) {	//小程序用户
			return false;
		}
		
		String appIds = getSysConfigByKey("CARD_PAY_SERVICE_APPS");	//开启银行卡支付的公众号APP列表
		boolean isAvailable = false;
		if (!StringUtils.isEmpty(appIds)) {
			if (appIds.indexOf(appId) > -1) {
				isAvailable = true;
			}
		}
		return isAvailable;
	}
	
	/**
	 * 当前公众号是否关联了小程序
	 */
	@Override
	public boolean isMiniprogramAvailabe (String appId){
		
		if (StringUtils.isEmpty(appId)) {	//小程序用户
			return false;
		}
		
		String appIds = getSysConfigByKey("MINIPROGRAM_SERVICE_APPS");
		boolean isAvailable = false;
		if (!StringUtils.isEmpty(appIds)) {
			if (appIds.indexOf(appId) > -1) {
				isAvailable = true;
			}
		}
		return isAvailable;
	}
	
	public static String getREQUEST_URL() {
		return REQUEST_URL;
	}

	@Override
	public List<SystemConfig> getAll() {
		return systemConfigRepository.findAll();
	}
	
	/**
	 * 获取当前用户生效的appid(看是取小程序的appid还是公众号的appid)
	 * @param user
	 * @return
	 */
	@Override
	public ActiveApp getActiveApp(User user) {
	
		String activeAppid = "";
		String activeOpenid = "";
		if (StringUtils.isEmpty(user.getAppId())) {
			activeAppid = user.getMiniAppId();
			activeOpenid = user.getMiniopenid();
		} else {
			activeAppid = getSysConfigByKey(KEY_APPID_ACTIVE + user.getAppId());
			if (StringUtils.isEmpty(activeAppid) || activeAppid.equals(user.getAppId())) {
				activeAppid = user.getAppId();
				activeOpenid = user.getOpenid();
			} else if (activeAppid.equals(user.getMiniAppId())) {
				activeAppid = user.getMiniAppId();
				activeOpenid = user.getMiniopenid();
			}
		}
		ActiveApp activeApp = new ActiveApp(activeAppid, activeOpenid);
		return activeApp;
	}
	
	/**
	 * 获取小程序所对应的公众号，一旦对应，当前公众号用户将会与小程序用户合并
	 * @param miniAppid
	 * @return
	 */
	@Override
	public String getMiniProgramMappedApp(String miniAppid) {
		
		String appid = "";
		if (!StringUtils.isEmpty(miniAppid)) {
			appid = getSysConfigByKey(KEY_APP_MAPPING + miniAppid);
		}
		return appid;
	}
    
}

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.AppUtil;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.integration.wechat.entity.AccessToken;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.system.SystemConfig;
import com.yumu.hexie.model.system.SystemConfigRepository;
import com.yumu.hexie.service.common.SystemConfigService;
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
	private static Map<String, String> sysMap = new HashMap<>();
    
    @Inject
    private SystemConfigRepository systemConfigRepository;
    @Inject
    private RedisRepository redisRepository;
    @Autowired
    private RedisTemplate<String, SystemConfig> redisTemplate;
    
    
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
    		redisTemplate.opsForHash().put(ModelConstant.KEY_SYS_CONFIG, sysKey, systemConfig);
    		
    		try {
				if (sysKey.indexOf(KEY_APP_SYS) > -1) {
					String key = sysKey.substring(sysKey.indexOf(KEY_APP_SYS) + 1);
					sysMap.put(key, systemConfig.getSysValue());
				}
			} catch (Exception e) {
				throw new BizValidateException(e.getMessage(), e);
			}
    		
		}
    	
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
            throw new BizValidateException("微信token没有记录");
		
    	} else {
    		
    		String authorizerAccessToken = redisRepository.getAuthorizerAccessToken(ConstantWeChat.KEY_AUTHORIZER_ACCESS_TOKEN + appId);
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
    	log.info("appId is : " + appId, "is main :" + AppUtil.isMainApp(appId));
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
    	SystemConfig systemConfig = (SystemConfig) redisTemplate.opsForHash().get(ModelConstant.KEY_SYS_CONFIG, key);
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
    	
    	redisTemplate.delete(ModelConstant.KEY_SYS_CONFIG);
    	List<SystemConfig> configList = systemConfigRepository.findAll();
    	if (configList == null) {
			log.error("未配置系统参数表systemConfig!");
			return;
		}
    	for (SystemConfig systemConfig : configList) {
    		
    		String sysKey = systemConfig.getSysKey();
    		redisTemplate.opsForHash().put(ModelConstant.KEY_SYS_CONFIG, sysKey, systemConfig);
    		try {
				if (sysKey.indexOf(KEY_APP_SYS) > -1) {
					String key = sysKey.substring(sysKey.indexOf(KEY_APP_SYS) + 1);
					sysMap.put(key, systemConfig.getSysValue());
				}
			} catch (Exception e) {
				throw new BizValidateException(e.getMessage(), e);
			}
    	
    	}
    }

	public static Map<String, String> getSysMap() {
		return sysMap;
	}
    
    
}

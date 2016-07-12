/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.common.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.wechat.entity.AccessToken;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.system.SystemConfig;
import com.yumu.hexie.model.system.SystemConfigRepository;
import com.yumu.hexie.service.common.SystemConfigService;

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

    private static final String JS_TOKEN = "JS_TOKEN";
    private static final String ACC_TOKEN = "ACCESS_TOKEN";
    @Inject
    private SystemConfigRepository systemConfigRepository;
    @Inject
    private RedisRepository redisRepository;
    /** 
     * @return
     * @see com.yumu.hexie.service.common.SystemConfigService#querySmsChannel()
     */
    @Override
    public int querySmsChannel() {
        List<SystemConfig> list = systemConfigRepository.findAllBySysKey("SMS_CHANNEL");
        if (list.size()>0) {
            SystemConfig config = list.get(0);
            String setting = config.getSysValue();
            
            if (!"0".equals(setting)) {
                return 1;
            }
        }
        return 0;
    }
    public void saveAccessTokenInfo(AccessToken at) {
        try {
            SystemConfig config = null;
            List<SystemConfig> configs = systemConfigRepository
                    .findAllBySysKey(ACC_TOKEN);
            if (configs!=null&&configs.size() > 0) {
                config = configs.get(0);
                
                    config.setSysValue(JacksonJsonUtil.beanToJson(at));
                
            } else {
                config = new SystemConfig(ACC_TOKEN,
                        JacksonJsonUtil.beanToJson(at));
            }
            systemConfigRepository.save(config);
        } catch (JSONException e) {
        }
    }
    public AccessToken queryWXAccToken() {
        AccessToken token = null;
        SystemConfig config = getConfigWithCache(ACC_TOKEN);
        if (config != null) {
            try {
                token = (AccessToken) JacksonJsonUtil.jsonToBean(
                        config.getSysValue(), AccessToken.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return token;
    }
    public void saveJsToken(String jsToken) {
        SystemConfig config = null;
        List<SystemConfig> configs = systemConfigRepository
                .findAllBySysKey(JS_TOKEN);
        if (configs.size() > 0) {
            config = configs.get(0);
            config.setSysValue(jsToken);
        } else {
            config = new SystemConfig(JS_TOKEN, jsToken);
        }
        systemConfigRepository.save(config);
    }
    public String queryJsTickets() {
        String tickets = "";
        SystemConfig config = getConfigWithCache(JS_TOKEN);
        if (config != null) {
            tickets = config.getSysValue();
        }
        return tickets;
    }
    
    public String[] queryActPeriod() {
        List<SystemConfig> list = systemConfigRepository.findAllBySysKey("ACT_PERIOD");
        if (list.size()>0) {
            
            SystemConfig systemConfig = list.get(0);
            String datePeriod = systemConfig.getSysValue();
            
            return datePeriod.split(",");
        } else {
            return new String[0];
        }
    }
    
    public Set<String> getUnCouponItems() {
        Set<String> res = new HashSet<String>();
        String key = "NOCOUPON_ITEMS";
        SystemConfig systemConfig = getConfigWithCache(key);
        if(systemConfig != null) {
            String ids = systemConfig.getSysValue();
            for(String idStr: ids.split(",")) {
                res.add(idStr.trim());
            }
        }
        return res;
    }
    private SystemConfig getConfigWithCache(String key) {
        SystemConfig systemConfig = redisRepository.getSystemConfig(key);
        if(systemConfig == null) {
            List<SystemConfig> list = systemConfigRepository.findAllBySysKey(key);
            if (list.size()>0) {
                systemConfig = list.get(0);
                redisRepository.setSystemConfig(key, systemConfig);
            }
        }
        return systemConfig;
    }
    
    @Override
	public String queryValueByKey(String key) {
		
		String ret = "";
		List<SystemConfig> list = systemConfigRepository.findAllBySysKey(key);
		if (list.size()>0) {
			SystemConfig config = list.get(0);
			ret = config.getSysValue();
		}
	
		return ret;
	}
    
}

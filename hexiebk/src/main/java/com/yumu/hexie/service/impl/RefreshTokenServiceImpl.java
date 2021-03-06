/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.wechat.entity.AccessToken;
import com.yumu.hexie.integration.wechat.util.WeixinUtil;
import com.yumu.hexie.model.redis.Keys;
import com.yumu.hexie.model.system.SystemConfig;
import com.yumu.hexie.service.RefreshTokenService;
import com.yumu.hexie.service.common.SystemConfigService;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: RefreshTokenServiceImpl.java, v 0.1 2016年5月9日 下午8:03:35  Exp $
 */
@Service("refreshTokenService")
public class RefreshTokenServiceImpl implements RefreshTokenService {

	private static final String ACC_TOKEN = "ACCESS_TOKEN";
	private static final String JS_TOKEN = "JS_TOKEN";
	
    private static final Logger logger = LoggerFactory.getLogger("com.yumu.hexie.schedule");
    
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    @Qualifier("systemConfigRedisTemplate")
    private RedisTemplate<String, SystemConfig> redisTemplate;
    
    public static void main(String[] args) {
    	String sysKey = Keys.systemConfigKey(ACC_TOKEN);
    	System.out.println(sysKey);
	}
    
    /**
     * 获取AccessToken
     * 大于等于1小时50分，更新token
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void refreshAccessTokenJob() {
        if(!Constants.MAIN_SERVER){
            return;
        }
        logger.error("--------------------refresh token[B]-------------------");
        String sysKey = Keys.systemConfigKey(ACC_TOKEN);
        boolean updateFlag = checkTokenValidate(sysKey);
        if (updateFlag) {
			logger.info("will update AccessToken .");
			AccessToken at = WeixinUtil.getAccessToken();
			if (at == null) {
				logger.error("获取token失败----------------------------------------------！！！！！！！！！！！");
	            return;
	        }
			SystemConfig config = null;
			try {
				config = new SystemConfig(ACC_TOKEN, JacksonJsonUtil.beanToJson(at));
				config.setCreateDate(System.currentTimeMillis());
				redisTemplate.opsForValue().set(sysKey, config);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			
		}
        logger.error("--------------------refresh token[E]-------------------");
    }

    /**
     * 获取jstoken
     * 大于等于1小时50分，更新token
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void refreshJsTicketJob() {
        if(!Constants.MAIN_SERVER){
            return;
        }
        logger.error("--------------------refresh ticket[B]-------------------");
        String sysKey = Keys.systemConfigKey(JS_TOKEN);
        boolean updateFlag = checkTokenValidate(sysKey);
        if (updateFlag) {
        	 String jsToken = WeixinUtil.getRefreshJsTicket(systemConfigService.queryWXAToken(""));
             if (StringUtils.isEmpty(jsToken)) {
                 logger.error("获取ticket失败----------------------------------------------！！！！！！！！！！！");
                 return;
             }
             SystemConfig config = new SystemConfig(JS_TOKEN, jsToken);
             config.setCreateDate(System.currentTimeMillis());
             redisTemplate.opsForValue().set(sysKey, config);
		}
        logger.error("--------------------refresh ticket[E]-------------------");
    }
    
    /**
     * check token的时效
     * @param sysKey
     * @return
     */
    private boolean checkTokenValidate(String sysKey) {
    	
    	boolean updateFlag = false;
    	SystemConfig systemConfig = redisTemplate.opsForValue().get(sysKey);
        if (systemConfig == null) {
			updateFlag = true;
		}else {
			Long createDate = systemConfig.getCreateDate();
			if (createDate == null) {
				createDate = 0l;
			}
			long currDate = System.currentTimeMillis();
			if ((currDate - createDate) >= ((60+50)*60*1000)) {	//大于等于1小时50分，更新token
				updateFlag = true;
			}
		}
        return updateFlag;
    	
    }
    
}

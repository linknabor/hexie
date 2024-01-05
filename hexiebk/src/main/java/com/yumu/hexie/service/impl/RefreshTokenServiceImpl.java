/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.integration.wechat.entity.MiniAccessToken;
import com.yumu.hexie.integration.wechat.service.MiniprogramAuthService;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.system.SystemConfig;
import com.yumu.hexie.model.system.SystemConfigRepository;
import com.yumu.hexie.service.RefreshTokenService;
import com.yumu.hexie.service.common.WechatCoreService;

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

	
    private static final Logger logger = LoggerFactory.getLogger("com.yumu.hexie.schedule");
    
    @Autowired
    private WechatCoreService wechatCoreService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
	private SystemConfigRepository systemConfigRepository;
    
    /**
     * 获取小程序AccessToken
     * 大于等于1小时50分，更新token
     */
    @Scheduled(cron = "0 0/10 * * * ?")
	@Override
	public void refreshMiniAccessTokenJob() {
    	
    	List<SystemConfig> configs = systemConfigRepository.findAll();
    	if (configs != null && configs.size() > 0) {
    		for (SystemConfig systemConfig : configs) {
    			String sysKey = systemConfig.getSysKey();
    			if (sysKey.indexOf(MiniprogramAuthService.MINI_APP_KEY_PREFIX) > -1) {
					String miniAppid = sysKey.substring(sysKey.lastIndexOf("_")+1, sysKey.length());
					refreshMiniTokenByMiniAppid(miniAppid);
				}
			}
    		
		}

	}
    
    /**
     * 更新小程序AccessToken
     * @param miniAppid
     */
	private void refreshMiniTokenByMiniAppid(String miniAppid) {
		logger.error("--------------------refresh mini token[B]-------------------");
        String sysKey = ModelConstant.KEY_MINI_ACCESS_TOKEN + miniAppid;
        Long expire = stringRedisTemplate.opsForValue().getOperations().getExpire(sysKey);	//返回-1表示永久，返回-2表示没有值
        logger.info("miniappid " + miniAppid + ", expire :" + expire);
        boolean updateFlag = false;
        if (expire <= 60*10l) {	//小于等于10分钟的，重新获取token
        	updateFlag = true;
		}
        if (updateFlag) {
			logger.info("will update miniprogram accessToken. miniappid : " + miniAppid);
			MiniAccessToken miniAccessToken = null;
			try {
				miniAccessToken = wechatCoreService.getMiniAccessToken(miniAppid);
				if (!StringUtils.isEmpty(miniAccessToken.getErrcode())) {
					logger.info("get miniAccessToken failed, errcode : " + miniAccessToken.getErrcode() + ", errormsg: " + miniAccessToken.getErrmsg());
					return;
				}
				stringRedisTemplate.opsForValue().set(sysKey, miniAccessToken.getAccess_token(), 7200, TimeUnit.SECONDS);
				
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			
		}
        logger.error("--------------------refresh mini token[E]-------------------");
	}
    
}

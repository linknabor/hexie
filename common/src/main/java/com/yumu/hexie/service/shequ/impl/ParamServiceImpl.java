package com.yumu.hexie.service.shequ.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wuye.WuyeParamService;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.shequ.ParamService;

@Service
public class ParamServiceImpl implements ParamService {
	
	private static Logger logger = LoggerFactory.getLogger(ParamServiceImpl.class);
	
	@Autowired
	private SystemConfigService systemConfigService;

	@Autowired
	private WuyeParamService wuyeParamService;
	
	/**
	 * 根据用户的cspId获取该用户的公司参数，如果缓存中没有参数，则发起请求获取参数
	 * @param user 用户信息，其中绑了房子的用户才有cspId
	 * @return
	 */
	@Override
	public Map<String, String> getWuyeParam(User user) {

		return checkAndGetParam(user);
	}

	/**
	 * 异步获取物业参数
	 * @param user
	 * @param type
	 * @return
	 */
	@Override
	@Async("taskExecutor")
	public Map<String, String> getWuyeParamAsync(User user, String type) {
		
		try {
			//先休息1分钟，因为平台重新加载参数需要时间
			Thread.sleep(60*1000);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		
		}
		if (ModelConstant.PARA_TYPE_CSP.equals(type)) {
			wuyeParamService.clearWuyeCache(user.getCspId(), type);
		}
		return checkAndGetParam(user);
	}

	@Override
	public void updateSysParam() {
		
		systemConfigService.reloadSysConfigCache();
	}

	private Map<String, String> checkAndGetParam(User user) {
		
		Map<String, String> paramMap = null;
		String cspId = user.getCspId();
		if (StringUtil.isEmpty(cspId) || "0".equals(cspId)) {
			//do nothing
		} else {
			paramMap = wuyeParamService.cacheWuyeParam(user, ModelConstant.PARA_TYPE_CSP);
		}
		if (paramMap == null) {
			paramMap = new HashMap<>();
		}
		return paramMap;
	}
	
	

}

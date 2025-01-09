package com.yumu.hexie.service.shequ.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wuye.WuyeParamService;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.common.impl.SystemConfigServiceImpl;
import com.yumu.hexie.service.shequ.ParamService;

@Service
public class ParamServiceImpl implements ParamService {
	
	private static Logger logger = LoggerFactory.getLogger(ParamServiceImpl.class);
	
	@Autowired
	private SystemConfigService systemConfigService;

	@Autowired
	private WuyeParamService wuyeParamService;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
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
		} else if (ModelConstant.PARA_TYPE_SECT.equals(type)) {
			wuyeParamService.clearWuyeCache(user.getSectId(), type);
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
		if (!StringUtil.isEmpty(cspId) && !"0".equals(cspId)) {
			paramMap = wuyeParamService.cacheWuyeParam(user, user.getCspId(), ModelConstant.PARA_TYPE_CSP);
		}
		String sectId = user.getSectId();
		if (!StringUtil.isEmpty(sectId) && !"0".equals(sectId)) {
			Map<String, String> sectMap = wuyeParamService.cacheWuyeParam(user, user.getSectId(), ModelConstant.PARA_TYPE_SECT);
			if(paramMap == null) {
				paramMap = sectMap;
			} else {
				paramMap.putAll(sectMap);
			}
		}
		if (paramMap == null) {
			paramMap = new HashMap<>();
		}
		return paramMap;
	}
	
	/**
	 * 是否开启维修服务（新版工单）
	 * @param user
	 * @return
	 */
	@Override
	public boolean repairServiceAvailable(User user) {
		
		if (StringUtil.isEmpty(user.getSectId()) || "0".equals(user.getSectId())) {
			return false;
		}
		String valueStr = getRepairService(user);
		
		if (StringUtils.isEmpty(valueStr)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 是否开启维修服务（新版工单）
	 * @param user
	 * @return
	 */
	@Override
	public String getRepairService(User user) {
		
		String sys = SystemConfigServiceImpl.getSysMap().get(user.getAppId());
		String location = "";
		if (!"_guizhou".equals(sys) && !"_hebei".equals(sys)) {
			location = "_sh";
		} else {
			location = sys;
		}
		String key = ModelConstant.KEY_WORKORDER_CFG + location + ":" + user.getSectId();
		return stringRedisTemplate.opsForValue().get(key);
	}

}

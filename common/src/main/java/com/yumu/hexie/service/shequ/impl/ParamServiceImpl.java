package com.yumu.hexie.service.shequ.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wuye.WuyeUtil2;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.vo.HexieConfig;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.shequ.ParamService;

@Service
public class ParamServiceImpl implements ParamService {
	
	private final static Logger logger = LoggerFactory.getLogger(ParamServiceImpl.class);
	public final static String PARAM_NAMES = "ONLINE_REPAIR,ONLINE_SUGGESTION,ONLINE_MESSAGE,CORONA_PREVENTION_MODE";
	
	@Autowired
	private SystemConfigService systemConfigService;
	@Autowired
	private WuyeUtil2 wuyeUtil2;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	/**
	 * 缓存物业公司参数到redis中，如果失败重新请求，总共请求3次
	 */
	@Override
	public void cacheWuyeParam(User user, String infoId, String type) {

		try {
			BaseResult<HexieConfig> baseResult = wuyeUtil2.queryServiceCfg(user, infoId, type, PARAM_NAMES);
			HexieConfig hexieConfig = baseResult.getData();
			if (hexieConfig == null) {
				logger.error("未查询到参数：" + PARAM_NAMES);
			}
			Map<String, String> paramMap = hexieConfig.getParamMap();
			String key = ModelConstant.KEY_WUYE_PARAM_CFG + infoId;
			redisTemplate.opsForHash().putAll(key, paramMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}

	/**
	 * 根据用户的cspId获取该用户的公司参数，如果缓存中没有参数，则发起请求获取参数
	 * @param user 用户信息，其中绑了房子的用户才有cspId
	 * @return
	 */
	@Override
	public Map<Object, Object> getWuyeParamByUser(User user) {

		String cspId = user.getCspId();
		if (StringUtil.isEmpty(cspId) || "0".equals(cspId)) {
			return new HashMap<Object, Object>();
		}
		String key = ModelConstant.KEY_WUYE_PARAM_CFG + cspId;
		Map<Object, Object> paramMap = redisTemplate.opsForHash().entries(key);
		if (paramMap == null || paramMap.isEmpty()) {
			cacheWuyeParam(user, cspId, ModelConstant.PARA_TYPE_CSP);
			paramMap = redisTemplate.opsForHash().entries(key);
		}
		return paramMap;
	}
	
	@Override
	public void updateSysParam() {
		
		systemConfigService.reloadSysConfigCache();
	}
	

}

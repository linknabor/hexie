package com.yumu.hexie.service.shequ.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wuye.WuyeUtil;
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
	
	public static Map<String, Map<String, String>> cachedMap = new HashMap<>();
	
	@Autowired
	private SystemConfigService systemConfigService;
	
	/**
	 * 缓存物业公司参数到redis中，如果失败重新请求，总共请求3次
	 */
	@Override
	public void cacheWuyeParam(User user, String infoId, String type) {

		try {
			BaseResult<HexieConfig> baseResult = WuyeUtil.queryServiceCfg(user, infoId, type, PARAM_NAMES);//远程访问，可能会超时，不用管
			HexieConfig hexieConfig = baseResult.getData();
			if (hexieConfig == null) {
				logger.error("未查询到参数：" + PARAM_NAMES);
			}
			Map<String, String> paramMap = hexieConfig.getParamMap();
			cachedMap.put(infoId, paramMap);
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
	public Map<String, String> getWuyeParamByUser(User user) {

		String cspId = user.getCspId();
		if (StringUtil.isEmpty(cspId) || "0".equals(cspId)) {
			return new HashMap<String, String>();
		}
		Map<String, String> paramMap = cachedMap.get(cspId);
		if (paramMap == null) {
			cacheWuyeParam(user, cspId, ModelConstant.PARA_TYPE_CSP);
			paramMap = cachedMap.get(cspId);
		}
		return paramMap;
	}
	
	@Override
	public void updateSysParam() {
		
		systemConfigService.reloadSysConfigCache();
	}
	

}

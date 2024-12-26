package com.yumu.hexie.integration.wuye;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.vo.HexieConfig;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;

@Service
public class WuyeParamService {
	
	private final static Logger logger = LoggerFactory.getLogger(WuyeParamService.class);
	public final static String PARAM_CSP_NAMES = "ONLINE_REPAIR,ONLINE_SUGGESTION,ONLINE_MESSAGE,CORONA_PREVENTION_MODE";
	public final static String PARAM_SECT_NAMES = "ALLOW_RENOVATION_REGISTER";
	
	@Autowired
	private WuyeUtil2 wuyeUtil2;
	
	/**
	 * 缓存物业公司参数到redis中，如果失败重新请求，总共请求3次
	 */
	@Cacheable(cacheNames = ModelConstant.KEY_WUYE_PARAM_CFG, key = "#infoId", unless = "#result == null")
	public Map<String, String> cacheWuyeParam(User user, String infoId, String type) {

		try {
			String param = PARAM_CSP_NAMES;
			if(ModelConstant.PARA_TYPE_SECT.equals(type)) {
				param = PARAM_SECT_NAMES;
			}
			BaseResult<HexieConfig> baseResult = wuyeUtil2.queryServiceCfg(user, type, param);
			HexieConfig hexieConfig = baseResult.getData();
			if (hexieConfig == null ) {
				logger.error("未查询到参数：" + param);
				return null;
			}
			if (hexieConfig.getParamMap().isEmpty()) {
				logger.error("未查询到参数：" + param);
				return null;
			}
			return hexieConfig.getParamMap();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
		
	}
	
	@CacheEvict(cacheNames = ModelConstant.KEY_WUYE_PARAM_CFG, key = "#infoId")
	public void clearWuyeCache(String infoId, String type) {
		
	}
}

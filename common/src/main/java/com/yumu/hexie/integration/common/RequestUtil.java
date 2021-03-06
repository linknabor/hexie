package com.yumu.hexie.integration.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.yumu.hexie.model.region.RegionUrl;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.impl.SystemConfigServiceImpl;
import com.yumu.hexie.service.shequ.LocationService;
import com.yumu.hexie.service.shequ.impl.LocationServiceImpl;

@Component
public class RequestUtil {
	
	private Logger logger = LoggerFactory.getLogger(RequestUtil.class);

	@Value("${requestUrl}")
	private String requestUrl;
	
	@Autowired
	private LocationService locationService;
	
	/**
	 * 获取需要请求的服务器地址
	 * 给wuyeUtil2用的，以后都调用这个
	 * @param user
	 * @param regionName
	 * @return
	 */
	public String getRequestUrl(User user, String regionName) {
	
		logger.info("requestUrl : " + requestUrl);
		
		//1.先从用户的自动定位取
		String targetUrl = "";
		if (!StringUtils.isEmpty(regionName)) {
			RegionUrl regionurl = locationService.getRegionUrlByName(regionName);
			if (regionurl == null) {
				logger.info("regionName : " + regionName + " 未能找到相应的配置链接。");
			}else {
				targetUrl = regionurl.getRegionUrl();
			}
			
		}
		//2.如果自动定位的地区在区域配置表中没有，则根据用户所属的公众号 取配置文件中默认的请求地址
		if (StringUtils.isEmpty(targetUrl)) {
			//TODO 下面2个静态引用以后改注入形式
			String userSysCode = SystemConfigServiceImpl.getSysMap().get(user.getAppId());	//获取用户所属的公众号
			RegionUrl regionUrl = LocationServiceImpl.getCodeUrlMap().get(userSysCode);	//根据公众号 获取请求地址
			logger.info("user : " + user.getId() + ", userSysCode : " + userSysCode + ", regionUrl : " + regionUrl);
			targetUrl = requestUrl;
			if (regionUrl!=null) {
				String urlLink = regionUrl.getRegionUrl();
				if (!StringUtils.isEmpty(urlLink)) {
					targetUrl = urlLink;
				}
			}
		}
		
		logger.info("targetUrl : " + targetUrl);
		
		return targetUrl;
		
	}

}

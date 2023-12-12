package com.yumu.hexie.common.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class RequestUtil {
	
	private static Logger logger = LoggerFactory.getLogger(RequestUtil.class);
	
	public static String getRealIp(HttpServletRequest request) {
		
		String ipAddress = "";
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		logger.info("X-Forwarded-For : " + xForwardedFor);
		if (!StringUtils.isEmpty(xForwardedFor)) {
			String[]ipArr = xForwardedFor.split(",");
			ipAddress = ipArr[ipArr.length-1];
		}
		if (StringUtils.isEmpty(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			logger.info("remote addr : " + ipAddress);
		}
		return ipAddress;
	}

}

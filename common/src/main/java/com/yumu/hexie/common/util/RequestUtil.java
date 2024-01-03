package com.yumu.hexie.common.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestUtil {
	
	private static Logger logger = LoggerFactory.getLogger(RequestUtil.class);
	
	public static String getRealIp(HttpServletRequest request) {
		
		String ip = request.getHeader("x-forwarded-for");
		logger.info("x-forwarded-for : " + ip);
		if (!StringUtil.isEmpty(ip)) {
			String[]ipArr = ip.split(",");
			ip = ipArr[0];
		}
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
            logger.info("Proxy-Client-IP : " + ip);
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP"); 
            logger.info("WL-Proxy-Client-IP : " + ip);
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
            logger.info("HTTP_CLIENT_IP : " + ip);
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
            logger.info("HTTP_X_FORWARDED_FOR : " + ip);
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();
            logger.info("remoteAddr : " + ip);
        }
        return ip;
		
	}

}

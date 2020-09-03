/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.yumu.hexie.common.util.MD5Util;
import com.yumu.hexie.common.util.RandomStringUtils;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: SimpleCORSFilter.java, v 0.1 2016年5月27日 上午11:52:57  Exp $
 */
@WebFilter(urlPatterns = "/*", filterName = "simpleCORSFilter")
public class SimpleCORSFilter implements Filter {
	
	private static Logger logger = LoggerFactory.getLogger(SimpleCORSFilter.class);
	
	@Value("${testMode}")
	private Boolean testMode;
	
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        
    	HttpServletRequest request = (HttpServletRequest) req;
    	HttpServletResponse response = (HttpServletResponse) res;
        
        String requestUrl = request.getRequestURL().toString();
        logger.info("SimpleCORSFilter, requestUrl " + requestUrl);
        
        if (requestUrl.indexOf("/getInvoice") == -1) {	//发票的验证码添入额外的token，防止恶意刷验证码,其他的请求随意放一个token不做处理
			String random = RandomStringUtils.random(5);
			String token = MD5Util.MD5Encode(random, "");
			response.addHeader("Access-Control-Allow-Token", token);
		}
        if (testMode) {
        	response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1");
		}
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, Access-Control-Allow-Token");
        response.setHeader("Access-Control-Max-Age", "3600");
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}

}

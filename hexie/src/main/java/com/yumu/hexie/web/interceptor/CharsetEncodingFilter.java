package com.yumu.hexie.web.interceptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.springframework.core.annotation.Order;

//@WebFilter(urlPatterns = "/*", filterName = "charsetEncodingFilter" )
//@Order(1)
public class CharsetEncodingFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
        request.setCharacterEncoding("UTF-8");
        chain.doFilter(request , response);
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	
	
}

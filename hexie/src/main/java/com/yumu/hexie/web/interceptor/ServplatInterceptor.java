package com.yumu.hexie.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.web.wrapper.ServplatRequestWrapper;

/**
 * 拦截servplat请求过来的请求
 * @author david
 *
 */
public class ServplatInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String uri = request.getRequestURI();
		if (uri.contains("servplat")) {
			ServplatRequestWrapper myRequestWrapper = new ServplatRequestWrapper(request);
	        String body = myRequestWrapper.getBody();
	        ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
	        BaseRequestDTO <?> baseRequestDTO = objectMapper.readValue(body, BaseRequestDTO.class);
	        String sign = baseRequestDTO.getSign();
	        if ("1111111".equals(sign)) {
				
			}
	        //TODO 验证签名
		}
        return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}

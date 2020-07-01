package com.yumu.hexie.web.customservice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 自定义服务监控
 * @author david
 *
 */
@Component
@Aspect
public class CustomServiceMonitor {

	private static Logger logger = LoggerFactory.getLogger(CustomServiceMonitor.class);
	
	@Around("execution(* com.yumu.hexie.web.customservice.CustomServiceController.*(..))")
	public Object logServiceMethodAccess(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		Object object = joinPoint.proceed();
		long end = System.currentTimeMillis();
		long t = end - start;
		String tmp = joinPoint.getSignature().toString();
		logger.info(String.format("class:%s,invoke_time:%s",tmp,t));
		return object;
	}

	
}

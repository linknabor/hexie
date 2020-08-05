package com.yumu.hexie.web;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义服务监控
 * @author david
 *
 */
//@Component
//@Aspect
public class ControllerMonitor {

	private static Logger logger = LoggerFactory.getLogger(ControllerMonitor.class);
	
	@Around("execution(* com.yumu.hexie.web.*.*.*(..))")
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

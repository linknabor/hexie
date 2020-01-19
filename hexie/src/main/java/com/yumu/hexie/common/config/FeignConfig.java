package com.yumu.hexie.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;

@Configuration
public class FeignConfig {

	@Bean
	Logger.Level feignLoggerLevel() {
		// 这里记录所有，根据实际情况选择合适的日志level
		return Logger.Level.FULL;
	}
}

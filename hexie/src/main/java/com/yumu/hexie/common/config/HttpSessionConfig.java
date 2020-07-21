package com.yumu.hexie.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 360000)
public class HttpSessionConfig {

	@Bean
	public HttpSessionStrategy httpSessionStrategy() {
		// return new HeaderHttpSessionStrategy();
		CookieHttpSessionStrategy c = new CookieHttpSessionStrategy();
		return c;
	}
}

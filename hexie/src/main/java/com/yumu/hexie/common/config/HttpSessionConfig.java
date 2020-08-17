package com.yumu.hexie.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 360000)
public class HttpSessionConfig {
	
	@Configuration
	public class SpringSessionConfig {
		@Bean
		public CookieSerializer httpSessionIdResolver() {
			DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
			cookieSerializer.setUseHttpOnlyCookie(false);
			cookieSerializer.setSameSite("None");
			cookieSerializer.setCookiePath("/");
			cookieSerializer.setUseSecureCookie(true);
			return cookieSerializer;
		}
	}


}

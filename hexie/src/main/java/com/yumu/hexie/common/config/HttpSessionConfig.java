package com.yumu.hexie.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

//10天过期
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 24*3600*10)	
public class HttpSessionConfig {
	
	@Value("${testMode}")
	private Boolean testMode;
	
	/**
	 * 应对chrome浏览器67及以上版本 set-cookie新属性SameSite=Strict或者SameSite=Lax时，cookie不能跨域保存的问题
	 *
	 */
	@Configuration
	public class SpringSessionConfig {
		@Bean
		public CookieSerializer httpSessionIdResolver() {
			DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
			if (testMode) {
				cookieSerializer.setUseHttpOnlyCookie(false);
				cookieSerializer.setSameSite("None");
				cookieSerializer.setCookiePath("/");
				cookieSerializer.setUseSecureCookie(false);
			}
			return cookieSerializer;
		}
	}


}

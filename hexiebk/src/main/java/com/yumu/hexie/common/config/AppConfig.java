package com.yumu.hexie.common.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@Configuration
@PropertySource(value = {"classpath:wechat.properties", "classpath:alipay.properties"})
@ComponentScan(basePackages = {"com.yumu.hexie"}, includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"com.yumu.hexie.backend.web.*"}))
@EnableJpaRepositories({"com.yumu.hexie.model.*"})
@EntityScan(basePackages = {"com.yumu.hexie.model"})
@EnableScheduling
@EnableAspectJAutoProxy
@EnableCaching(proxyTargetClass=true)
@EnableAsync
public class AppConfig {
	
    public static void main(String[] args) {
        SpringApplication.run(AppConfig.class, args);
    }

    @Bean
    public ServletWebServerFactory EmbeddedServletContainerFactory(){
    	TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.setPort(8888);
        factory.addConnectorCustomizers(new AppTomcatConnectorCustomizer());
        return factory;
    }
    
    @Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("locale/messages");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}
    
}

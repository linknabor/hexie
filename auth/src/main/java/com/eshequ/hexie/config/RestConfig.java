package com.eshequ.hexie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {
	
	@Bean(name="restTemplate")
    public RestTemplate httpClientRestTemplate(){
		
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());	//以后改连接池
        return restTemplate;
    }
}

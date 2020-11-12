package com.yumu.hexie.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {
	
	private static final int REST_READ_TIME_OUT = 15000;
	private static final int REST_CONNECT_TIME_OUT = 10000;

	@Bean(name="restTemplate")
    public RestTemplate httpClientRestTemplate(){
		
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setReadTimeout(REST_READ_TIME_OUT);
        requestFactory.setConnectTimeout(REST_CONNECT_TIME_OUT);
        requestFactory.setConnectionRequestTimeout(REST_CONNECT_TIME_OUT);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }
	
}

package com.eshequ.hexie.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.eshequ.hexie.exception.BusinessException;

@Component
public class RestUtil {

	private static Logger logger = LoggerFactory.getLogger(RestUtil.class);
	
	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;

	public <T> T doPost(String reqUrl, Map<String, String> postData, Class<T> respClazz){
		
		ResponseEntity<T> resp = restTemplate.postForEntity(reqUrl, postData, respClazz);
		HttpStatus httpStatus = resp.getStatusCode();
		if (HttpStatus.OK.equals(httpStatus) ) {
			logger.info("response entity is : " + resp.getBody());
		}else {
			throw new BusinessException("request failed, code : " + httpStatus);
		}
		return resp.getBody();
	}
	
}

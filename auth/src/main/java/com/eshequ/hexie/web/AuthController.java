package com.eshequ.hexie.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.eshequ.hexie.common.WechatConfig;
import com.eshequ.hexie.service.AuthService;

@RestController
@RequestMapping(value = "/event")
public class AuthController {
	
	private Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@Autowired
	private AuthService authService;
	
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String hello() {
		return "hello world!";
	}
	
	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	public String authEvent(@RequestBody String requestXml) {
		
		logger.info("request is : " + requestXml);
		authService.authEventHandle(requestXml);
		return WechatConfig.SUCCESS;
	}
	
	@RequestMapping(value = "/common/*/", method = RequestMethod.POST)
	public String commonEnvent(@RequestBody String requestXml) {
		
		logger.info(requestXml);
		return "a";
	}
	

}

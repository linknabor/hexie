package com.yumu.hexie.web.event;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yumu.hexie.common.Constants;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.web.BaseController;

@RestController
@RequestMapping(value = "/yanji/event")
public class EventHandlerController extends BaseController{
	
	@Value("${yanji.appid:wxb2e5b64025a1a5f8}")
	private String yanjiAppid;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(EventHandlerController.class);
	
	
	@RequestMapping(value = "/subscribe", method = RequestMethod.POST)
	public String subscribe(@RequestBody String requestJson) throws JsonProcessingException {
		log.info("subscribe event : " + requestJson);
		stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_EVENT_SCAN_SUBSCRIBE_QUEUE, requestJson);
		return JacksonJsonUtil.getMapperInstance(false).writeValueAsString(Constants.SERVICE_SUCCESS);
	}
	
	@RequestMapping(value = "/updateToken", method = RequestMethod.POST)
	public String updateToken(@RequestBody String authorizerAccessToken) {
		if (!StringUtils.isEmpty(authorizerAccessToken)) {
			log.info("updateToken event, token. ");
			String authTokenKey = ConstantWeChat.KEY_AUTHORIZER_ACCESS_TOKEN + yanjiAppid;
			stringRedisTemplate.opsForValue().set(authTokenKey, authorizerAccessToken);	//给合协公众号设置授权了的AccessToken
			return Constants.SERVICE_SUCCESS;
		}
		return "";
	}
	
}
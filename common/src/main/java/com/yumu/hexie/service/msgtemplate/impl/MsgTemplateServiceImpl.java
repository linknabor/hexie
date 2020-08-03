package com.yumu.hexie.service.msgtemplate.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.msgtemplate.MsgTempalateRepository;
import com.yumu.hexie.model.msgtemplate.MsgTemplate;
import com.yumu.hexie.model.msgtemplate.MsgTemplateUrl;
import com.yumu.hexie.model.msgtemplate.MsgTemplateUrlRepository;
import com.yumu.hexie.service.msgtemplate.MsgTemplateService;

@Service
public class MsgTemplateServiceImpl implements MsgTemplateService {
	
	private static final Logger logger = LoggerFactory.getLogger(MsgTemplateServiceImpl.class);
	
	@Autowired
	private MsgTempalateRepository msgTempalateRepository;
	@Autowired
	private MsgTemplateUrlRepository msgUrlRepository;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	
	@PostConstruct
	public void loadCache() {
		
		if (ConstantWeChat.isMainServer()) {	//BK程序不跑下面的队列轮询
			return;
		}
		try {
			loadTemplateFromDatabase();
			loadMsgUrlFromDatabase();
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
	}

	private void loadTemplateFromDatabase() {
		
		List<MsgTemplate> templateList = msgTempalateRepository.findByStatus(1);	//1正常状态的模板
		for (MsgTemplate msgTemplate : templateList) {
			//key的形式：msgtemplate:xxxxx_appid
			String key = ModelConstant.KEY_MSG_TEMPLATE + msgTemplate.getName() + "_" + msgTemplate.getAppid();
			String value = msgTemplate.getValue();
			redisTemplate.opsForValue().set(key, value);
		}
	}
	
	private void loadMsgUrlFromDatabase() {
		
		List<MsgTemplateUrl> msgUrlList = msgUrlRepository.findByStatus(1);	//1正常状态的页面链接配置
		for (MsgTemplateUrl msgUrl : msgUrlList) {
			//key的形式：msgtemplate:xxxxx_appid
			String key = ModelConstant.KEY_MSG_TEMPLATE_URL + msgUrl.getName();
			String value = msgUrl.getValue();
			redisTemplate.opsForValue().set(key, value);
		}
	}

	/**
	 * 从缓存中获取配置的模板消息
	 * @param name
	 * @param appid
	 * @return
	 */
	@Override
	public String getTemplateFromCache(String name, String appid) {
		
		String key = ModelConstant.KEY_MSG_TEMPLATE + name + "_" + appid;
		return redisTemplate.opsForValue().get(key); 
	}
	
	/**
	 * 从缓存中获取配置的消息跳转链接
	 * @param name
	 * @param appid
	 * @return
	 */
	@Override
	public String getMsgUrlFromCache(String name) {
		
		String key = ModelConstant.KEY_MSG_TEMPLATE_URL + name;
		return redisTemplate.opsForValue().get(key); 
	}
	
	/**
	 * 刷新模板缓存
	 */
	@Override
	public void rerefshCache() {
		
		Set<String> keySet = redisTemplate.keys(ModelConstant.KEY_MSG_TEMPLATE);
		redisTemplate.delete(keySet);
		loadTemplateFromDatabase();
		
		Set<String> urlKeySet = redisTemplate.keys(ModelConstant.KEY_MSG_TEMPLATE_URL);
		redisTemplate.delete(urlKeySet);
		loadMsgUrlFromDatabase();
		
	}
	
}

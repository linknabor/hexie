package com.yumu.hexie.service.msgtemplate.impl;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.msgtemplate.MsgTempalateRepository;
import com.yumu.hexie.model.msgtemplate.MsgTemplate;
import com.yumu.hexie.model.msgtemplate.MsgTemplateUrl;
import com.yumu.hexie.model.msgtemplate.MsgTemplateUrlRepository;
import com.yumu.hexie.service.msgtemplate.AliMsgService;

@Service
public class AliMsgServiceImpl implements AliMsgService {

	@Resource
	private MsgTemplateUrlRepository msgUrlRepository;
	
	@Resource
	private MsgTempalateRepository msgTempalateRepository;
	
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_MSG_TEMPLATE, key = "#templateName+'_'+#appId", unless = "#result == null ")
	public MsgTemplate getTemplateByNameAndAppId(String templateName, String appId) {
		
		Assert.hasText(templateName, "模板消息名称不能为空。");
    	return msgTempalateRepository.findByNameAndAppidAndStatus(templateName, appId, 1);
	}
	
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_MSG_TEMPLATE_URL, key = "#urlName")
    public String getMsgUrl(String urlName) {
    	
    	Assert.hasText(urlName, "消息跳转链接不能为空。");
    	MsgTemplateUrl msgTemplateUrl = msgUrlRepository.findByNameAndStatus(urlName, 1);
    	String url = "";
    	if (msgTemplateUrl != null) {
    		url = msgTemplateUrl.getValue();
		}
    	return url;
    	
    }

}

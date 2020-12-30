package com.yumu.hexie.service.msgtemplate.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.msgtemplate.MsgTempalateRepository;
import com.yumu.hexie.model.msgtemplate.MsgTemplate;
import com.yumu.hexie.model.msgtemplate.MsgTemplateUrl;
import com.yumu.hexie.model.msgtemplate.MsgTemplateUrlRepository;
import com.yumu.hexie.service.msgtemplate.WechatMsgService;

@Service
public class WechatMsgServiceImpl implements WechatMsgService {
	
	@Autowired
	private MsgTempalateRepository msgTempalateRepository;
	@Autowired
	private MsgTemplateUrlRepository msgUrlRepository;
	

	/**
     * 不同公众号用不同模板消息
     */
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_MSG_TEMPLATE, key = "#templateName+'_'+#appId")
    public String getTemplateByNameAndAppId(String templateName, String appId) {
    	
    	Assert.hasText(templateName, "模板消息名称不能为空。");
    	
    	if (StringUtils.isEmpty(appId)) {
			appId = ConstantWeChat.APPID;
		}
    	String templateId = "";
    	MsgTemplate msgTemplate = msgTempalateRepository.findByNameAndAppidAndStatus(templateName, appId, 1);
    	if (msgTemplate != null) {
    		templateId = msgTemplate.getValue();
		}
    	return templateId;
    	
    }
    
    /**
     * 不同公众号用不同模板消息
     */
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
	
	/**
	 * 刷新模板缓存
	 */
	@Override
	@CacheEvict(cacheNames = {ModelConstant.KEY_MSG_TEMPLATE, ModelConstant.KEY_MSG_TEMPLATE_URL}, allEntries = true)
	public void refreshCache() {
		
	}
	
}

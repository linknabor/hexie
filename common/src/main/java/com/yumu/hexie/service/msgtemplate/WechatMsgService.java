package com.yumu.hexie.service.msgtemplate;

public interface WechatMsgService {

	String getTemplateByNameAndAppId(String templateName, String appId);

	String getMsgUrl(String urlName);
	
	void refreshCache();


}

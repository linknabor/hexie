package com.yumu.hexie.service.msgtemplate;

public interface MsgTemplateService {

	String getTemplateFromCache(String name, String appid);

	String getMsgUrlFromCache(String name);
	
	void refreshCache();

}

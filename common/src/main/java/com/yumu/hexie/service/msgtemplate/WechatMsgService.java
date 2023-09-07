package com.yumu.hexie.service.msgtemplate;

import java.util.List;

import com.yumu.hexie.model.msgtemplate.MsgTemplate;

public interface WechatMsgService {

	String getTemplateByNameAndAppId(String templateName, String appId);
	
	MsgTemplate getTemplateByNameAndAppIdV2(String templateName, String appId);

	String getMsgUrl(String urlName);
	
	void refreshCache();

	MsgTemplate getTemplateByTemplateId(String templateId);

	List<MsgTemplate> getSubscribeMsgTemplate(String appId, int type, int bizType);

	
}

package com.yumu.hexie.service.msgtemplate;

import com.yumu.hexie.model.msgtemplate.MsgTemplate;

public interface AliMsgService {

	MsgTemplate getTemplateByNameAndAppId(String templateName, String appId);
	
	String getMsgUrl(String urlName);
}

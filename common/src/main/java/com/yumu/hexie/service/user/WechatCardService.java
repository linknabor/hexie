package com.yumu.hexie.service.user;

import com.yumu.hexie.integration.wechat.entity.card.PreActivateReq;
import com.yumu.hexie.model.user.WechatCardCatagory;

public interface WechatCardService {
	
	public WechatCardCatagory getWechatCardCatagory(int cardType, String appId);
	
	public void preActivate(PreActivateReq preActivateReq);
}

package com.yumu.hexie.service.user;

import com.yumu.hexie.integration.wechat.entity.card.PreActivateReq;
import com.yumu.hexie.integration.wechat.vo.SubscribeVO;
import com.yumu.hexie.model.user.WechatCardCatagory;

public interface WechatCardService {
	
	WechatCardCatagory getWechatCardCatagoryByCardTypeAndAppId(int cardType, String appId);
	
	WechatCardCatagory getWechatCardCatagoryByCardTypeAndCardId(int cardType, String cardId);
	
	void acctivate(PreActivateReq preActivateReq);

	void subscribeEvent(SubscribeVO subscribeVO);

	

	
}

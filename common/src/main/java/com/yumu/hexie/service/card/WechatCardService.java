package com.yumu.hexie.service.card;

import com.yumu.hexie.integration.wechat.entity.card.PreActivateReq;
import com.yumu.hexie.model.card.WechatCardCatagory;
import com.yumu.hexie.model.card.dto.EventGetCardDTO;
import com.yumu.hexie.model.card.dto.EventSubscribeDTO;

public interface WechatCardService {
	
	WechatCardCatagory getWechatCardCatagoryByCardTypeAndAppId(int cardType, String appId);
	
	WechatCardCatagory getWechatCardCatagoryByCardId(String cardId);
	
	void acctivate(PreActivateReq preActivateReq);

	void eventSubscribe(EventSubscribeDTO eventSubscribeDTO);
	
	void eventGetCard(EventGetCardDTO eventGetCardDTO);

	

	
}

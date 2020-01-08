package com.yumu.hexie.service.card;

import com.yumu.hexie.integration.wechat.entity.card.PreActivateReq;
import com.yumu.hexie.integration.wuye.vo.RefundDTO;
import com.yumu.hexie.model.card.WechatCard;
import com.yumu.hexie.model.card.WechatCardCatagory;
import com.yumu.hexie.model.card.dto.EventGetCardDTO;
import com.yumu.hexie.model.card.dto.EventSubscribeDTO;
import com.yumu.hexie.model.card.dto.EventUpdateCardDTO;
import com.yumu.hexie.model.user.User;

public interface WechatCardService {
	
	WechatCardCatagory getWechatCardCatagoryByCardTypeAndAppId(int cardType, String appId);
	
	WechatCardCatagory getWechatCardCatagoryByCardId(String cardId);
	
	User activate(PreActivateReq preActivateReq);

	void eventSubscribe(EventSubscribeDTO eventSubscribeDTO);
	
	void eventGetCard(EventGetCardDTO eventGetCardDTO);
	
	void eventUpdateCard(EventUpdateCardDTO eventUpdateCardDTO);

	String getActivateUrlOnPage(User user);

	WechatCard getWechatMemberCard(String openid);

	void wuyeRefund(RefundDTO refundDTO);
	
}

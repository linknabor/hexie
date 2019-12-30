package com.yumu.hexie.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WechatCardCatagoryRepository extends JpaRepository<WechatCardCatagory, Long> {

	public WechatCardCatagory findByCardTypeAndAppId(int cardType, String appId);
	
	public WechatCardCatagory findByCardTypeAndCardId(int cardType, String cardId);
}

package com.yumu.hexie.model.card;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WechatCardRepository extends JpaRepository<WechatCard, Long> {

	public WechatCard findByCardIdAndUserOpenId(String cardId, String userOpenId);
	
}

package com.yumu.hexie.model.card;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface WechatCardRepository extends JpaRepository<WechatCard, Long> {

	public WechatCard findByCardIdAndUserOpenId(String cardId, String userOpenId);
	
	public WechatCard findByCardTypeAndUserOpenId(int cardType, String userOpenId);
	
	@Modifying
	@Transactional
	@Query(value = "update wechatcard set point = point + ?1 where id = ?2 and point = ?3 ", nativeQuery = true)
	public int updateCardPointByUserId(long userId, int addPoint, int oriPoint);
}

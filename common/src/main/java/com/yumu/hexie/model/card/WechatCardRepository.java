package com.yumu.hexie.model.card;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface WechatCardRepository extends JpaRepository<WechatCard, Long> {

	public WechatCard findByCardIdAndUserOpenId(String cardId, String userOpenId);
	
	public WechatCard findByCardTypeAndUserOpenId(int cardType, String userOpenId);
	
	/**
	 * 增量更新
	 * @param addPoint
	 * @param cardCode
	 * @param oriPoint
	 * @return
	 */
	@Modifying
	@Transactional
	@Query(value = "update wechatcard set bonus = bonus + ?1 where cardCode = ?2 and bonus = ?3 ", nativeQuery = true)
	public int updateCardByCardCodeIncremently(int addPoint, String cardCode, int oriPoint);
	
	@Modifying
	@Transactional
	@Query(value = "update wechatcard set userId = ?1, userName = ?2 where id = ?3 ", nativeQuery = true)
	public int updateCardUserInfo(long userId, String userName, long id);
	
	@Modifying
	@Transactional
	@Query(value = "update wechatcard set status = ?1 where id = ?2 ", nativeQuery = true)
	public int updateCardStatus(int status, long id);
	
	public WechatCard findByCardCode(String cardCode);
}

package com.yumu.hexie.model.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {

	public List<BankCard> findByUserIdAndQuickTokenIsNotNull(long userId);
	
	public BankCard findByAcctNo(String acctNo);	//卡号具有唯一性
	
	public BankCard findByAcctNoAndQuickTokenIsNull(String acctNo);	//卡号具有唯一性
	
	
	@Modifying
	@Transactional
	@Query(value = "update bankcard set quickToken = ?1 where acctNo = ?2 and userId = ?3 ", nativeQuery = true)
	public int updateBankCardByAcctNoAndUserId(String quickToken, String acctNo, long userId);
	
}

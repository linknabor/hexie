package com.yumu.hexie.model.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {

	public List<BankCard> findByUserId(long userId);
	
	public BankCard findByAcctNo(String acctNo);	//卡号具有唯一性
	
	public BankCard findByAcctNoAndQuickTokenIsNull(String acctNo);	//卡号具有唯一性
	
}

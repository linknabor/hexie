package com.yumu.hexie.model.market.saleplan;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface OnSaleRuleRepository extends JpaRepository<OnSaleRule, Long> {
	OnSaleRule findById(long id);

	List<OnSaleRule> findAllByProductId(long productId);
	
	@Transactional
	@Modifying
	@Query(value = "update onSaleRule set status = ?1 where id = ?2 ", nativeQuery = true)
	void updateStatus(int status, long id);
	
	@Query(value = "select * from onSaleRule where UNIX_TIMESTAMP(endDate) <= ?1 and status = ?2 ", nativeQuery = true)
	List<OnSaleRule> findTimeoutRules(long current, int status);
}

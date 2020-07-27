package com.yumu.hexie.model.market;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EvoucherRepository extends JpaRepository<Evoucher, Long> {

	
	Evoucher findByCode(String code);
	
	List<Evoucher> findByUserId(long userId);
	
	List<Evoucher> findByOrderId(long orderId);
	
	List<Evoucher> findByOrderIdAndStatus(long orderId, int status);
	
	@Query(value = "select e.* from evoucher e where productId > 0 and status > 0 "
			+ "and IF (?1!='', e.status = ?1, 1=1) "
			+ "and IF (?2!='', e.tel = ?2, 1=1) "
			+ "and IF (?3!='', e.agentName like CONCAT('%',?3,'%'), 1=1) "
			+ "order by e.id desc \n#pageable\n "
			, nativeQuery = true
			, countQuery = "select count(1) as counts from evoucher e where productId >0 and status >0 "
				+ "and IF (?1!='', e.status = ?1, 1=1) "
				+ "and IF (?2!='', e.tel = ?2, 1=1) "  
				+ "and IF (?3!='', e.agentName like CONCAT('%',?3,'%'), 1=1) " )
	Page<Evoucher> findByMultipleConditions(String status, String tel, String agentName, Pageable pageable);
	
}

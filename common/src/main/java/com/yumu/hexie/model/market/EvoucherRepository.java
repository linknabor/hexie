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
	
	final String column1 = "e.orderId, e.tel, e.consumeDate, e.productName, e.status, sum(e.actualPrice) as actualPrice, count(e.id) as counts";
	
	/**
	 * 如果同一批次购入的券要分开核销，则这个查询需要更上状态筛选的条件
	 * @param operatorId
	 * @return
	 */
	@Query(value = "select " + column1 + " from evoucher e "
			+ "where e.operatorUserId = ?1 group by e.orderId order by e.consumeDate desc ", 
			nativeQuery = true)
	List<Object[]> findByOperator(long operatorId);
	
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
	
	@Query(value = "select e.* from evoucher e where endDate < ?1 and status = ?2 ", nativeQuery = true)
	List<Evoucher> findTimeoutEvouchers(long current, int status);
	
}

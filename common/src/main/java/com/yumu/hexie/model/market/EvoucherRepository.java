package com.yumu.hexie.model.market;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yumu.hexie.model.ModelConstant;

public interface EvoucherRepository extends JpaRepository<Evoucher, Long> {

	
	Evoucher findByCode(String code);
	
	List<Evoucher> findByUserIdAndType(long userId, int type);
	
	List<Evoucher> findByUserIdAndTypeAndStatus(long userId, int type, int status);
	
	List<Evoucher> findByOrderId(long orderId);
	
	final String column1 = "e.orderId, e.tel, e.consumeDate, e.productName, e.status, sum(e.actualPrice) as actualPrice, count(e.id) as counts";
	
	/**
	 * 如果同一批次购入的券要分开核销，则这个查询需要更上状态筛选的条件
	 * @param operatorId
	 * @return
	 */
	@Query(value = "select " + column1 + " from evoucher e "
			+ "where e.operatorUserId = ?1 and e.type = ?2 group by e.orderId order by e.consumeDate desc ", 
			nativeQuery = true)
	List<Object[]> findByOperatorAndType(long operatorId, int type);
	
	List<Evoucher> findByOrderIdAndStatus(long orderId, int status);
	
	@Query(value = "select distinct e.* from evoucher e "
			+ "left join serviceOperatorItem s on e.productId = s.serviceId "
			+ "left join onSaleAreaItem o on e.productId = o.productId "
			+ "where e.productId > 0 and e.status > 0 "
			+ "and IF (?1!='', e.status = ?1, 1=1) "
			+ "and IF (?2!='', e.tel = ?2, 1=1) "
			+ "and IF (?3!='', e.agentNo = ?3, 1=1) "
			+ "and IF (?4!='', e.agentName like CONCAT('%',?4,'%'), 1=1) "
			+ "and IF (?5!='', e.type = ?5, 1=1) "
			+ "and IF (?6!='', s.operatorId = ?6, 1=1) "
			+ "and IF (?7 is not null, o.regionId in (?7), 1=1) "
			, nativeQuery = true
			, countQuery = "select count(distinct e.id) as counts from evoucher e "
				+ "left join serviceOperatorItem s on e.productId = s.serviceId "
				+ "left join onSaleAreaItem o on e.productId = o.productId "
				+"where productId >0 and status >0 "
				+ "and IF (?1!='', e.status = ?1, 1=1) "
				+ "and IF (?2!='', e.tel = ?2, 1=1) "
				+ "and IF (?3!='', e.agentNo = ?3, 1=1) "
				+ "and IF (?4!='', e.agentName like CONCAT('%',?4,'%'), 1=1) " 
				+ "and IF (?5!='', e.type = ?5, 1=1) "
				+ "and IF (?6!='', s.operatorId = ?6, 1=1) "
				+ "and IF (?7 is not null, o.regionId in (?7), 1=1) "
			)
	Page<Evoucher> findByMultipleConditions(String status, String tel, String agentNo, String agentName, String type, String userId, List<String> listSect, Pageable pageable);
	
	@Query(value = "select e.* from evoucher e where UNIX_TIMESTAMP(endDate) < ?1 and status = ?2 ", nativeQuery = true)
	List<Evoucher> findTimeoutEvouchers(long current, int status);
	
	@Query(value = "select e.* from evoucher e join onsalerule r on e.ruleId = r.id "
			+ "where e.status = ?1 "
			+ "and e.type = ?2 "
			+ "and e.agentNo = ?3 "
			+ "and r.status = " + ModelConstant.RULE_STATUS_ON, 
			nativeQuery = true)
	List<Evoucher> findByStatusAndTypeAndAgentNo(int status, int type, String agentNo);
	
}

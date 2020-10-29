package com.yumu.hexie.model.promotion.coupon;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CouponRuleRepository extends JpaRepository<CouponRule, Long> {
	@Query("from CouponRule cr where cr.seedId = ?1")
	public List<CouponRule> findBySeedId(long seedId);
	@Query("from CouponRule cr where cr.seedId = ?1 and cr.status = ?2 and cr.startDate<= ?3 and endDate >=?4 ")
	public List<CouponRule> findBySeedIdAndStatusDuration(long seedId, int status, Date now1,Date now2);
	
	public List<CouponRule> findBySeedIdAndStatus(long seedId,int status);
	
	
	String queryColumn = "r.id as ruleId, s.id as seedId, r.title, s.seedType, s.seedStr, r.itemType, r.status, r.supportType, "
			+ "r.totalCount, r.receivedCount, r.usedCount, r.amount, r.usageCondition, r.productId, r.uProductId, "
			+ "r.startDate, r.endDate, r.useStartDate, r.useEndDate, r.expiredDays, r.suggestUrl, s.seedImg, "
			+ "a.name as agentName, a.agentNo, a.id as agentId, r.couponDesc ";
	
	@Query(value = "select " + queryColumn + " from couponRule r "
			+ "join couponSeed s on r.seedId = s.id "
			+ "left join agent a on a.id = r.agentId "
			+ "where s.seedType <> 1 "	//分裂红包不显示，因为每分享一次，会产生一条新的规则，产生的规则部可人为编辑
			+ "and IF (?1!='', r.id = ?1, 1=1) "
			+ "and IF (?2!='', s.id = ?2, 1=1) "
			+ "and IF (?3!='', s.seedType = ?3, 1=1) "
			+ "and IF (?4!='', r.status = ?4, 1=1) "
			+ "and (COALESCE(?5) IS NULL OR (r.agentId IN (?5) )) "
			+ "and IF (?6!='', r.title like CONCAT('%',?6,'%'), 1=1) "
			, countQuery = "select count(1) from couponRule r  "
					+ "join couponSeed s on r.seedId = s.id "
					+ "where s.seedType <> 1 "	//分裂红包不显示，因为每分享一次，会产生一条新的规则，产生的规则部可人为编辑
					+ "and IF (?1!='', r.id = ?1, 1=1) "
					+ "and IF (?2!='', s.id = ?2, 1=1) "
					+ "and IF (?3!='', s.seedType = ?3, 1=1) "
					+ "and IF (?4!='', r.status = ?4, 1=1) "
					+ "and (COALESCE(?5) IS NULL OR (r.agentId IN (?5) )) "
					+ "and IF (?6!='', r.title like CONCAT('%',?6,'%'), 1=1) "
			, nativeQuery = true)
	public Page<Object[]> findByMultiCondition(String ruleId, String seedId, String seedType, String ruleStatus, 
			List<Long> agentIds, String title, Pageable pageable);
}

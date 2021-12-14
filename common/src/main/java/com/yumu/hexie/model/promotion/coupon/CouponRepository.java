package com.yumu.hexie.model.promotion.coupon;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yumu.hexie.model.ModelConstant;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
	Coupon findById(long id);
	List<Coupon> findByUserIdAndSeedId(long userId, long seedId);
	@Query("from Coupon c where c.seedId=?1 and c.empty=false order by c.id desc")
	List<Coupon> findBySeedIdOrderByIdDesc(long seedId, Pageable page);

	@Query("from Coupon c where c.userId=?1 and c.status in ?2 and c.empty=false order by c.id desc")
	List<Coupon> findByUserIdAndStatusIn(long userId, List<Integer> status, Pageable page);


	@Query("from Coupon c where c.status = "+ModelConstant.COUPON_STATUS_AVAILABLE+" and c.expiredDate<?1")
	List<Coupon> findTimeoutByPage(Date today, Pageable page);

	
	@Query("select count(c.id) from Coupon c where c.userId=?1 and c.status in ?2 and c.empty=false")
	int countByUserIdAndStatusIn(long userId, List<Integer> status);

	@Query("from Coupon c where c.ruleId=?1 and c.empty=false order by c.id desc")
	List<Coupon> findByRuleId(long ruleId);

	@Query("select count(c.id) from Coupon c where c.seedId=?1 and c.empty=false")
	int countBySeedId(long seedId);
	@Query("select count(c.id) from Coupon c where c.seedType=?1 and c.empty=false")
	int countBySeedType(int seedType);
	
	@Query("select count(c.id) from Coupon c where c.userId=?1 and c.seedType=?2 and c.empty=false")
	int countByUserAndSeedType(long userId, int seedType);
	
	@Query("from Coupon c where c.userId=?1 and c.status in ?2 and c.seedType=?3 and c.empty=false order by c.id desc")
	List<Coupon> findByStatusInAndSeedType(long userId, List<Integer>status, int seedType);
	
	@Query("from Coupon c where c.status = "+ModelConstant.COUPON_STATUS_AVAILABLE+" and c.expiredDate >=?1 and c.expiredDate<= ?2")
	List<Coupon> findTimeoutCouponByDate(Date fromDate, Date toDate, Pageable page);
	
	Coupon findByOrderId(long orderId);
	
	String queryCoumn = "id, title, ruleId, createDate, userId, tel, seedType, status, amount, useStartDate, expiredDate, usedDate, couponDesc, agentName, agentNo ";
	
	@Query(value = "select " + queryCoumn + "from coupon where status in ( ?1 ) "
			+ "and IF (?2!='', title like CONCAT('%',?2,'%'), 1=1) "
			+ "and IF (?3!='', seedType = ?3, 1=1) "
			+ "and IF (?4!='', tel = ?4, 1=1) "
			+ "and (COALESCE(?5) IS NULL OR (agentId IN (?5) )) "
			, countQuery = "select count(1) from coupon where status in ( ?1 ) "
					+ "and IF (?2!='', title like CONCAT('%',?2,'%'), 1=1) "
					+ "and IF (?3!='', seedType = ?3, 1=1) "
					+ "and IF (?4!='', tel = ?4, 1=1) "
					+ "and (COALESCE(?5) IS NULL OR (agentId IN (?5) )) "
			, nativeQuery = true)
	Page<Object[]> findByMultiCondition(List<Integer> status, String title, String seedType,
										String tel, List<Long> agentId, Pageable pageable);
}

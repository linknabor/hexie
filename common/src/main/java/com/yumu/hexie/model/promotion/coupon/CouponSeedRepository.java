package com.yumu.hexie.model.promotion.coupon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CouponSeedRepository extends JpaRepository<CouponSeed, Long> {
	public List<CouponSeed> findBySeedType(int seedType);
	public CouponSeed findBySeedStr(String seedStr);
	public CouponSeed findBySeedTypeAndBizId(int seedType,long bizId);
	public List<CouponSeed> findBySeedTypeAndStatusAndAppid(int seedType, int status, String appid);

	@Query(value = "select cs.* from couponseed cs join couponrule cr on cs.id = cr.seedId "
			+ "where cs.seedType in ( ?1 ) "
			+ "and (cr.agentId = ?2 or cr.agentId = 0 ) "
			+ "order by cr.agentId desc ", nativeQuery = true)
	public List<CouponSeed> findBySeedType(List<Integer> seedTypes, long agentId);
	
}

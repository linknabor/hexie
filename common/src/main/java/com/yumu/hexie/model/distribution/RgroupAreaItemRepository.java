package com.yumu.hexie.model.distribution;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yumu.hexie.model.ModelConstant;

public interface RgroupAreaItemRepository extends JpaRepository<RgroupAreaItem, Long> {

	@Query(value = "select m.* from RgroupAreaItem m left join productplat pp on pp.productid = m.productId "
			+ "where m.status="+ModelConstant.DISTRIBUTION_STATUS_ON+" and ((m.regionType=0) "
			+ "or (m.regionType=1 and m.regionId=?1) "
			+ "or (m.regionType=2 and m.regionId=?2) "
			+ "or (m.regionType=3 and m.regionId=?3) "
			+ "or (m.regionType=4 and m.regionId=?4)) "
			+ "and m.ruleCloseTime>?5 "
			+ "and pp.appid = ?6 ",
			countQuery = "select count(m.id) from RgroupAreaItem m left join productplat pp on pp.productid = m.productId "
					+ "where m.status="+ModelConstant.DISTRIBUTION_STATUS_ON+" and ((m.regionType=0) "
					+ "or (m.regionType=1 and m.regionId=?1) "
					+ "or (m.regionType=2 and m.regionId=?2) "
					+ "or (m.regionType=3 and m.regionId=?3) "
					+ "or (m.regionType=4 and m.regionId=?4)) "
					+ "and m.ruleCloseTime>?5 "
					+ "and pp.appid = ?6 ",
			nativeQuery = true)
	public List<RgroupAreaItem> findAllByUserInfo(long provinceId,long cityId,long countyId,long xiaoquId,long current, String appid, Pageable pageable);

	@Query(value = "select m.* from RgroupAreaItem m left join productplat pp on pp.productId = m.productId "
			+ "where m.status="+ModelConstant.DISTRIBUTION_STATUS_ON
			+ " and m.regionType!=4 and m.ruleCloseTime>?1 and pp.appid= ?2 ",
			countQuery = "select count(m.id) from RgroupAreaItem m left join productplat pp on pp.productId = m.productId "
					+ " where m.status="+ModelConstant.DISTRIBUTION_STATUS_ON
					+ " and m.regionType!=4 and m.ruleCloseTime>?1 and pp.appid= ?2 ",
			nativeQuery = true)
	public List<RgroupAreaItem> findAllDefalut(long current, String appid, Pageable pageable);

	@Query("select count(*) from RgroupAreaItem m where m.status="+ModelConstant.DISTRIBUTION_STATUS_ON+" and m.regionType!=4 and m.ruleCloseTime>?1")
	public int countAllDefalut(long current);
	
	@Query("select count(*) from RgroupAreaItem m where m.status="+ModelConstant.DISTRIBUTION_STATUS_ON+" and ((m.regionType=0) "
			+ "or (m.regionType=1 and m.regionId=?1) "
			+ "or (m.regionType=2 and m.regionId=?2) "
			+ "or (m.regionType=3 and m.regionId=?3) "
			+ "or (m.regionType=4 and m.regionId=?4)) "
			+ "and m.ruleCloseTime>?5")
	public int countByUserInfo(long provinceId,long cityId,long countyId,long xiaoquId,long current);
	
	@Query("from RgroupAreaItem m where m.status="+ModelConstant.DISTRIBUTION_STATUS_ON+" "
			+ "and m.ruleId=?1 "
			+ "and m.ruleCloseTime>?2 "
			+ "order by m.id desc ")
	public List<RgroupAreaItem> findAllAvaibleItemById(long id, long currentTimeMillis);
	
	public List<RgroupAreaItem> findByRuleId(long ruleId);
	
	public List<RgroupAreaItem> findByProductIdAndRegionId(long productId, long regionId);
	
	@Query("from RgroupAreaItem m where m.status="+ModelConstant.DISTRIBUTION_STATUS_ON+" and ((m.regionType=0) "
			+ "or (m.regionType=1 and m.regionId=?1) "
			+ "or (m.regionType=2 and m.regionId=?2) "
			+ "or (m.regionType=3 and m.regionId=?3) "
			+ "or (m.regionType=4 and m.regionId=?4)) "
			+ "and m.ruleCloseTime>?5 and featured is true "
			+ "order by m.sortNo asc,m.id desc ")
	public List<RgroupAreaItem> findFeatured(long provinceId,long cityId,long countyId,long xiaoquId,long current, Pageable pageable);
	
	@Query("from RgroupAreaItem m where m.status="+ModelConstant.DISTRIBUTION_STATUS_ON+" and ((m.regionType=0) "
			+ "or (m.regionType=1 and m.regionId=?1) "
			+ "or (m.regionType=2 and m.regionId=?2) "
			+ "or (m.regionType=3 and m.regionId=?3) "
			+ "or (m.regionType=4 and m.regionId=?4)) "
			+ "and m.ruleCloseTime>?5 and productType=?6 "
			+ "order by m.sortNo asc,m.id desc ")
	public List<RgroupAreaItem> findByCusProductType(long provinceId,long cityId,long countyId,long xiaoquId,long current,int productType, Pageable pageable);
	
	
	@Transactional
	@Modifying
	@Query(value = "delete from RgroupAreaItem where productId = ?1 ", nativeQuery = true)
	public void deleteByProductId(String productId);
	
	/**
	 * 查询样板商品
	 * @param status
	 * @param type
	 * @param current
	 * @param pageable
	 * @return
	 */
	@Query(value = "select m.* from RgroupAreaItem m "
			+ "join product p on m.productId = p.id "
			+ "where m.status = ?1 "
			+ "and m.productType = ?2 "
			+ "and m.ruleCloseTime> ?3 "
			+ "and p.demo = 1 "
			+ "group by m.ruleId ", 
			countQuery = "select count(*) from RgroupAreaItem m "
					+ "join product p on m.productId = p.id "
					+ "where m.status = ?1 "
					+ "and m.productType = ?2 "
					+ "and m.ruleCloseTime> ?3 "
					+ "and p.demo = 1 ", 
			nativeQuery = true)
	public List<RgroupAreaItem> findDemos(int status, int type, long current, Pageable pageable);
	
	/**
	 * 按业主绑定的房屋查询可以购买的商品
	 * @param status
	 * @param type
	 * @param current
	 * @param pageable
	 * @return
	 */
	@Query(value = "select m.* from RgroupAreaItem m "
			+ "join region r on m.regionId = r.id "
			+ "where m.status= ?1 "
			+ "and m.productType = ?2 "
			+ "and m.ruleCloseTime> ?3 "
			+ "and r.sectId = ?4 ",
			countQuery = "select count(m.id) from RgroupAreaItem m "
					+ "join region r on m.regionId = r.id "
					+ "where m.status= ?1 "
					+ "and m.productType = ?2 "
					+ "and m.ruleCloseTime> ?3 "
					+ "and r.sectId = ?4 ",
			nativeQuery = true)
	public List<RgroupAreaItem> findByBindedSect(int status, int type, long current, String sectId, Pageable pageable);
	
	/**
	 * 根据团购id查询，包括所在region的信息一并查
	 * @param status
	 * @param type
	 * @param current
	 * @param pageable
	 * @return
	 */
	@Query(value = "select m.ruleId, m.currentNum, m.groupMinNum, m.groupStatus, m.remark, r.id, r.name, r.parentName, "
			+ "r.latitude, r.longitude, r.xiaoquAddress, r.sectId "
			+ "from RgroupAreaItem m "
			+ "join region r on m.regionId = r.id "
			+ "where m.ruleId = ?1 ",
			countQuery = "select count(*) from RgroupAreaItem m "
					+ "join region r on m.regionId = r.id "
					+ "where m.ruleId = ?1 "
			, nativeQuery = true)
	public List<Object[]> findWithRegionByRuleId(long ruleId);
	
	
	@Transactional
	@Modifying
	@Query(value = "update RgroupAreaItem set status = ?1 where id = ?2 ", nativeQuery = true)
	public void updateStatus(int status, long id);
	
}

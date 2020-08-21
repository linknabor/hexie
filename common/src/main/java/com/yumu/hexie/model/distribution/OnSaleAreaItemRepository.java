package com.yumu.hexie.model.distribution;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yumu.hexie.model.ModelConstant;

public interface OnSaleAreaItemRepository extends JpaRepository<OnSaleAreaItem, Long> {

	@Query("from OnSaleAreaItem m where m.status="+ModelConstant.DISTRIBUTION_STATUS_ON+" "
			+ "and m.ruleId=?1 "
			+ "and m.ruleCloseTime>?2 "
			+ "order by m.id desc ")
	public List<OnSaleAreaItem> findAllAvaibleItemById(long ruleId,long current);


	@Query(value = "select m.* from OnSaleAreaItem m left join productplat pp on pp.productId = m.productId "
			+ "where m.status="+ModelConstant.DISTRIBUTION_STATUS_ON+" and ((m.regionType=0) "
			+ "or (m.regionType=1 and m.regionId=?1) "
			+ "or (m.regionType=2 and m.regionId=?2) "
			+ "or (m.regionType=3 and m.regionId=?3) "
			+ "or (m.regionType=4 and m.regionId=?4)) "
			+ "and m.ruleCloseTime>?5 and featured is true "
			+ "and pp.appid = ?6 ",
			countQuery = "select count(m.id) from OnSaleAreaItem m left join productplat pp on pp.productId = m.productId "
					+ "where m.status="+ModelConstant.DISTRIBUTION_STATUS_ON+" and ((m.regionType=0) "
					+ "or (m.regionType=1 and m.regionId=?1) "
					+ "or (m.regionType=2 and m.regionId=?2) "
					+ "or (m.regionType=3 and m.regionId=?3) "
					+ "or (m.regionType=4 and m.regionId=?4)) "
					+ "and m.ruleCloseTime>?5 and featured is true "
					+ "and pp.appid = ?6 ",
			nativeQuery = true)
	public List<OnSaleAreaItem> findFeatured(long provinceId,long cityId,long countyId,
			long xiaoquId,long current, String appid, Pageable spageable);
	

	@Query(value = "select m.* from OnSaleAreaItem m left join productplat pp on pp.productId = m.productId "
			+ "where m.status="+ModelConstant.DISTRIBUTION_STATUS_ON+" and ((m.regionType=0) "
			+ "or (m.regionType=1 and m.regionId=?1) "
			+ "or (m.regionType=2 and m.regionId=?2) "
			+ "or (m.regionType=3 and m.regionId=?3) "
			+ "or (m.regionType=4 and m.regionId=?4)) "
			+ "and m.ruleCloseTime>?5 and productType=?6 "
			+ "and pp.appid = ?7 ",
			countQuery = "select count(m.id) from OnSaleAreaItem m left join productplat pp on pp.productId = m.productId "
					+ "where m.status="+ModelConstant.DISTRIBUTION_STATUS_ON+" and ((m.regionType=0) "
					+ "or (m.regionType=1 and m.regionId=?1) "
					+ "or (m.regionType=2 and m.regionId=?2) "
					+ "or (m.regionType=3 and m.regionId=?3) "
					+ "or (m.regionType=4 and m.regionId=?4)) "
					+ "and m.ruleCloseTime>?5 and productType=?6 "
					+ "and pp.appid = ?7 ",
			nativeQuery = true)
	public List<OnSaleAreaItem> findByCusProductType(long provinceId,long cityId,long countyId,long 
			xiaoquId,long current,int productType, String appid, Pageable pageable);

	public List<OnSaleAreaItem> findByRuleId(long ruleId);
	
	@Transactional
	@Modifying
	@Query(value = "delete from OnSaleAreaItem where productId = ?1 ", nativeQuery = true)
	public void deleteByProductId(String productId);
	
	
	@Transactional
	@Modifying
	@Query(value = "update OnSaleAreaItem set status = ?1 where id = ?2 ", nativeQuery = true)
	public void updateStatus(int status, long id);
	
	/**
	 * 查询样板商品
	 * @param status
	 * @param type
	 * @param current
	 * @param pageable
	 * @return
	 */
	@Query(value = "select m.* from OnSaleAreaItem m "
			+ "join product p on m.productId = p.id "
			+ "where m.status = ?1 "
			+ "and m.productType = ?2 "
			+ "and m.ruleCloseTime> ?3 "
			+ "and p.demo = 1 "
			+ "group by m.ruleId ", 
			countQuery = "select count(*) from OnSaleAreaItem m "
					+ "join product p on m.productId = p.id "
					+ "where m.status = ?1 "
					+ "and m.productType = ?2 "
					+ "and m.ruleCloseTime> ?3 "
					+ "and m.productCategoryId = ?4 "
					+ "and p.demo = 1 ", 
			nativeQuery = true)
	public List<OnSaleAreaItem> findDemos(int status, int type, long current, long categoryId, Pageable pageable);
	
	/**
	 * 按业主绑定的房屋查询可以购买的商品
	 * @param status
	 * @param type
	 * @param current
	 * @param pageable
	 * @return
	 */
	@Query(value = "select m.* from OnSaleAreaItem m "
			+ "join region r on m.regionId = r.id "
			+ "where m.status= ?1 "
			+ "and m.productType = ?2 "
			+ "and m.ruleCloseTime> ?3 "
			+ "and m.productCategoryId = ?4 "
			+ "and r.sectId = ?5 ",
			countQuery = "select count(m.id) from OnSaleAreaItem m "
					+ "join region r on m.regionId = r.id "
					+ "where m.status= ?1 "
					+ "and m.productType = ?2 "
					+ "and m.ruleCloseTime> ?3 "
					+ "and m.productCategoryId = ?4 "
					+ "and r.sectId = ?5 ",
			nativeQuery = true)
	public List<OnSaleAreaItem> findByBindedSect(int status, int type, long current, long categoryId, String sectId, Pageable pageable);
	
	
	/**
	 * 按业主绑定的房屋查询可以购买的商品
	 * @param status
	 * @param type
	 * @param current
	 * @param pageable
	 * @return
	 */
	@Query(value = "select m.* from OnSaleAreaItem m "
			+ "join region r on m.regionId = r.id "
			+ "where m.status= ?1 "
			+ "and m.productType = ?2 "
			+ "and m.ruleCloseTime> ?3 "
			+ "and IF (?4!='', m.productName like CONCAT('%',?4,'%'), 1=1) "
			+ "and r.sectId = ?5 ",
			countQuery = "select count(m.id) from OnSaleAreaItem m "
					+ "join region r on m.regionId = r.id "
					+ "where m.status= ?1 "
					+ "and m.productType = ?2 "
					+ "and m.ruleCloseTime> ?3 "
					+ "and IF (?4!='', m.productName like CONCAT('%',?4,'%'), 1=1) "
					+ "and r.sectId = ?5 ",
			nativeQuery = true)
	public List<OnSaleAreaItem> findByProductName(int status, int type, long current, String ruleName, String sectId, Pageable pageable);
	
	
	
}

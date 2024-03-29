package com.yumu.hexie.model.commonsupport.info;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Product findById(long id);
	
	//不要修改顺序--核销券
	String sqlColumn1 = " p.id, p.name, p.productType, p.oriPrice, p.miniPrice, p.singlePrice, rule.status, p.startDate, p.endDate, "
			+ "p.mainPicture, p.smallPicture, p.pictures, p.serviceDesc, p.demo, p.totalCount-p.saledNum as totalCount, a.name as agentName, a.agentNo, rule.limitNumOnce, "
			+ "rule.postageFee, rule.freeShippingNum, 0 as groupMinNum, p.productCategoryId, item.sortNo, pp.appid, count(r.id) as counts, count(distinct op.id) as operCounts ";
	
	//不要修改顺序--团购
	String sqlColumn2 = " p.id, p.name, p.productType, p.oriPrice, p.miniPrice, p.singlePrice, rule.status, p.startDate, p.endDate, "
			+ "p.mainPicture, p.smallPicture, p.pictures, p.serviceDesc, p.demo, p.totalCount-p.saledNum as totalCount, a.name as agentName, a.agentNo, rule.limitNumOnce, "
			+ "rule.postageFee, rule.freeShippingNum, CONVERT(rule.groupMinNum, SIGNED INTEGER) as groupMinNum, p.productCategoryId, item.sortNo, pp.appid, count(r.id) as counts, 0 as operCounts ";
		
	//各类商品合集
	String sqlColumn3 = "p.id, p.name, p.productType, p.oriPrice, p.miniPrice, p.singlePrice, p.status, p.startDate, p.endDate, "
				+ "p.mainPicture, p.smallPicture, p.pictures, p.serviceDesc, p.totalCount-p.saledNum as totalCount, a.name as agentName, a.agentNo "; 
	
	/**
	 * 核销券、特卖、团购合一
	 * @param productType
	 * @param productId
	 * @param productName
	 * @param status
	 * @param agentId
	 * @param isDemo
	 * @param pageable
	 * @return
	 */
	@Query(value = "select " + sqlColumn3
			+ "from product p "
			+ "left join agent a on a.id = p.agentId "
			+ "where p.productType in ( ?1 ) "
			+ "and IF (?2!='', p.status = ?2, 1=1) "
			+ "and IF (?3!='', p.name like CONCAT('%',?3,'%'), 1=1) "
			+ "and (COALESCE(?4) IS NULL OR (p.agentId IN (?4) )) "
			, countQuery = "select count(p.id) from product p "
					+ "left join agent a on a.id = p.agentId "
					+ "where p.productType in ( ?1 ) "
					+ "and IF (?2!='', p.status = ?2, 1=1) "
					+ "and IF (?3!='', p.name like CONCAT('%',?3,'%'), 1=1) "
					+ "and (COALESCE(?4) IS NULL OR (p.agentId IN (?4) )) "
					, nativeQuery = true)
	Page<Object[]> getSupportProduct(List<String> productType, String status, String productName, List<Long>agentId, Pageable pageable);
	
	/**
	 * 团购
	 * @param productType
	 * @param productId
	 * @param productName
	 * @param status
	 * @param agentId
	 * @param isDemo
	 * @param pageable
	 * @return
	 */
	@Query(value = "select " + sqlColumn2
			+ "from product p "
			+ "left join agent a on a.id = p.agentId "
			+ "join rgrouprule rule on rule.productId = p.id "
			+ "join rgroupareaitem item on item.productId = p.id "
			+ "join region r on r.id = item.regionId "
			+ "left join productplat pp on p.id = pp.productId "
			+ "where p.productType = ?1 "
			+ "and IF (?2!='', p.id = ?2, 1=1) "
			+ "and IF (?3!='', p.name like CONCAT('%',?3,'%'), 1=1) "
			+ "and IF (?4!='', item.status = ?4, 1=1) "
			+ "and (COALESCE(?5) IS NULL OR (p.agentId IN (?5) )) "
			+ "and IF (?6!='', p.demo = ?6, 1=1) "
			+ "group by p.id ",
			countQuery = "select p.id, count(r.id) from product p "
					+ "left join agent a on a.id = p.agentId "
					+ "join rgroupareaitem item on item.productId = p.id "
					+ "join region r on r.id = item.regionId "
					+ "left join productplat pp on p.id = pp.productId "
					+ "where p.productType = ?1 "
					+ "and IF (?2!='', p.id = ?2, 1=1) "
					+ "and IF (?3!='', p.name like CONCAT('%',?3,'%'), 1=1) "
					+ "and IF (?4!='', item.status = ?4, 1=1) "
					+ "and (COALESCE(?5) IS NULL OR (p.agentId IN (?5) )) "
					+ "and IF (?6!='', p.demo = ?6, 1=1) "
					+ "group by p.id ", nativeQuery = true)
	Page<Object[]> findByMultiCondRgroup(String productType, String productId, String productName, String status, 
			List<Long>agentId, String isDemo, Pageable pageable);
	
	
	/**
	 * 核销券、特卖
	 * @param productType
	 * @param productId
	 * @param productName
	 * @param status
	 * @param agentId
	 * @param isDemo
	 * @param pageable
	 * @return
	 */
	@Query(value = "select " + sqlColumn1
			+ "from product p "
			+ "left join agent a on a.id = p.agentId "
			+ "join onsalerule rule on rule.productId = p.id "
			+ "join onsaleareaitem item on item.productId = p.id "
			+ "join region r on r.id = item.regionId "
			+ "left join serviceOperatorItem op on op.serviceId = p.id "
			+ "left join productplat pp on p.id = pp.productId "
			+ "where p.productType = ?1 "
			+ "and IF (?2!='', p.id = ?2, 1=1) "
			+ "and IF (?3!='', p.name like CONCAT('%',?3,'%'), 1=1) "
			+ "and IF (?4!='', item.status = ?4, 1=1) "
			+ "and (COALESCE(?5) IS NULL OR (p.agentId IN (?5) )) "
			+ "and IF (?6!='', p.demo = ?6, 1=1) "
			+ "group by p.id ",
			countQuery = "select p.id, count(r.id) from product p "
					+ "left join agent a on a.id = p.agentId "
					+ "join onsaleareaitem item on item.productId = p.id "
					+ "join region r on r.id = item.regionId "
					+ "left join productplat pp on p.id = pp.productId "
					+ "where p.productType = ?1 "
					+ "and IF (?2!='', p.id = ?2, 1=1) "
					+ "and IF (?3!='', p.name like CONCAT('%',?3,'%'), 1=1) "
					+ "and IF (?4!='', item.status = ?4, 1=1) "
					+ "and (COALESCE(?5) IS NULL OR (p.agentId IN (?5) )) "
					+ "and IF (?6!='', p.demo = ?6, 1=1) "
					+ "group by p.id ", nativeQuery = true)
	Page<Object[]> findByMultiCondOnsale(String productType, String productId, String productName, String status, 
			List<Long>agentId, String isDemo, Pageable pageable);
	

	@Transactional
	@Modifying
	@Query(value = "update product set status = ?1 where id = ?2 ", nativeQuery = true)
	void updateStatus(int status, long id);
	
	@Transactional
	@Modifying
	@Query(value = "update product set demo = ?1 where id = ?2 ", nativeQuery = true)
	void updateDemo(int demo, long id);
	
	@Query(value = "select * from product where status = ?1 and productType >= 1000 ", nativeQuery = true)
	List<Product> findByStatusMultiType(int status);
	
//	@Transactional
//	@Modifying
//	@Query(value = "update product set totalCount = totalCount - ?1 where id = ?2 ", nativeQuery = true)
//	void updateStock(int count, long id);
	
	@Query(value = "select p.* from product p join productRule pr on p.id = pr.productId where pr.ruleId = ?1 "
			, nativeQuery = true)
	List<Product> findMultiByRuleId(long ruleId);
	
	
	@Query(value = "select distinct p.* from product p join productRule pr on p.id = pr.productId "
			+ "join rgroupRule r on r.id = pr.ruleId "
			+ "where r.ownerId = ?1 "
			+ "and r.createDate >= ?2 "
			+ "and IF (?3!='', p.name like CONCAT('%',?3,'%'), 1=1) "
			+ "and (COALESCE(?4) IS NULL OR (p.depotId not IN (?4) )) "
			, countQuery = "select count(distinct p.id) from product p join productRule pr on p.id = pr.productId "
					+ "join rgroupRule r on r.id = pr.ruleId "
					+ "where r.ownerId = ?1 "
					+ "and r.createDate >= ?2 "
					+ "and IF (?3!='', p.name like CONCAT('%',?3,'%'), 1=1) "
					+ "and (COALESCE(?4) IS NULL OR (p.depotId not IN (?4) )) "
			, nativeQuery = true)
	Page<Product> findProductFromSalesByOwner(long userId, Date createDate, String productName, List<String> excludeDepotIds, Pageable pageable);
	
	
}

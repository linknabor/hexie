package com.yumu.hexie.model.commonsupport.info;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
	
	//不要修改顺序
	String sqlColumn1 = " p.id, p.name, p.productType, p.oriPrice, p.miniPrice, p.singlePrice, rule.status, p.startDate, p.endDate, "
			+ "p.mainPicture, p.smallPicture, p.pictures, p.serviceDesc, p.demo, a.name as agentName, a.agentNo, rule.limitNumOnce, "
			+ "item.sortNo, pp.appid, count(r.id) as counts, count(distinct op.id) as operCounts ";
	
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
			+ "group by p.id "
			+ "order by p.id desc \n#pageable\n ",
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
					+ "group by p.id ", 
			nativeQuery = true)
	Page<Object[]> findByPageSelect(String productType, String productId, String productName, String status, 
			List<Integer>agentId, String isDemo, Pageable pageable);

	@Transactional
	@Modifying
	@Query(value = "update product set status = ?1 where id = ?2 ", nativeQuery = true)
	void updateStatus(int status, long id);
	
	@Transactional
	@Modifying
	@Query(value = "update product set demo = ?1 where id = ?2 ", nativeQuery = true)
	void updateDemo(int demo, long id);
	
}

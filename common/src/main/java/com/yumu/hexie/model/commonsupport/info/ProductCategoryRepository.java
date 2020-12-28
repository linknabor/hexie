package com.yumu.hexie.model.commonsupport.info;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
	
	
	/**
	 * 按业主绑定的房屋查询可以购买的商品
	 * @param status
	 * @param type
	 * @param current
	 * @param sectId
	 * @return
	 */
	@Query(value = "select distinct pc.* from productCategory pc "
			+ "join OnSaleAreaItem m on pc.id = m.productCategoryId "
			+ "join region r on m.regionId = r.id "
			+ "where m.status= ?1 "
			+ "and m.productType = ?2 "
			+ "and m.ruleCloseTime> ?3 "
			+ "and r.sectId = ?4 "
			+ "order by pc.sort ", 
			nativeQuery = true)
	public List<ProductCategory> findCategoryByBindedSect(int status, int type ,long current, String sectId);
	
	/**
	 * 样板查询
	 * @param status
	 * @param type
	 * @param current
	 * @return
	 */
	@Query(value = "select distinct pc.* from productCategory pc "
			+ "join OnSaleAreaItem m on pc.id = m.productCategoryId "
			+ "join product p on m.productId = p.id "
			+ "where m.status= ?1 "
			+ "and m.productType = ?2 "
			+ "and m.ruleCloseTime> ?3 "
			+ "and p.demo = 1 "
			+ "order by pc.sort ", 
			nativeQuery = true)
	public List<ProductCategory> findCategoryDemo(int status, int type ,long current);

	
}

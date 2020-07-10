package com.yumu.hexie.model.merchant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
	//根据商品类型来判断
	@Query("from Merchant p where p.productType = ?1")
	public Merchant findMerchantByProductType(int productType);
	
	@Query(value = "select m.id from merchant m where m.status =?1 "
			+ "and IF (?2!='', m.merchantNo = ?2, 1=1 )"
			+ "and IF (?3!='', m.name like CONCAT('%',?3,'%'), 1=1) ", nativeQuery = true)
	public List<Integer> findByMerchantNoOrName(int status, String merchantNo, String merchantName);
	
	public Merchant findByMerchantNo(String merchantNo);
}

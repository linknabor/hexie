package com.yumu.hexie.model.commonsupport.info;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPlatRepository extends JpaRepository<ProductPlat, Long> {
	
	public ProductPlat findByProductIdAndAppId(long productId, String appid);

}

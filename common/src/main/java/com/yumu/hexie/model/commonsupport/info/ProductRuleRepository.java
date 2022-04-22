package com.yumu.hexie.model.commonsupport.info;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRuleRepository extends JpaRepository<ProductRule, Long> {

	ProductRule findByRuleIdAndProductId(long ruleId, long productId);
}

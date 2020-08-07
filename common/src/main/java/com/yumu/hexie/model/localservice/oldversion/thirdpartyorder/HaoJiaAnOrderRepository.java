package com.yumu.hexie.model.localservice.oldversion.thirdpartyorder;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HaoJiaAnOrderRepository extends JpaRepository<HaoJiaAnOrder, Long>{
	public HaoJiaAnOrder findBysOrderId(long sOrderId);
	public HaoJiaAnOrder findByyOrderId(long yOrderId);
}

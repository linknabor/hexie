package com.yumu.hexie.model.localservice.oldversion.thirdpartyorder;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BovoOrderRepository extends JpaRepository<BovoOrder, Long> {
	public BovoOrder findBysOrderId(long sOrderId);
	public BovoOrder findByyOrderId(long yOrderId);
}

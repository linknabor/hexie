package com.yumu.hexie.model.localservice.oldversion.thirdpartyorder;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GaofeiOrderRepository extends JpaRepository<GaofeiOrder, Long> {
	public GaofeiOrder findBysOrderId(long sOrderId);
	public GaofeiOrder findByyOrderId(long yOrderId);
}

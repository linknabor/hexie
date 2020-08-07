package com.yumu.hexie.model.localservice.oldversion.thirdpartyorder;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HaorenshengOrderRepository extends JpaRepository<HaorenshengOrder, Long> {
	public HaorenshengOrder findBysOrderId(long sOrderId);
	public HaorenshengOrder findByyOrderId(long yOrderId);
}

package com.yumu.hexie.model.localservice.oldversion.thirdpartyorder;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DaoJiaMeiOrderRepository extends JpaRepository<DaoJiaMeiOrder, Long> {
	public DaoJiaMeiOrder findBysOrderId(long sOrderId);
	public DaoJiaMeiOrder findByyOrderId(long yOrderId);
}

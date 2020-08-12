package com.yumu.hexie.model.localservice.oldversion.thirdpartyorder;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FlowerPlusOrderRepository extends JpaRepository<FlowerPlusOrder, Long> {
	public List<FlowerPlusOrder> findBysOrderId(long sOrderId);
	public List<FlowerPlusOrder> findByyOrderId(long yOrderId);
}

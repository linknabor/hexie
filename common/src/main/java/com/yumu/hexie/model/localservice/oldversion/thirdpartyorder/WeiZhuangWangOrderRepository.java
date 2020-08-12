package com.yumu.hexie.model.localservice.oldversion.thirdpartyorder;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WeiZhuangWangOrderRepository extends JpaRepository<WeiZhuangWangOrder, Long> {
	public WeiZhuangWangOrder findBysOrderId(long sOrderId);
	public WeiZhuangWangOrder findByyOrderId(long yOrderId);
}

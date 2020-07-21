package com.yumu.hexie.model.market;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EvoucherRepository extends JpaRepository<Evoucher, Long> {

	
	List<Evoucher> findByCode(String code);
	
	List<Evoucher> findByUserId(long userId);
	
	List<Evoucher> findByOrderId(long orderId);
	
	List<Evoucher> findByOrderIdAndStatus(long orderId, int status);
	
	
	
}

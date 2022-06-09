package com.yumu.hexie.model.market;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRecordRepository extends JpaRepository<RefundRecord, Long> {

	public RefundRecord findById(long id);
	
	public List<RefundRecord> findByOrderId(long orderId, Sort sort);
}

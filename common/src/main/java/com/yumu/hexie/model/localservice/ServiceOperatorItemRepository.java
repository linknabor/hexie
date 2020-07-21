package com.yumu.hexie.model.localservice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOperatorItemRepository extends JpaRepository<ServiceOperatorItem, Long> {


	ServiceOperatorItem findByOperatorIdAndServiceId(long operatorId, long serviceId);
	
	List<ServiceOperatorItem> findByOperatorId(long operatorId);
	
	void deleteByServiceId(long serviceId);
	
	
}

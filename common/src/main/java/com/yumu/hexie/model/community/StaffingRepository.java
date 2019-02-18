package com.yumu.hexie.model.community;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StaffingRepository extends JpaRepository<Staffing, String> {
	
	@Query(value="select * from staffing where region_id = ?1 and staffing_status = '0' ",nativeQuery = true)
	public List<Staffing> getStaffing(String regionId);
	
}

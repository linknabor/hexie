package com.yumu.hexie.model.localservice.repair;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RepairAreaRepository extends JpaRepository<RepairArea, Long>{
	
	@Query(value = "select * from RepairArea ra where (COALESCE(?1) IS NULL OR (ra.sectId IN (?1) ))", 
			nativeQuery = true)
	public List<RepairArea> findBySectIds(List<String> sectIds);
	
	public List<RepairArea> findBySectId(String sectId);
	
	public List<RepairArea> findByCspId(String cspId);

}

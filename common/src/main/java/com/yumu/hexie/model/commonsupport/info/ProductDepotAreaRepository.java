package com.yumu.hexie.model.commonsupport.info;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.yumu.hexie.model.distribution.region.Region;

@Repository
public interface ProductDepotAreaRepository extends JpaRepository<ProductDepotArea, Long>{

	@Query(value = "select r.* from ProductDepotArea a join region r on a.regionId = r.id "
			+ "where a.depotId = ?1 ", 
			nativeQuery = true)
	List<Region> findRegionsByDepotId(long depotId);
}

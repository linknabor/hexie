package com.yumu.hexie.model.region;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionUrlRepository extends JpaRepository<RegionUrl, Long>{
	
	RegionUrl findByRegionName(String regionname);
}

package com.yumu.hexie.model.region;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionUrlRepository extends JpaRepository<RegionUrl, Long>{
	
	List<RegionUrl> findByRegionName(String regionname);
}

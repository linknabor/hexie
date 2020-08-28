package com.yumu.hexie.model.distribution.region;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CountyRepository extends JpaRepository<County, Long> {

	
	public List<County> findByCityIdAndStatus(long cityId, int status);
}

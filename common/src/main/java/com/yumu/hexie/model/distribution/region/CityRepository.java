package com.yumu.hexie.model.distribution.region;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {

	List<City> findByProvinceIdAndStatus(long provinceId, int status);
	
	City findByCityId(long cityId);
}

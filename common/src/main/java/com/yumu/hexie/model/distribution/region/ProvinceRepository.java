package com.yumu.hexie.model.distribution.region;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvinceRepository extends JpaRepository<Province, Long> {

	public List<Province> findByStatus(int status);
	
}

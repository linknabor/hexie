package com.yumu.hexie.model.express;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ExpressRepository extends JpaRepository<Express, Long>{
	public List<Express> findByUserId(long userId);
}

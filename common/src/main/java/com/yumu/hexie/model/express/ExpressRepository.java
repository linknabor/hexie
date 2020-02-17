package com.yumu.hexie.model.express;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ExpressRepository extends JpaRepository<Express, Long>{
	public Express findByUserId(long userId);
}

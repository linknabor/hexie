package com.yumu.hexie.model.view;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {


	List<Menu> findByCspId(String cspId, Sort sort);
	
	List<Menu> findByAppid(String appid, Sort sort);
	
	List<Menu> findByDef(Boolean def, Sort sort);
	
}

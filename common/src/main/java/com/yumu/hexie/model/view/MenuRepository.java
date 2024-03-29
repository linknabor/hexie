package com.yumu.hexie.model.view;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

	List<Menu> findBySectId(String sectId, Sort sort);
	
	List<Menu> findByCspId(String cspId, Sort sort);
	
	List<Menu> findByDefaultTypeLessThan(int defaultType, Sort sort);
	
	List<Menu> findByAppidAndDefaultTypeLessThan(String appid, int defaultType, Sort sort);
	
	List<Menu> findByAppidAndType(String appid, String type);
	
}

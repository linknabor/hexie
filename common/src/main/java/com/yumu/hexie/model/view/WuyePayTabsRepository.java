package com.yumu.hexie.model.view;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WuyePayTabsRepository extends JpaRepository<WuyePayTabs, Long> {

	public List<WuyePayTabs> findByAppId(String appId, Sort sort);
	
}

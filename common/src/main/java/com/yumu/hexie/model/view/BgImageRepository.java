package com.yumu.hexie.model.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BgImageRepository extends JpaRepository<BgImage, Long> {

	List<BgImage> findByTypeAndFromSys(int type, String fromSys);
	
}

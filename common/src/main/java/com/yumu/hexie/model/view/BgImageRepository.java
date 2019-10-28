package com.yumu.hexie.model.view;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BgImageRepository extends JpaRepository<BgImage, Long> {

	BgImage findByTypeAndAppId(int type, String fromSys);
	
}

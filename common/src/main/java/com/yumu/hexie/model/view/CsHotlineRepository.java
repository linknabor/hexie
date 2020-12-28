package com.yumu.hexie.model.view;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CsHotlineRepository extends JpaRepository<CsHotline, Long> {

	CsHotline findByFromSys(String fromSys);
}

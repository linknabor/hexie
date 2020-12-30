package com.yumu.hexie.model.msgtemplate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MsgTempalateRepository extends JpaRepository<MsgTemplate, Long>{

	MsgTemplate findByNameAndAppidAndStatus(String appid, String name, int status);
	
}

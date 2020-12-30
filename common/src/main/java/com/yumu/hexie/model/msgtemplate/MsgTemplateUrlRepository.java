package com.yumu.hexie.model.msgtemplate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MsgTemplateUrlRepository extends JpaRepository<MsgTemplateUrl, Long> {

	MsgTemplateUrl findByNameAndStatus(String name, int status);
	
}

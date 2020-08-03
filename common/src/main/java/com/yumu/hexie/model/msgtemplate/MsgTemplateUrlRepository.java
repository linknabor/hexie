package com.yumu.hexie.model.msgtemplate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MsgTemplateUrlRepository extends JpaRepository<MsgTemplateUrl, Long> {

	MsgTemplateUrl findByNameAndAppid(String name, String appid);
	
	List<MsgTemplateUrl> findByStatus(int status);
	
}

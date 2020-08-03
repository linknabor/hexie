package com.yumu.hexie.model.msgtemplate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MsgTempalateRepository extends JpaRepository<MsgTemplate, Long>{

	MsgTemplate findByNameAndAppid(String name, String appid);
	
	List<MsgTemplate> findByStatus(int status);
}

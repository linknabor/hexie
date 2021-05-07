package com.yumu.hexie.model.msgtemplate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MsgTempalateRepository extends JpaRepository<MsgTemplate, Long>{

	MsgTemplate findByNameAndAppidAndStatus(String appid, String name, int status);
	
	MsgTemplate findByValue(String value);
	
	List<MsgTemplate> findByAppidAndTypeAndBizType(String appid, int type, int bizType);
	
}

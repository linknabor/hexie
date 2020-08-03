package com.yumu.hexie.model.msgtemplate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MsgUrlRepository extends JpaRepository<MsgUrl, Long> {

	MsgUrl findByNameAndAppid(String name, String appid);
	
	List<MsgUrl> findByStatus(int status);
	
}

package com.yumu.hexie.model.subscribemsg;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSubscribeMsgRepository extends JpaRepository<UserSubscribeMsg, Long> {

	UserSubscribeMsg findByOpenidAndTemplateId(String openid, String templateId);
	
	List<UserSubscribeMsg> findByAliuseridAndAppid(String aliuserid, String appid);
}

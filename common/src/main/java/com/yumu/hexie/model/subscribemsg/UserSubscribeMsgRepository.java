package com.yumu.hexie.model.subscribemsg;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSubscribeMsgRepository extends JpaRepository<UserSubscribeMsg, Long> {

	List<UserSubscribeMsg> findByOpenidAndBizType(String openid, int bizType);
	
	UserSubscribeMsg findByOpenidAndTemplateId(String openid, String templateId);
}

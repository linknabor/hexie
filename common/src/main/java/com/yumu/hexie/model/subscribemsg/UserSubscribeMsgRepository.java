package com.yumu.hexie.model.subscribemsg;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSubscribeMsgRepository extends JpaRepository<UserSubscribeMsg, Long> {

	UserSubscribeMsg findByOpenidAndTemplateId(String openid, String templateId);
}

package com.yumu.hexie.service.home;

import com.yumu.hexie.integration.daojia.haojiaan.HaoJiaAnReq;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.vo.YuyueQueryOrder;

public interface HaoJiaAnService {

	public Long addNoNeedPayOrder(User user, HaoJiaAnReq haoJiaAnReq, long addressId);
	
	public YuyueQueryOrder queryYuYueOrder(User user, long orderId);
}

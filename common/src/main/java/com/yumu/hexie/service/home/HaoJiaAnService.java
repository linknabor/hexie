package com.yumu.hexie.service.home;

import com.yumu.hexie.integration.daojia.haojiaan.HaoJiaAnReq;
import com.yumu.hexie.model.localservice.oldversion.YuyueOrder;
import com.yumu.hexie.model.localservice.oldversion.thirdpartyorder.HaoJiaAnOrder;
import com.yumu.hexie.model.user.User;

public interface HaoJiaAnService {

	public Long addNoNeedPayOrder(User user, HaoJiaAnReq haoJiaAnReq,
			long addressId);

}

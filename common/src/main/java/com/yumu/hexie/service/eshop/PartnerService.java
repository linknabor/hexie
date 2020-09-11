package com.yumu.hexie.service.eshop;

import com.yumu.hexie.integration.notify.PartnerNotification;
import com.yumu.hexie.model.user.Partner;
import com.yumu.hexie.model.user.User;

public interface PartnerService {

	void save(Partner partner);

	void invalidate(PartnerNotification partnerNotification);

	void checkValidation(User user);
	
	

}

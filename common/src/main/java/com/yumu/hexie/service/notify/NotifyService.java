package com.yumu.hexie.service.notify;

import com.yumu.hexie.integration.notify.PayNotifyDTO;
import com.yumu.hexie.integration.notify.PayNotifyDTO.AccountNotify;
import com.yumu.hexie.integration.notify.PayNotifyDTO.ServiceNotify;

public interface NotifyService {

	void noticePayed(PayNotifyDTO payNotifyDTO);

	void sendPayNotify(AccountNotify accountNotify);

	void sendServiceNotify(ServiceNotify serviceNotify);

}

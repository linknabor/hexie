package com.yumu.hexie.service.notify;

import com.yumu.hexie.integration.notify.PayNotifyDTO;
import com.yumu.hexie.integration.notify.PayNotifyDTO.AccountNotification;
import com.yumu.hexie.integration.notify.PayNotifyDTO.ServiceNotification;

public interface NotifyService {

	void notify(PayNotifyDTO payNotifyDTO);

	void sendPayNotificationAsync(AccountNotification accountNotification);

	void sendServiceNotificationAsync(ServiceNotification serviceNotification);

}

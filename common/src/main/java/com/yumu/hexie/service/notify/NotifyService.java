package com.yumu.hexie.service.notify;

import com.yumu.hexie.integration.notify.PayNotification;
import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;
import com.yumu.hexie.integration.notify.PayNotification.ServiceNotification;

public interface NotifyService {

	void notify(PayNotification payNotification);

	void sendPayNotificationAsync(AccountNotification accountNotification);

	void sendServiceNotificationAsync(ServiceNotification serviceNotification);

}

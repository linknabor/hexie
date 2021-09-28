package com.yumu.hexie.service.notify;

import java.util.List;

import com.yumu.hexie.integration.notify.PartnerNotification;
import com.yumu.hexie.integration.notify.PayNotification;
import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;
import com.yumu.hexie.integration.notify.PayNotification.ServiceNotification;
import com.yumu.hexie.integration.notify.ConversionNotification;
import com.yumu.hexie.integration.notify.InvoiceNotification;

import com.yumu.hexie.integration.notify.WorkOrderNotification;

public interface NotifyService {

	void notify(PayNotification payNotification);

	void sendPayNotificationAsync(AccountNotification accountNotification);

	void sendServiceNotificationAsync(ServiceNotification serviceNotification);

	void updateServiceOrderStatusAsync(String orderId);

	void notifyDeliveryAsync(String orderId);
	
	void updatePartner(List<PartnerNotification> list);

	void notifyEshopRefund(String orderId);

	void notifyWuyeCouponConsumeAsync(String orderId, String couponId);

	void sendPayNotification4BinderAsync(AccountNotification accountNotification);

	void notifyWorkOrderMsgAsync(WorkOrderNotification workOrderNotification);

	void notifyConversionAsync(ConversionNotification notification);

	void notifyInvoiceMsgAsync(InvoiceNotification invoiceNotification);


}

package com.yumu.hexie.service.notify;

public interface NotifyQueueTask {

	void sendWuyeNotificationAysc();
	
	void sendCustomServiceNotificationAysc();

	void updateServiceCfgAysc();
	
	void updateOrderStatusAysc();
	
	void sendDeliveryNotificationAsyc();

	void updatePartnerAsync();

	void eshopRefundAsync();

	void consumeWuyeCouponAsync();

	void sendWuyeNotification4HouseBinderAysc();

	void sendWorkOrderMsgNotificationAsyc();

	void handleConversionAsyc();

	void sendInvoiceMsgAsyc();

	void sendReceiptMsgAsyc();

	void noticeRgroupArrial();

	void notifyGroupSuccess();

	void notifyInteractComment();

	void notifyInteractGrade();

	void notifyRenovation();

}

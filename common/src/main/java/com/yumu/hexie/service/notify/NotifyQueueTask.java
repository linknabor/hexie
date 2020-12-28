package com.yumu.hexie.service.notify;

public interface NotifyQueueTask {

	void sendWuyeNotificationAysc();
	
	void sendCustomServiceNotificationAysc();

	void updateOpereratorAysc();

	void updateServiceCfgAysc();
	
	void updateOrderStatusAysc();
	
	void sendDeliveryNotificationAsyc();

	void updatePartnerAsync();

	void eshopRefundAsync();

	void consumeWuyeCouponAsync();

}

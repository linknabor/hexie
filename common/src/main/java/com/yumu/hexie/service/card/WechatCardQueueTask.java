package com.yumu.hexie.service.card;

public interface WechatCardQueueTask {

	void eventSubscribe();
	
	void eventUserGetCard();
	
	void eventUpdateCard();
	
	void updatePointAsync();
	
	void wuyeRefund();
	
}

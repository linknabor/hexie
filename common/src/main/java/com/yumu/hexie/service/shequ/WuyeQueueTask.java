package com.yumu.hexie.service.shequ;

public interface WuyeQueueTask {

	void bindHouseByTrade();
	
	void eventScanSubscribe4Invoice();	//扫二维码申请电子发票的关注事件（未关注过的用户）
	
	void eventScan4Invoice();	//扫二维码申请电子发票的事件（已关注过的用户）
	
}

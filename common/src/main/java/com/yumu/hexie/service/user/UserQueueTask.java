package com.yumu.hexie.service.user;

public interface UserQueueTask {

	void eventSubscribe();
	
	void eventUnsubscribe();
	
	void unsubscribeNotify();
}

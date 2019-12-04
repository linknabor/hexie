package com.yumu.hexie.service.batch;

public interface BatchService {

	void updateRepeatUserShareCode();
	
	void updateUserShareCode();
	
	void fixBindHouse(String userId, String tradeWaterId);

	void bindHouseBatch(String appId);

}

package com.yumu.hexie.service.cache;

import com.yumu.hexie.model.user.User;

public interface CacheService {
	
	
	String getCacheKey(User user);

	void clearUserCache(String key);

	

}

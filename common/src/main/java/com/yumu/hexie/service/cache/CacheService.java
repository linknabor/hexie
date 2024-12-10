package com.yumu.hexie.service.cache;

import com.yumu.hexie.model.user.User;

public interface CacheService {
	
	
	void clearUserCache(User user);

	void delCache(String key);

}

package com.yumu.hexie.service.user;

import com.yumu.hexie.model.user.User;

public interface PointService {

	void updatePoint(User user, String point, String key);

	void updatePoint(User user, String point, String key, boolean notifyWechat);
	
}

package com.yumu.hexie.service.health;

import com.yumu.hexie.model.community.Thread;
import com.yumu.hexie.model.user.User;

public interface HealthService {
	
	void healthReport(User user, Thread thread);	//健康上报
	void maskReservation(User user, Thread thread);	//口罩预约
	void testTemplate(User user);

}

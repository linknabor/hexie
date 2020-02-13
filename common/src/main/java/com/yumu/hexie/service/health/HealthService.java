package com.yumu.hexie.service.health;

import org.springframework.data.domain.Page;

import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.model.community.Thread;
import com.yumu.hexie.model.user.User;

public interface HealthService {
	
	void addHealthReport(User user, Thread thread);	//健康上报
	
	void addMaskReservation(User user, Thread thread);	//口罩预约
	
	void addServiceReservation(User user, Thread thread);	//服务预约
	
	Page<Thread> getHealthReport(BaseRequestDTO<Thread> baseRequestDTO);	//获取健康上报
	
	Page<Thread> getMaskReservation(BaseRequestDTO<Thread> baseRequestDTO);	//口罩预约列表

	Page<Thread> getServiceReservation(BaseRequestDTO<Thread> baseRequestDTO);	//服务预约列表	

	
	
}

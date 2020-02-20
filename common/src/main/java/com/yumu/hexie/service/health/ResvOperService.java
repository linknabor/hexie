package com.yumu.hexie.service.health;

import java.util.List;

import org.springframework.data.domain.Page;

import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.user.User;

public interface ResvOperService {
	
	Page<ServiceOperator> getOperList(BaseRequestDTO<ServiceOperator> baseRequestDTO);	//预约服务人员列表
	
	List<User> getUserListByTel(BaseRequestDTO<String> baseRequestDTO);

	void saveResvOper(BaseRequestDTO<ServiceOperator> baseRequestDTO);
	
	List<String> getOperServedSect(String operatorId);

	void deleteOperator(BaseRequestDTO<String> baseRequestDTO);

}

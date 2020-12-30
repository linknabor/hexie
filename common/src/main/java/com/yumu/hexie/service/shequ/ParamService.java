package com.yumu.hexie.service.shequ;

import java.util.Map;

import com.yumu.hexie.model.user.User;

public interface ParamService {

	Map<String, String> getWuyeParam(User user);
	
	Map<String, String> getWuyeParamAsync(User user, String type);

	void updateSysParam();
}

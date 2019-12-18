package com.yumu.hexie.service.shequ;

import java.util.Map;

import com.yumu.hexie.model.user.User;

public interface ParamService {

	void cacheWuyeParam(User user, String infoId, String type);
	
	Map<String, String> getWuyeParamByUser(User user);

	void updateSysParam();
}

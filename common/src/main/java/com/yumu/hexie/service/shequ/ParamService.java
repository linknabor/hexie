package com.yumu.hexie.service.shequ;

import java.util.Map;

import com.yumu.hexie.model.user.User;

public interface ParamService {

	public void cacheParam(User user, String infoId, String type);
	
	public Map<String, String> getParamByUser(User user);
}

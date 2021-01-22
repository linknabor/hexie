package com.yumu.hexie.service.oper;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.oper.vo.QueryOperVO;
import com.yumu.hexie.model.user.User;

public interface OperService {

	void authorize(User user, String sectIds, String timestamp, String type);
	
	void cancelAuthorize(QueryOperVO queryOperVO);
	
	CommonResponse<Object> getOperList(QueryOperVO queryOperVO);

	CommonResponse<Object> getRegionList(QueryOperVO queryOperVO);
	
}

package com.yumu.hexie.service.oper;

import java.util.List;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.oper.mapper.QueryOperRegionMapper;
import com.yumu.hexie.integration.oper.vo.QueryOperVO;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.vo.OperAuthorization;

public interface OperService {

	void authorize(User user, OperAuthorization oa) throws Exception;
	
	void cancelAuthorize(QueryOperVO queryOperVO) throws Exception;
	
	CommonResponse<Object> getOperList(QueryOperVO queryOperVO);

	CommonResponse<Object> getRegionList(QueryOperVO queryOperVO);

	List<QueryOperRegionMapper> getRegionListMobile(User user, String type) throws Exception;
	
}

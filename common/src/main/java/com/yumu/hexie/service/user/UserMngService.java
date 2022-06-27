package com.yumu.hexie.service.user;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.vo.req.QueryWuyeUserReq;

public interface UserMngService {

	CommonResponse<Object> getUserList(QueryWuyeUserReq queryWuyeUserReq);

	CommonResponse<Object> getByWuyeIds(QueryWuyeUserReq queryWuyeUserReq);
}

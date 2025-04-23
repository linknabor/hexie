package com.yumu.hexie.service.shequ;

import com.yumu.hexie.integration.wuye.resp.UserAccessSpotResp;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.req.UserAccessRecordReq;

public interface UserAccessService {

	UserAccessSpotResp getAccessSpot(User user, String spotId) throws Exception;
	
	void saveAccessRecord(User user, UserAccessRecordReq userAccessRecordReq) throws Exception;

}

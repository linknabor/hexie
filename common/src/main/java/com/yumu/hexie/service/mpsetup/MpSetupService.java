package com.yumu.hexie.service.mpsetup;

import com.yumu.hexie.service.mpsetup.req.MpQueryReq;
import com.yumu.hexie.service.mpsetup.req.MpSetupReq;
import com.yumu.hexie.service.mpsetup.resp.MpQueryResp;

public interface MpSetupService {
	
	public void saveMp(MpSetupReq MpSetupReq) throws Exception;

	MpQueryResp queryMp(MpQueryReq mpQueryReq) throws Exception;


}

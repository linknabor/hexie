package com.yumu.hexie.service.mpsetup;

import com.yumu.hexie.service.mpsetup.req.MpQueryReq;
import com.yumu.hexie.service.mpsetup.req.MpSetupReq;

public interface MpSetupService {
	
	public void saveMp(MpSetupReq MpSetupReq) throws Exception;

	String queryMp(MpQueryReq mpQueryReq) throws Exception;


}

package com.yumu.hexie.service.common;

import com.yumu.hexie.vo.RgroupVO;

public interface RgroupV3Service {


	long saveRgroup(RgroupVO createRgroupReq);
	
	void pubRgroup(String rgroupRuleId);

	RgroupVO queryRgroupByRule(String rgroupRuleId, boolean isOnsale);
	
}

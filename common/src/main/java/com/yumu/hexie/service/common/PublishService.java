package com.yumu.hexie.service.common;

import com.yumu.hexie.vo.CreateRgroupReq;

public interface PublishService {


	void saveRgroup(CreateRgroupReq createRgroupReq);
	
	public void pubRgroup(long rgroupRuleId);

	
}

package com.yumu.hexie.service.common;

import java.util.List;

import com.yumu.hexie.model.user.User;
import com.yumu.hexie.vo.RgroupRecordsVO;
import com.yumu.hexie.vo.RgroupVO;

public interface RgroupV3Service {


	long saveRgroup(RgroupVO createRgroupReq);
	
	RgroupVO queryRgroupByRule(String rgroupRuleId, boolean isOnsale);

	void updateRgroupStatus(long ruleId, boolean isPub);

	List<RgroupVO> queryOwnerRgroups(User user, String title, int currentPage);

	RgroupRecordsVO queryOrderRecords(String ruleId, int currentPage);
	
}

package com.yumu.hexie.service.repair;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.repair.vo.QueryRAreaVO;
import com.yumu.hexie.integration.repair.vo.SaveRAreaVO;

public interface RepairAreaService {
	
	CommonResponse<Object> getRepairArea(QueryRAreaVO vo);
	
	void saveRepairArea(SaveRAreaVO SaveRAreaVO);
	
}

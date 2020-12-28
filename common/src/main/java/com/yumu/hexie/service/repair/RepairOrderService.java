package com.yumu.hexie.service.repair;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.repair.vo.QueryROrderVO;

public interface RepairOrderService {

	CommonResponse<Object> getOrder(QueryROrderVO queryROrderVO);

}

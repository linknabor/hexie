package com.yumu.hexie.service.workorder;

import com.yumu.hexie.integration.workorder.resp.WorkOrdersVO;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.workorder.req.WorkOrderReq;

public interface WorkOrderService {

	void addWorkOrder(User user, WorkOrderReq workOrderReq) throws Exception;

	WorkOrdersVO queryWorkOrder(User user) throws Exception;

}

package com.yumu.hexie.service.workorder;

import com.yumu.hexie.integration.workorder.resp.OrderDetailVO;
import com.yumu.hexie.integration.workorder.resp.WorkOrderServiceVO;
import com.yumu.hexie.integration.workorder.resp.WorkOrdersVO;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.workorder.req.WorkOrderReq;

public interface WorkOrderService {

	void addWorkOrder(User user, WorkOrderReq workOrderReq) throws Exception;

	WorkOrdersVO queryWorkOrder(User user) throws Exception;

	OrderDetailVO getOrderDetail(User user, String orderId) throws Exception;

	void reverseOrder(User user, String orderId, String reason) throws Exception;

	WorkOrderServiceVO getService(User user, String sectId) throws Exception;

}

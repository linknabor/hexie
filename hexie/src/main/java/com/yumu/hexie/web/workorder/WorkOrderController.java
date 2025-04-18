package com.yumu.hexie.web.workorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.workorder.resp.OrderDetailVO;
import com.yumu.hexie.integration.workorder.resp.WorkOrderServiceVO;
import com.yumu.hexie.integration.workorder.resp.WorkOrdersVO;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.repair.RepairService;
import com.yumu.hexie.service.workorder.WorkOrderService;
import com.yumu.hexie.service.workorder.req.WorkOrderReq;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@RestController
@RequestMapping(value = "/workorder")
public class WorkOrderController extends BaseController {
	
	@Autowired
	private RepairService repairService;
	
	@Autowired
	private WorkOrderService workOrderService;

	/**
	 * 获取默认绑定房屋
	 * @param user
	 * @return
	 */
    @RequestMapping(value="/address/default", method = RequestMethod.GET)
    public BaseResult<Address> queryProject(@ModelAttribute(Constants.USER)User user){
        /*
         * 1.维修只能修业主绑定的房子，自己添加的小区以外的地址物业服务不到。
         * 2.进入到这个功能的用户应该都是已经绑定过房子的用户。需要去community查询他具体绑定的房屋地址，并在address表对应的记录上添加相应的标识
         * 3.address表上已经有标识的用户，不用去community查询。
         * 4.在所有绑定房屋的功能中，都将添加bind这个标识来标记address是否是合协社区的房子
         */
        Address address = repairService.getDefaultAddress(user);
        return new BaseResult<Address>().success(address);
    }
    
    /**
     * 业主添加新的工单
     * @param user
     * @param workOrderReq
     * @return
     * @throws Exception 
     */
    @RequestMapping(value="/save", method = RequestMethod.POST)
    public BaseResult<String> save(@ModelAttribute(Constants.USER)User user, 
    		WorkOrderReq workOrderReq) throws Exception{
    	
    	workOrderService.addWorkOrder(user, workOrderReq);
        return new BaseResult<String>().success(Constants.PAGE_SUCCESS);
    }
    
    /**
     * 工单查询
     * @param user
     * @param workOrderReq
     * @return
     * @throws Exception 
     */
    @RequestMapping(value="/orderList", method = RequestMethod.GET)
    public BaseResult<WorkOrdersVO> orderList(@ModelAttribute(Constants.USER)User user) throws Exception{
    	
    	WorkOrdersVO vo = workOrderService.queryWorkOrder(user);
        return new BaseResult<WorkOrdersVO>().success(vo);
    }
    
    /**
	 * 查询工单
	 * @param user
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/orderDetail/{orderId}", method = RequestMethod.GET)
	public BaseResult<OrderDetailVO> orderDetail(@ModelAttribute(name = Constants.USER) User user, 
			@PathVariable String orderId) throws Exception {
		
		OrderDetailVO vo = workOrderService.getOrderDetail(user, orderId);
		return BaseResult.successResult(vo);
	}
	
	/**
	 * 撤消工单
	 * @param user
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/reverse/{orderId}", method = RequestMethod.GET)
	public BaseResult<String> reverse(@ModelAttribute(name = Constants.USER) User user, 
			@PathVariable String orderId, String reason) throws Exception {
		
		workOrderService.reverseOrder(user, orderId, reason);
		return BaseResult.successResult(Constants.PAGE_SUCCESS);
	}
	
	/**
	 * 获取工单服务（如果用户当前所在小区支持工单服务，则显示该用户绑定的房屋，否则）
	 * @param user
	 * @param sectId 可选，如果用户页面没选房子，则取用户默认绑定房子所在小区
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/service", method = RequestMethod.GET)
	public BaseResult<WorkOrderServiceVO> queryService(@ModelAttribute(name = Constants.USER) User user, @RequestParam(required = false) String sectId) throws Exception {
		
		WorkOrderServiceVO workOrderServiceVO = workOrderService.getService(user, sectId);
		return BaseResult.successResult(workOrderServiceVO);
	}
	
}

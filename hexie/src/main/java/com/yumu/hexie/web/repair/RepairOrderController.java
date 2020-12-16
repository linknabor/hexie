package com.yumu.hexie.web.repair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.repair.vo.QueryROrderVO;
import com.yumu.hexie.service.repair.RepairOrderService;
import com.yumu.hexie.web.BaseController;

/**
 * NewRepairController是servplat查询hexie、设置操作员等操作，这个类是backmng查询hexie的订单,只有查询查询操作
 * 以后servplat查询hexie，也改为这个类里调用service的模式 TODO
 * @author david
 *
 */
@RestController
@RequestMapping("/repair/order")
public class RepairOrderController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(RepairOrderController.class);
	
	@Autowired
	private RepairOrderService repairOrderService;
	
	/**
	 * 获取维修订单列表
	 * @return 
	 */
	@RequestMapping("/get")
	public CommonResponse<Object> orderList(@RequestBody QueryROrderVO queryROrderVO) {
		
		logger.info("queyrOrderVo : " + queryROrderVO);
		return repairOrderService.getOrder(queryROrderVO);
		
	}
}

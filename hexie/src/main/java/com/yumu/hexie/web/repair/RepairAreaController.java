package com.yumu.hexie.web.repair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.repair.vo.QueryRAreaVO;
import com.yumu.hexie.integration.repair.vo.SaveRAreaVO;
import com.yumu.hexie.service.repair.RepairAreaService;

@RestController
@RequestMapping(value = "/servplat/repairArea")
public class RepairAreaController {

	@Autowired
	private RepairAreaService repairAreaService;
	
	/**
	 * 管理端获取有维修服务的区域
	 * @param baseRequestDTO
	 * @return
	 */
	@RequestMapping(value = "/get", method = RequestMethod.POST)
	public CommonResponse<Object> getServiceArea(@RequestBody QueryRAreaVO queryRAreaVO) {
		
		return repairAreaService.getRepairArea(queryRAreaVO);
	}
	
	/**
	 * 管理端新增维修区域
	 * @param baseRequestDTO
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public CommonResponse<String> saveRepairArea(@RequestBody SaveRAreaVO saveRAreaVO) {
		
		repairAreaService.saveRepairArea(saveRAreaVO);
		CommonResponse<String> commonResponse = new CommonResponse<>();
		commonResponse.setResult("00");
		return commonResponse;
		
	}
	
	
}

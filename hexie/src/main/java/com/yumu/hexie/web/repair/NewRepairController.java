package com.yumu.hexie.web.repair;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.integration.wuye.resp.BaseResponse;
import com.yumu.hexie.integration.wuye.resp.BaseResponseDTO;
import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.service.exception.IntegrationBizException;
import com.yumu.hexie.service.repair.RepairService;
import com.yumu.hexie.web.BaseController;

@Controller
@RequestMapping(value = "/servplat/repair")
public class NewRepairController extends BaseController{
	
	@Autowired
	private RepairService repairService;
	
	@RequestMapping(value = "/getRepairOderList", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResponseDTO<Map<String,Object>> getRepairOderList(@RequestBody BaseRequestDTO<Map<String,String>> baseRequestDTO) {
		Map<String,Object> map=new HashMap<>();
		try {
		Page<RepairOrder> page=repairService.getRepairOderList(baseRequestDTO);
		map.put("count", page.getTotalElements());
		map.put("list", page.getContent());
		} catch (Exception e) {
			throw new IntegrationBizException(e.getMessage(), e, baseRequestDTO.getRequestId());
		}
		return BaseResponse.success(baseRequestDTO.getRequestId(), map);
	}
	
	@RequestMapping(value = "/getServiceoperator", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResponseDTO<Map<String,Object>> getServiceoperator(@RequestBody BaseRequestDTO<Map<String,String>> baseRequestDTO) {
		Map<String,Object> map=new HashMap<>();
		try {
			Page<Object>  page=repairService.getServiceoperator(baseRequestDTO);
			map.put("count", page.getTotalElements());
			map.put("list", page.getContent());
		} catch (Exception e) {
			throw new IntegrationBizException(e.getMessage(), e, baseRequestDTO.getRequestId());
		}
		return BaseResponse.success(baseRequestDTO.getRequestId(), map);
	}
	
	@RequestMapping(value = "/saveRepiorOperator", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResponseDTO<Integer> saveRepiorOperator(@RequestBody BaseRequestDTO<Map<String,String>> baseRequestDTO) {
		int r=0;
		try {
			 r=repairService.saveRepiorOperator(baseRequestDTO);
		} catch (Exception e) {
			throw new IntegrationBizException(e.getMessage(), e, baseRequestDTO.getRequestId());
		}
		return BaseResponse.success(baseRequestDTO.getRequestId(), r);
	}
	
	@RequestMapping(value = "/operatorInfo", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResponseDTO<Map<String,Object>> operatorInfo(@RequestBody BaseRequestDTO<String> baseRequestDTO) {
		Map<String,Object> map=null;		
		try {
			 map=repairService.operatorInfo(baseRequestDTO);
		} catch (Exception e) {
			throw new IntegrationBizException(e.getMessage(), e, baseRequestDTO.getRequestId());
		}
		return BaseResponse.success(baseRequestDTO.getRequestId(),map);
	}
	
	@RequestMapping(value = "/deleteOperator", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResponseDTO<Integer> deleteOperator(@RequestBody BaseRequestDTO<Map<String,String>> baseRequestDTO) {
		try {
			repairService.deleteOperator(baseRequestDTO);
		} catch (Exception e) {
			throw new IntegrationBizException(e.getMessage(), e, baseRequestDTO.getRequestId());
		}
		return BaseResponse.success(baseRequestDTO.getRequestId());
	}
	
	@RequestMapping(value = "/checkTel", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResponseDTO<Integer> checkTel(@RequestBody BaseRequestDTO<String> baseRequestDTO) {
		int r=0;
		try {
			 r=repairService.checkTel(baseRequestDTO);
		} catch (Exception e) {
			throw new IntegrationBizException(e.getMessage(), e, baseRequestDTO.getRequestId());
		}
		return BaseResponse.success(baseRequestDTO.getRequestId(), r);
	}
	
}

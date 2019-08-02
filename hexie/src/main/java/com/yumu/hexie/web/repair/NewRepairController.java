package com.yumu.hexie.web.repair;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.integration.wuye.resp.BaseResponseDTO;
import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.service.repair.RepairService;
import com.yumu.hexie.web.BaseController;

@Controller
public class NewRepairController extends BaseController{
	
	@Autowired
	private RepairService repairService;
	
	@RequestMapping(value = "/servplat/repair/getRepairOderList", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResponseDTO<List<RepairOrder>> getRepairOderList(@RequestBody BaseRequestDTO<Map<String,String>> baseRequestDTO) {
		BaseResponseDTO<List<RepairOrder>> result=new BaseResponseDTO<>();
		if("hexie-servplat".equals(baseRequestDTO.getSign())){
			Page<RepairOrder> page=repairService.getRepairOderList(baseRequestDTO);
			result.setTotal_size((int)page.getTotalElements());
			result.setData(page.getContent());
		}
		return result;
	}
	
	@RequestMapping(value = "/servplat/repair/getServiceoperator", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResponseDTO<List<Object>> getServiceoperator(@RequestBody BaseRequestDTO<Map<String,String>> baseRequestDTO) {
		BaseResponseDTO<List<Object>> result=new BaseResponseDTO<>();
		if("hexie-servplat".equals(baseRequestDTO.getSign())){
			Page<Object> page=repairService.getServiceoperator(baseRequestDTO);
			result.setTotal_size((int)page.getTotalElements());
			result.setData(page.getContent());
		}
		return result;
	}
	
	@RequestMapping(value = "/servplat/repair/saveRepiorOperator", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResponseDTO<Integer> saveRepiorOperator(@RequestBody BaseRequestDTO<Map<String,String>> baseRequestDTO) {
		BaseResponseDTO<Integer> result=new BaseResponseDTO<>();
		if("hexie-servplat".equals(baseRequestDTO.getSign())){
			int r=repairService.saveRepiorOperator(baseRequestDTO);
			result.setData(r);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servplat/repair/operatorInfo", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResponseDTO<ServiceOperator> operatorInfo(@RequestBody BaseRequestDTO<String> baseRequestDTO) {
		BaseResponseDTO<ServiceOperator> result=new BaseResponseDTO<>();
		if("hexie-servplat".equals(baseRequestDTO.getSign())){
			Map<String,Object> map=repairService.operatorInfo(baseRequestDTO);
			result.setSectList((List<String>)map.get("sectList"));
			result.setData((ServiceOperator)map.get("serviceOperator"));
		}
		return result;
	}
	
	@RequestMapping(value = "/servplat/repair/deleteOperator", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResponseDTO<Integer> deleteOperator(@RequestBody BaseRequestDTO<Map<String,String>> baseRequestDTO) {
		BaseResponseDTO<Integer> result=new BaseResponseDTO<>();
		if("hexie-servplat".equals(baseRequestDTO.getSign())){
			repairService.deleteOperator(baseRequestDTO);
		}
		return result;
	}
	
	@RequestMapping(value = "/servplat/repair/checkTel", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResponseDTO<Integer> checkTel(@RequestBody BaseRequestDTO<String> baseRequestDTO) {
		BaseResponseDTO<Integer> result=new BaseResponseDTO<>();
		if("hexie-servplat".equals(baseRequestDTO.getSign())){
			int r=repairService.checkTel(baseRequestDTO);
			result.setData(r);
		}
		return result;
	}
	
}

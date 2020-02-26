package com.yumu.hexie.web.health;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.integration.wuye.resp.BaseResponse;
import com.yumu.hexie.integration.wuye.resp.BaseResponseDTO;
import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.health.ResvOperService;
import com.yumu.hexie.web.BaseController;

@RestController
@RequestMapping(value = "/servplat/resvOper")
public class ResvOperController extends BaseController {

	@Autowired
	private ResvOperService resvOperService;

	/**
	 * 预约服务人员列表
	 * 
	 * @param baseRequestDTO
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public BaseResponseDTO<?> serviceResv(@RequestBody BaseRequestDTO<ServiceOperator> baseRequestDTO) {

		Page<ServiceOperator> page = resvOperService.getOperList(baseRequestDTO);
		return BaseResponse.success(baseRequestDTO.getRequestId(), page);

	}

	@RequestMapping(value = "/getByTel", method = RequestMethod.POST)
	public BaseResponseDTO<?> getUserByTel(@RequestBody BaseRequestDTO<String> baseRequestDTO) {
		
		List<User> userList= resvOperService.getUserListByTel(baseRequestDTO);
		return BaseResponse.success(baseRequestDTO.getRequestId(),userList);
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public BaseResponseDTO<?> saveResvOper(@RequestBody BaseRequestDTO<ServiceOperator> baseRequestDTO) {
		
		resvOperService.saveResvOper(baseRequestDTO);
		return BaseResponse.success(baseRequestDTO.getRequestId(),"success");
	}
	
	@RequestMapping(value = "/servedSect", method = RequestMethod.POST)
	public BaseResponseDTO<?> servedSect(@RequestBody BaseRequestDTO<String> baseRequestDTO) {
		
		List<String> list = resvOperService.getOperServedSect(baseRequestDTO.getData());
		return BaseResponse.success(baseRequestDTO.getRequestId(), list);
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseDTO<Integer> deleteOperator(@RequestBody BaseRequestDTO<String> baseRequestDTO) {
		
		resvOperService.deleteOperator(baseRequestDTO);
		return BaseResponse.success(baseRequestDTO.getRequestId());
	}
	
}

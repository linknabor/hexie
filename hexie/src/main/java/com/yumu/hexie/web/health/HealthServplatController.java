package com.yumu.hexie.web.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.integration.wuye.resp.BaseResponse;
import com.yumu.hexie.integration.wuye.resp.BaseResponseDTO;
import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.model.community.Thread;
import com.yumu.hexie.service.health.HealthService;

@RestController
@RequestMapping(value = "/servplat/health")
public class HealthServplatController {

	@Autowired
	private HealthService healthService;
	
	/**
	 * 获取健康报告
	 * @param baseRequestDTO
	 * @return
	 */
	@RequestMapping(value = "/report", method = RequestMethod.POST)
	public BaseResponseDTO<?>  report(@RequestBody BaseRequestDTO<Thread> baseRequestDTO) {
		
		Page<Thread> page = healthService.getHealthReport(baseRequestDTO);
		return BaseResponse.success(baseRequestDTO.getRequestId(), page);
		
	}
	
	
	/**
	 * 获取口罩预约情况
	 * @param baseRequestDTO
	 * @return
	 */
	@RequestMapping(value = "/maskResv", method = RequestMethod.POST)
	public BaseResponseDTO<?>  maskResv(@RequestBody BaseRequestDTO<Thread> baseRequestDTO) {
		
		Page<Thread> page = healthService.getMaskReservation(baseRequestDTO);
		return BaseResponse.success(baseRequestDTO.getRequestId(), page);
		
	}
	
}

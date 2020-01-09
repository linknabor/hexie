package com.yumu.hexie.web.common;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.model.maintenance.MaintenanceService;
import com.yumu.hexie.model.maintenance.vo.MaintenanceVO;
import com.yumu.hexie.web.BaseController;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController extends BaseController {

	@Autowired
	private MaintenanceService maintenanceService;

	/**
	 * 查询当前服务器队列开关状态
	 * 
	 * @param httpServletRequest
	 * @param sysCode
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "/queueSwitch", method = RequestMethod.GET)
	public String getSwitch(@RequestParam String sysCode) throws JsonProcessingException {

		if (StringUtils.isEmpty(sysCode)) {
			return "";
		}
		if (!"hexie".equals(sysCode)) {
			return "";
		}
		Map<Object, Object> map = maintenanceService.getQueueSwitch();
		String json = JacksonJsonUtil.getMapperInstance(false).writeValueAsString(map);
		return json;
	}

	/**
	 * 更新当前服务器队列开关状态
	 * 
	 * @param httpServletRequest
	 * @param sysCode
	 * @param maintenanceVO
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "/queueSwitch", method = RequestMethod.POST)
	public String getSwitch(@RequestParam(required = false) String sysCode, @RequestBody MaintenanceVO maintenanceVO)
			throws JsonProcessingException {

		if (StringUtils.isEmpty(sysCode)) {
			return "";
		}
		if (!"hexie".equals(sysCode)) {
			return "";
		}
		Map<Object, Object> map = maintenanceService.updateQueueSwitch(maintenanceVO);
		String json = JacksonJsonUtil.getMapperInstance(false).writeValueAsString(map);
		return json;
	}

}

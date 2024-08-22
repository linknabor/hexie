package com.yumu.hexie.backend.web;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.backend.web.dto.BaseResult;
import com.yumu.hexie.service.ScheduleService;

@RestController
@RequestMapping("/schedule/manual")
public class ScheduleController extends BaseController {

	@Resource
	private ScheduleService scheduleService;
	
	@GetMapping("/syncWestMiniData")
	public BaseResult<String> syncWestMiniData() {
		
		scheduleService.updatePageView();
		scheduleService.westDataBatch();
		scheduleService.westData2Beyondsoft();
		return BaseResult.successResult("success");
	}
}

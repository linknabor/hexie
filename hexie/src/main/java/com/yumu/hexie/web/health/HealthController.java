/**
 * 
 */
package com.yumu.hexie.web.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.model.community.Thread;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.health.HealthService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

/**
 * 肺炎疫情相关
 * @author david
 *
 */
@RestController
@RequestMapping(value = "/health")
public class HealthController extends BaseController {
	
	@Autowired
	private HealthService healthService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/report", method = RequestMethod.POST)
	public BaseResult<String> report(@ModelAttribute(Constants.USER) User user, @RequestBody Thread thread) {
		
		healthService.healthReport(user, thread);
		return BaseResult.successResult("success");
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/maskResv", method = RequestMethod.POST)
	public BaseResult<String> maskResv(@ModelAttribute(Constants.USER) User user, @RequestBody Thread thread) {
		
		healthService.maskReservation(user, thread);
		return BaseResult.successResult("success");
	}
	
	@RequestMapping(value = "/testTemplate", method = RequestMethod.GET)
	public String testTemplate(@ModelAttribute(Constants.USER) User user) {
		
		healthService.testTemplate(user);
		return "ok";
	}
	
}

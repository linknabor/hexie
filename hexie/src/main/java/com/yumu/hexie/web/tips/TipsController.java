package com.yumu.hexie.web.tips;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.page.PageConfigService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@RestController
@RequestMapping(value = "/tips")
public class TipsController extends BaseController {

	@Autowired
	private PageConfigService pageConfigService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/bindHouse", method = RequestMethod.GET)
    public BaseResult<String> getBindHouseTips(@ModelAttribute(Constants.USER)User user) throws Exception {
		String tips = pageConfigService.getBindHouseTips(user);
		return BaseResult.successResult(tips);
	}
}

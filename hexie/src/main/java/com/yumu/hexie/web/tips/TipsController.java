package com.yumu.hexie.web.tips;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
	@RequestMapping(value = "/switchSect/{page}", method = RequestMethod.GET)
    public BaseResult<String> getSwitchSectTips(@ModelAttribute(Constants.USER)User user, @PathVariable String page) throws Exception {
		String tips = pageConfigService.getSwtichSectTips(user, page);
		return BaseResult.successResult(tips);
	}
}

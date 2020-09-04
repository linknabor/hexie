/**
 * 
 */
package com.yumu.hexie.web.eshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.eshop.PromotionService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

/**
 * @author david
 *
 */
@RestController
@RequestMapping(value = "/promotion")
public class PromotionController extends BaseController {
	
	@Autowired
	private PromotionService promotionService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/password/reset", method = RequestMethod.GET)
	public BaseResult<String> resetPassword(@ModelAttribute(Constants.USER)User user, @RequestParam String vericode) throws Exception{
		
		promotionService.resetPassword(user, vericode);
		return BaseResult.successResult(Constants.PAGE_SUCCESS);
		
	}
	
	
}

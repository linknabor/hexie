package com.yumu.hexie.web.receipt;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

/**
 * 微信支付成功后的小票页面处理
 * @author david
 *
 */
@RestController(value = "receiptController")
public class ReceiptController extends BaseController {

	@RequestMapping(value = "/receipt/{outTradeNo}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<String> receipt(HttpSession httpSession, @PathVariable String outTradeNo) throws Exception {
		
		User user = new User();
		if (httpSession != null) {
			user = (User) httpSession.getAttribute(Constants.USER);
		}
		return new BaseResult<String>().success(user.toString());
    }
	
}

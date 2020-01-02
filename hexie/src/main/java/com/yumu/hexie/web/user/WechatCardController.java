package com.yumu.hexie.web.user;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.integration.wechat.entity.card.PreActivateReq;
import com.yumu.hexie.integration.wechat.entity.common.JsSign;
import com.yumu.hexie.service.card.WechatCardService;
import com.yumu.hexie.service.common.WechatCoreService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@RequestMapping(value = "/card")
@RestController
public class WechatCardController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(WechatCardController.class);
	
	@Autowired
	private WechatCardService wechatCardService;
	
	@Autowired
	 private WechatCoreService wechatCoreService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/preActivate", method = RequestMethod.POST)
	public BaseResult<JsSign> preActivate(HttpServletRequest request,
			@RequestParam(name="card_id", required = true) String cardId,
			@RequestParam(name="encrypt_code", required = true) String encryptCode,
			@RequestParam(name="openid", required = true) String openid,
			@RequestParam(name="outer_str", required = false) String outerStr,
			@RequestParam(name="activate_ticket", required = true) String activateTicket) {
		
		PreActivateReq preActivateReq = new PreActivateReq();
		preActivateReq.setCardId(cardId);
		preActivateReq.setEncryptCode(encryptCode);
		preActivateReq.setOpenid(openid);
		preActivateReq.setOuterStr(outerStr);
		preActivateReq.setActivateTicket(activateTicket);
		logger.info("preActivateReq is : " + preActivateReq);
		String appId = wechatCardService.acctivate(preActivateReq);
		
		String url = request.getRequestURL().toString();
		JsSign jsSign = wechatCoreService.getJsSign(url, appId);
		return BaseResult.successResult(jsSign);
		
	}
	
	
}

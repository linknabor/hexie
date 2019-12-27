package com.yumu.hexie.web.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.integration.wechat.entity.card.PreActivateReq;
import com.yumu.hexie.service.user.WechatCardService;
import com.yumu.hexie.web.BaseController;

@RequestMapping(value = "/card")
@RestController
public class WechatCardController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(WechatCardController.class);
	
	private static WechatCardService wechatCardService;
	
	@RequestMapping(value = "/preActivate", method = RequestMethod.POST)
	public void preActivate(@RequestBody PreActivateReq preActivateReq) {
		
		logger.info("preActivateReq is : " + preActivateReq);
		wechatCardService.activateCard(preActivateReq);
		
	}
	
	
}

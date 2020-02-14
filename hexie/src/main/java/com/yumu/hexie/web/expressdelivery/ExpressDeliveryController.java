package com.yumu.hexie.web.expressdelivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.service.expressdelivery.ExpressDeliveryService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.expressdelivery.resp.ExpressDeliveryVO;

@RestController
@RequestMapping(value = "/servplat/express")
public class ExpressDeliveryController extends BaseController{
	
	private static final Logger log = LoggerFactory.getLogger(ExpressDeliveryController.class);
	
	@Autowired
	private ExpressDeliveryService expressDeliveryService;
	
	@RequestMapping(value = "/pullWechat", method = RequestMethod.POST)
	public String pullWechat(@RequestBody ExpressDeliveryVO expr) {
		log.info("pullWechat:--wuyeId:"+expr.getWuyeId()+"---type:"+expr.getType());
		expressDeliveryService.pullWechat(expr.getWuyeId(),expr.getType());
		return "ok";
	}
}

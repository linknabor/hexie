package com.yumu.hexie.web.expressdelivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.service.expressdelivery.ExpressDeliveryService;
import com.yumu.hexie.web.BaseController;

@RestController
@RequestMapping(value = "/servplat/express")
public class ExpressDeliveryController extends BaseController{
	
	private static final Logger log = LoggerFactory.getLogger(ExpressDeliveryController.class);
	
	@Autowired
	private ExpressDeliveryService expressDeliveryService;
	
	@RequestMapping(value = "/pullWechat", method = RequestMethod.POST)
	public String pullWechat(@RequestParam(required = false) String wuyeId,@RequestParam(required = false) String type) {
		log.info("pullWechat:--wuyeId:"+wuyeId+"---type:"+type);
		expressDeliveryService.pullWechat(wuyeId,type);
		return "ok";
	}
}

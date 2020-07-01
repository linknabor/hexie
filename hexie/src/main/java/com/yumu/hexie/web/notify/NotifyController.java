package com.yumu.hexie.web.notify;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yumu.hexie.integration.notify.PayNotification;
import com.yumu.hexie.integration.notify.PayNotificationResponse;
import com.yumu.hexie.service.notify.NotifyService;
import com.yumu.hexie.web.BaseController;

@RestController
public class NotifyController extends BaseController {
	
	private static final Logger log = LoggerFactory.getLogger(NotifyController.class);
	
	@Autowired
	private NotifyService notifyService;

	/**
	 * 接收servplat过来的请求，消优惠券，增加积分
	 * @param user
	 * @param tradeWaterId
	 * @param feePrice
	 * @param couponId
	 * @param bindSwitch
	 * @param wuyeId
	 * @param cardNo
	 * @param quickToken
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws Exception
	 */
	@RequestMapping(value = "/servplat/noticeCardPay", method = {RequestMethod.GET, RequestMethod.POST})
	public String noticeCardPay(@RequestParam(required = false) String tradeWaterId,
			@RequestBody PayNotificationResponse<PayNotification> payNotificationResponse) throws Exception {
		
		log.info("payNotificationResponse :" + payNotificationResponse);
		if ("00".equals(payNotificationResponse.getResult())) {
			notifyService.notify(payNotificationResponse.getData());
		}else {
			log.error("result : " + payNotificationResponse.getResult() + ", data : " + payNotificationResponse.getData());
		}
		return "SUCCESS";
	}
	
	
}

package com.yumu.hexie.web.notify;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.notify.CommonNotificationResponse;
import com.yumu.hexie.integration.notify.PartnerNotification;
import com.yumu.hexie.integration.notify.PayNotification;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
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
			@RequestBody CommonNotificationResponse<PayNotification> commonNotificationResponse) throws Exception {
		
		log.info("payNotificationResponse :" + commonNotificationResponse);
		if ("00".equals(commonNotificationResponse.getResult())) {
			notifyService.notify(commonNotificationResponse.getData());
		}else {
			log.error("result : " + commonNotificationResponse.getResult() + ", data : " + commonNotificationResponse.getData());
		}
		return "SUCCESS";
	}
	
	@RequestMapping(value = "/promotion/partner/update", method = RequestMethod.POST )
	public <T> BaseResult<String> updatePartner(@RequestBody CommonNotificationResponse<List<PartnerNotification>> commonNotificationResponse) throws Exception {
		
		log.info("updatePartner :" + commonNotificationResponse);
		notifyService.updatePartner(commonNotificationResponse.getData());
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setResult("00");
		baseResult.setData(Constants.SERVICE_SUCCESS);
		return baseResult;
	}
	
	@RequestMapping(value = "/eshop/notifyRefund", method = RequestMethod.POST )
	public String notifyRefund(@RequestBody Map<String, String> map) throws Exception {
		
		log.info("notifyRefund :" + map);
		notifyService.notifyEshopRefund(map.get("trade_water_id"));
		return "SUCCESS";
	}
	
}

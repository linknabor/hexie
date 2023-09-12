package com.yumu.hexie.web.notify;

import java.util.List;
import java.util.Map;

import com.yumu.hexie.service.shequ.vo.InteractCommentNotice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.notify.CommonNotificationResponse;
import com.yumu.hexie.integration.notify.ConversionNotification;
import com.yumu.hexie.integration.notify.InvoiceNotification;

import com.yumu.hexie.integration.notify.PartnerNotification;
import com.yumu.hexie.integration.notify.PayNotification;
import com.yumu.hexie.integration.notify.ReceiptNotification;
import com.yumu.hexie.integration.notify.WorkOrderNotification;
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
	 * @param tradeWaterId
	 * @param commonNotificationResponse
	 * @return
	 */
	@RequestMapping(value = "/servplat/noticeCardPay", method = {RequestMethod.GET, RequestMethod.POST})
	public String noticeCardPay(@RequestParam(required = false) String tradeWaterId,
			@RequestBody CommonNotificationResponse<PayNotification> commonNotificationResponse) {
		
		log.info("payNotificationResponse :" + commonNotificationResponse);
		if ("00".equals(commonNotificationResponse.getResult())) {
			notifyService.notify(commonNotificationResponse.getData());
		}else {
			log.error("result : " + commonNotificationResponse.getResult() + ", data : " + commonNotificationResponse.getData());
		}
		return "SUCCESS";
	}
	
	@RequestMapping(value = "/promotion/partner/update", method = RequestMethod.POST )
	public BaseResult<String> updatePartner(@RequestBody CommonNotificationResponse<List<PartnerNotification>> commonNotificationResponse) throws Exception {
		
		log.info("updatePartner :" + commonNotificationResponse);
		notifyService.updatePartner(commonNotificationResponse.getData());
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setResult("00");
		baseResult.setData(Constants.SERVICE_SUCCESS);
		return baseResult;
	}
	
	@RequestMapping(value = "/eshop/notifyRefund", method = RequestMethod.POST )
	public String notifyRefund(@RequestBody Map<String, Object> map) {
		
		log.info("notifyRefund :" + map);
		notifyService.notifyEshopRefund(map);
		return "SUCCESS";
	}
	
	/**
	 * 工单消息通知
	 * @param workOrderNotification
	 * @return
	 */
	@RequestMapping(value = "/workorder/notication", method = RequestMethod.POST )
	public String notifyRefund(@RequestBody WorkOrderNotification workOrderNotification) {
		
		log.info("workOrderNotification :" + workOrderNotification);
		notifyService.notifyWorkOrderMsgAsync(workOrderNotification);
		return "SUCCESS";
	}
	
	/**
	 * 工单消息通知
	 * @param conversionNotification
	 * @return
	 */
	@RequestMapping(value = "/conversion/notication", method = RequestMethod.POST )
	public String notifyRefund(@RequestBody ConversionNotification conversionNotification) {
		
		log.info("conversionNotification :" + conversionNotification);
		notifyService.notifyConversionAsync(conversionNotification);
		return "SUCCESS";
	}
	
	/**
	 * 发票开具成功消息通知
	 * @param invoiceNotification
	 * @return
	 */
	@RequestMapping(value = "/invoice/notification", method = RequestMethod.POST )
	public String notifyInvoice(@RequestBody InvoiceNotification invoiceNotification) {
		
		log.info("invoiceNotification :" + invoiceNotification);
		notifyService.notifyInvoiceMsgAsync(invoiceNotification);
		return "SUCCESS";
	}

	/**
	 * 电子收据开具成功消息通知
	 * @param receiptNotification
	 * @return
	 */
	@RequestMapping(value = "/receipt/notification", method = RequestMethod.POST )
	public String notifyReceipt(@RequestBody ReceiptNotification receiptNotification) {
		
		log.info("receiptNotification :" + receiptNotification);
		notifyService.sendReceiptMsgAsync(receiptNotification);
		return "SUCCESS";
	}
	
	
	/**
	 * 释放发票申请锁
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/invoice/application/release", method = RequestMethod.POST )
	public String releaseInvoiceApplication(@RequestBody Map<String, String> map) {
		
		log.info("releaseInvoiceApplication, map :" + map);
		notifyService.releaseInvoiceApplicationLock(map.get("trade_water_id"));
		return "SUCCESS";
	}

	/**
	 * 业主意见物业回复通知
	 * @param notice
	 * @return
	 */
	@RequestMapping(value = "/interact/noticeComment", method = RequestMethod.POST)
	@ResponseBody
	public String noticeComment(@RequestBody InteractCommentNotice notice) throws Exception {
		log.info("/interact/noticeComment notice:" + notice);
		notifyService.noticeComment(notice);
		return "SUCCESS";
	}

	@RequestMapping(value = "/interact/noticeEvaluate", method = RequestMethod.POST)
	@ResponseBody
	public String noticeEvaluate(@RequestBody InteractCommentNotice notice) throws Exception {
		log.info("/interact/noticeEvaluate notice:" + notice);
		notifyService.noticeEvaluate(notice);
		return "SUCCESS";
	}
	
}

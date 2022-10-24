package com.yumu.hexie.integration.wechat.service;

/**
 * 消息配置
 * @author david
 *
 */
public class MsgCfg {
	
	//模板消息
	public static String TEMPLATE_MSG = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";

	public static final String TEMPLATE_TYPE_PAY_SUCCESS = "paySuccessTemplate";
	public static final String TEMPLATE_TYPE_REG_SUCCESS = "registerSuccessTemplate";
	public static final String TEMPLATE_TYPE_WUYEPAY_SUCCESS = "wuyePaySuccessTemplate";
	public static final String TEMPLATE_TYPE_REPAIR_ASSIGN = "reapirAssginTemplate";
	public static final String TEMPLATE_TYPE_YUYUE_ASSGIN = "yuyueNoticeTemplate";
	public static final String TEMPLATE_TYPE_COMPLAIN = "complainTemplate";
	public static final String TEMPLATE_TYPE_SERVICE = "serviceTemplate";
	public static final String TEMPLATE_TYPE_MESSAGE = "messageTemplate";
	public static final String TEMPLATE_TYPE_PAY_NOTIFY = "payNotifyTemplate";
	public static final String TEMPLATE_TYPE_PAY_HOUSE_BIND_NOTIFY = "payNotify4HouseBinderTemplate";
	public static final String TEMPLATE_TYPE_CUSTOM_SERVICE_ASSGIN = "customServiceAssginTemplate";
	public static final String TEMPLATE_TYPE_RESET_PASSWORD = "resetPasswordTemplate";
	public static final String TEMPLATE_TYPE_DELIVERY_MESSAGE = "deliveryMessageTemplate";	//商家订单发货提醒
	public static final String TEMPLATE_TYPE_CUSTOMER_DELIVERY = "customerDeliveryTemplate";	//买家发货提醒
	public static final String TEMPLATE_TYPE_BILL_PUSH = "billPushTemplate"; //物业账单通知
	public static final String TEMPLATE_TYPE_OPINION_NOTIFY = "sendOpinionNotificationMessageTemplate";
	public static final String TEMPLATE_TYPE_WORKORDER_NOTIFY = "workOrderNotificationTemplate";
	public static final String TEMPLATE_TYPE_INVOICE_APPLICATION_REMINDER = "invoiceApplicationReminderTemplate";	//发票申请提醒
	public static final String TEMPLATE_TYPE_INVOICE_FINISH = "invoiceFinishTemplate";	//发票开具完成提醒
	public static final String TEMPLATE_TYPE_RECEIPT_APPLICATION_REMINDER = "receiptApplicationReminderTemplate";	//收据申请提醒
	public static final String TEMPLATE_TYPE_RECEIPT_FINISH = "receiptFinishTemplate";	//收据下发提醒
	public static final String TEMPLATE_TYPE_NOTICE_ARRIVAL = "noticeArrivalMessageTemplate";	//收据下发提醒
	public static final String TEMPLATE_TYPE_GROUP_SUCCESS_MESSAGE = "groupSuccessMessageTemplate";	//团购成团提醒
	public static final String TEMPLATE_TYPE_GROUP_START_MESSAGE = "groupStartMessageTemplate";	//开团提醒
	
	public static final String URL_SUCCESS = "successUrl";
	public static final String URL_REG_SUCCESS = "regSuccessUrl";
	public static final String URL_YUYUE_NOTICE = "yuyueNotice";
	public static final String URL_COMPLAIN_DETAIL = "complainDetail";
	public static final String URL_WEIXIU_NOTICE = "weixiuNotice";
	public static final String URL_XIYI_NOTICE = "weixiuNotice";
	public static final String URL_WEIXIU_DETAIL = "weixiuDetail";
	public static final String URL_SUBSCRIBE_IMG = "subscribeImage";
	public static final String URL_SUBSCRIBE_DETAIL = "subscribeDetail";
	public static final String URL_EXPRESS = "expressUrl";
	public static final String URL_MESSAGE = "messageUrl";
	public static final String URL_SERVICE_RESV = "serviceResvUrl";
	public static final String URL_PAY_NOTIFY = "payNotifyUrl";
	public static final String URL_PAY_HOUSE_BINDER_NOTIFY = "payNotifyUrl4HouseBinder";
	public static final String URL_CUSTOM_SERVICE_ASSIGN = "customServiceUrl";
	public static final String URL_CUSTOM_SERVICE_DETAIL = "customServiceDetail";
	public static final String URL_DELIVERY_DETAIL = "deliveryDetailUrl";
	public static final String URL_CUSTOMER_DELIVERY = "customerDeliveryUrl";
	public static final String URL_CUSTOMER_GROUP_DELIVERY = "customerGroupDeliveryUrl";
	public static final String URL_WUYE_PAY = "wuyePayUrl"; //物业缴费页面
	public static final String URL_OPINION_NOTICE = "sendOpinionUrl";
	public static final String URL_WORK_ORDER_DETAIL = "workOrderDetail";	//工单明细页面
	public static final String URL_INVOICE_APPLICATION_URL = "invoiceApplicationUrl";	//发票申请页面
	public static final String URL_RECEIPT_APPLICATION_URL = "receiptApplicationUrl";	//电子收据申请页面
	public static final String URL_RECEIPT_VIEW_URL = "receiptViewUrl";	//电子收据详情页面
	public static final String URL_GROUP_SUCCESS = "groupSuccessUrl";
	public static final String URL_LEADER_GROUP_START = "leaderGroupStartUrl";
	public static final String URL_REGION_GROUP_START = "sectGroupStartUrl";
	
	//订阅消息
	public static String SUBSCRIBE_MSG = "https://api.weixin.qq.com/cgi-bin/message/subscribe/bizsend?access_token=ACCESS_TOKEN";
	public static final String TEMPLATE_TYPE_SUBSCRIBE_PAY_NOTIFY = "payNotifySubscribeTemplate";
	public static final String TEMPLATE_TYPE_SUBSCRIBE_ORDER_NOTIFY = "orderNotifySubscribeTemplate";
	
}

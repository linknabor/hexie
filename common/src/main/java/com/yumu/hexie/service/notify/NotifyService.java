package com.yumu.hexie.service.notify;

import java.util.List;
import java.util.Map;

import com.yumu.hexie.integration.notify.*;
import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;

import com.yumu.hexie.service.shequ.vo.InteractCommentNotice;

public interface NotifyService {

	void notify(PayNotification payNotification);

	void sendPayNotificationAsync(AccountNotification accountNotification);

	void sendServiceNotificationAsync(String orderId);

	void updateServiceOrderStatusAsync(String orderId);

	void notifyDeliveryAsync(String orderId);
	
	void updatePartner(List<PartnerNotification> list);

	void notifyEshopRefund(Map<String, Object> map);

	void notifyWuyeCouponConsumeAsync(String orderId, String couponId);

	void sendPayNotification4BinderAsync(AccountNotification accountNotification);

	void notifyWorkOrderMsgAsync(WorkOrderNotification workOrderNotification);

	void notifyConversionAsync(ConversionNotification notification);

	void notifyInvoiceMsgAsync(InvoiceNotification invoiceNotification);

	void sendReceiptMsgAsync(ReceiptNotification receiptNotification);

	void releaseInvoiceApplicationLock(String tradeWaterId);

	//业主意见物业回复通知
	void noticeComment(InteractCommentNotice notice) throws Exception;

	void noticeEvaluate(InteractCommentNotice notice) throws Exception;

	//从外部春川小程序上用户的绑定房屋数据
	void noticeUserBindHouseByCC(CcBindHouseNotification notice) throws Exception;

	//装修登记审核结果通知给业主
	void noticeRenovation(RenovationNotification notice) throws Exception;
}

package com.yumu.hexie.service.common;

import com.yumu.hexie.integration.notify.InvoiceNotification;
import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;
import com.yumu.hexie.integration.notify.ReceiptNotification;
import com.yumu.hexie.integration.notify.RenovationNotification;
import com.yumu.hexie.integration.notify.WorkOrderNotification;
import com.yumu.hexie.integration.wechat.entity.common.WechatResponse;
import com.yumu.hexie.model.card.dto.EventSubscribeDTO;
import com.yumu.hexie.model.event.dto.BaseEventDTO;
import com.yumu.hexie.model.localservice.bill.YunXiyiBill;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.billpush.vo.BillPushDetail;
import com.yumu.hexie.service.sales.req.NoticeRgroupSuccess;
import com.yumu.hexie.service.shequ.vo.InteractCommentNotice;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: GotongService.java, v 0.1 2016年1月8日 上午9:59:49  Exp $
 */
public interface GotongService {

    void sendRepairAssignMsg(long opId, RepairOrder order);
    
    void sendXiyiAssignMsg(long opId, YunXiyiBill bill);
    
    void sendRepairAssignedMsg(RepairOrder order);
    
    boolean sendSubscribeMsg(EventSubscribeDTO subscribeVO);
    
    void sendCommonYuyueBillMsg(int serviceType, String title, String billName, String requireTime, String url, String remark);
    
	void sendServiceResvMsg(long threadId, String openId, String title, String content, String requireTime, String remark, String appId);

	void sendRegiserMsg(User user);

	WechatResponse sendGroupMessage(String openId, String appId, long msgId, String content);

	void sendPayNotification(AccountNotification accountNotification);
	
	void sendCustomServiceAssignedMsg(ServiceOrder serviceOrder);

	void sendResetPasswordMsg(User user, String password);

	void sendCustomerDelivery(User user, ServiceOrder serviceOrder);

	void sendPayNotification4HouseBinder(AccountNotification accountNotify);

	String sendBillPush(String openId, String appId, BillPushDetail billPushDetail);

	boolean sendWorkOrderNotification(WorkOrderNotification workOrderNotification);

	WechatResponse sendMsg4ApplicationInvoice(BaseEventDTO baseEventDTO);

	WechatResponse sendMsg4FinishInvoice(InvoiceNotification invoiceNotification);

	WechatResponse sendMsg4ApplicationReceipt(BaseEventDTO baseEventDTO);

	WechatResponse sendMsg4FinishReceipt(ReceiptNotification receiptNotification);

	void sendGroupSuccessNotification(NoticeRgroupSuccess noticeRgroupSuccess);

	//物业意见回复通知
	WechatResponse sendInteractNotification(InteractCommentNotice commentNotice);

	//业主评价模板推送
	void sendInteractGradeNotification(InteractCommentNotice notice);

	WechatResponse sendRenovationNotification(RenovationNotification notice);
}

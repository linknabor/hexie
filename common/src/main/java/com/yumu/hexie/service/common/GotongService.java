/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.common;

import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;
import com.yumu.hexie.model.card.dto.EventSubscribeDTO;
import com.yumu.hexie.model.community.Thread;
import com.yumu.hexie.model.localservice.bill.YunXiyiBill;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.billpush.vo.BillPushDetail;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: GotongService.java, v 0.1 2016年1月8日 上午9:59:49  Exp $
 */
public interface GotongService {

    public void sendRepairAssignMsg(long opId,RepairOrder order,int distance);
    
    public void sendXiyiAssignMsg(long opId,YunXiyiBill bill);
    
    public void sendRepairAssignedMsg(RepairOrder order);
    
    public boolean sendSubscribeMsg(EventSubscribeDTO subscribeVO);
    
    public void sendCommonYuyueBillMsg(int serviceType,String title,String billName, String requireTime, String url, String remark);
    
	void sendServiceResvMsg(long threadId, String openId, String title, String content, String requireTime, String remark, String appId);

	void sendRegiserMsg(User user);

	boolean sendGroupMessage(String openId, String appId, long msgId, String content);

	void sendPayNotification(AccountNotification accountNotification);
	
	void sendServiceNotification(User sendUser, ServiceOrder serviceOrder);

	void sendCustomServiceAssignedMsg(ServiceOrder serviceOrder);

	void sendDeliveryNotification(User sendUser, ServiceOrder serviceOrder);
	
	void sendResetPasswordMsg(User user, String password);

	void sendCustomerDelivery(User user, ServiceOrder serviceOrder);

	void sendPostingReplyMsg(Thread thread);

	void sendPayNotification4HouseBinder(AccountNotification accountNotify);

	boolean sendBillPush(String openId, String appId, BillPushDetail billPushDetail);

}

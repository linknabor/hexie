package com.yumu.hexie.service.user;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserNotice;

public interface UserNoticeService {

	public List<UserNotice> queryByUserId(long userId,Pageable page);

	public void readNotice(long userId,long noticeId);
	
	public void orderSuccess(long userId,String tel,long orderId,String orderNo, String productName, float prices);
	public void orderSend(long userId,String tel,long orderId,String orderNo,String logistics,String logisticsNo);
	
	public void groupSuccess(User user,String tel,long groupId, int ruleMinNum,String productName, String ruleName);
	public void groupFail(User user,String tel,long groupId,String productName,int needNum, String ruleName);
	public void groupTimeoutTip(User user,String tel,long groupId,String groupNo,String productName, int ruleMinNum,int needNum);
	
	public void yuyueSuccess(long userId,String tel, String userName, long yuyueId, String serviceName, int paymentType, float prices);
	
	public void commentNotice(long userId, long threadId,String replyerName,String replyInfo);

	void couponSuccess(Coupon coupon);

	void groupArriaval(ServiceOrder serviceOrder);
	
}

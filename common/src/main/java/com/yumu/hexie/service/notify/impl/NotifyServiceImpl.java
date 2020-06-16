package com.yumu.hexie.service.notify.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.yumu.hexie.integration.notify.PayNotifyDTO;
import com.yumu.hexie.integration.notify.PayNotifyDTO.AccountNotify;
import com.yumu.hexie.integration.notify.PayNotifyDTO.ServiceNotify;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.user.BankCardRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.notify.NotifyService;
import com.yumu.hexie.service.shequ.WuyeService;
import com.yumu.hexie.service.user.CouponService;
import com.yumu.hexie.service.user.PointService;

@Service
public class NotifyServiceImpl implements NotifyService {
	
	private static final Logger log = LoggerFactory.getLogger(NotifyServiceImpl.class);
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CouponService couponService;
	@Autowired
	private SystemConfigService systemConfigService;
	@Autowired
	private PointService pointService;
	@Autowired
	private BankCardRepository bankCardRepository;
	@Autowired
	private WuyeService wuyeService;
	@Autowired
	private GotongService gotongService;

	/**
	 * 支付完成后的一些操作
	 * 步骤：
	 *  1.有红包的更新红包状态，
	 *	2.绑定缴费房屋（bindStich==1，需要远程请求，双边事务），
	 *	3.+芝麻，
	 *
	 *其中1,2实时完成,3可异步完成(队列)。
	 *
	 */
	@Transactional
	@Override
	public void noticePayed(PayNotifyDTO payNotifyDTO) {
		
		//1.更新红包状态
		User user = null;
		Coupon coupon = null;
		if (!StringUtils.isEmpty(payNotifyDTO.getCouponId())) {
			coupon = couponService.findOne(Long.valueOf(payNotifyDTO.getCouponId()));
			if (coupon != null) {
				try {
					couponService.comsume("999999", coupon.getId());
				} catch (Exception e) {
					//如果优惠券已经消过一次，里面会抛异常提示券已使用，但是步骤2和3还是需要进行的
					log.error(e.getMessage(), e);
				}
			}
		}
		if (user == null) {
			if (coupon != null) {
				user = userRepository.findById(coupon.getUserId());
			}
		}
		if (user == null) {
			List<User> userList = userRepository.findByWuyeId(payNotifyDTO.getWuyeId());
			if (userList == null || userList.isEmpty()) {
				log.info("can not find user, wuyeId : " + payNotifyDTO.getWuyeId() + ", tradeWaterId : " + payNotifyDTO.getOrderId());
			}else {
				user = userList.get(0);
			}
			
		}
		if (user != null) {
			//2.添加芝麻积分
			if (systemConfigService.isCardServiceAvailable(user.getAppId())) {
				String pointKey = "wuyePay-" + payNotifyDTO.getOrderId();
				pointService.addPointAsync(user, payNotifyDTO.getPoints(), pointKey);
			}else {
				String pointKey = "zhima-bill-" + user.getId() + "-" + payNotifyDTO.getOrderId();
				pointService.updatePoint(user, "10", pointKey);
			}
			//3.如果是绑卡支付，记录用户的quicktoken
			if (!StringUtils.isEmpty(payNotifyDTO.getCardNo()) && !StringUtils.isEmpty(payNotifyDTO.getQuickToken())) {
				bankCardRepository.updateBankCardByAcctNoAndUserId(payNotifyDTO.getQuickToken(), payNotifyDTO.getQuickToken(), user.getId());
			}
			//5.绑定所缴纳物业费的房屋
			wuyeService.bindHouseByTradeAsync(payNotifyDTO.getBindSwitch(), user, payNotifyDTO.getOrderId());
		}
		
		
		AccountNotify accountNotify = payNotifyDTO.getAccountNotify();
		if (accountNotify!=null) {
			
			//4.通知物业相关人员，收费到账
			sendPayNotify(accountNotify);
		}
		
		ServiceNotify serviceNotify = payNotifyDTO.getServiceNotify();
		if (serviceNotify!=null) {
			serviceNotify.setOrderId(payNotifyDTO.getOrderId());
			sendServiceNotify(serviceNotify);
		}
		
	}
	
	/**
	 * 到账消息推送
	 */
	@Override
	public void sendPayNotify(AccountNotify accountNotify) {
		
		List<Map<String, String>> openidList = accountNotify.getOpenids();

		if (openidList == null || openidList.isEmpty()) {
			return;
		}
	
		for (Map<String, String> openidMap : openidList) {
			
			User user = null;
			String openid = openidMap.get("openid");
			if (StringUtils.isEmpty(openid)) {
				log.warn("openid is empty, will skip. ");
				continue;
			}
			List<User> userList = userRepository.findByOpenid(openid);
			if (userList!=null && !userList.isEmpty()) {
				user = userList.get(0);
			}else {
				log.warn("can not find user, openid : " + openid);
			}
			if (user!=null) {
				accountNotify.setUser(user);
				gotongService.sendPayNotify(accountNotify);
			}
			
		}
			
	}
	
	/**
	 * 服务消息推送
	 */
	@Override
	public void sendServiceNotify(ServiceNotify serviceNotify) {
		
		List<Map<String, String>> openidList = serviceNotify.getOpenids();

		if (openidList == null || openidList.isEmpty()) {
			return;
		}
	
		for (Map<String, String> openidMap : openidList) {
			
			User user = null;
			String openid = openidMap.get("openid");
			if (StringUtils.isEmpty(openid)) {
				log.warn("openid is empty, will skip. ");
				continue;
			}
			List<User> userList = userRepository.findByOpenid(openid);
			if (userList!=null && !userList.isEmpty()) {
				user = userList.get(0);
			}else {
				log.warn("can not find user, openid : " + openid);
			}
			if (user!=null) {
				serviceNotify.setUser(user);
				gotongService.sendServiceNotify(serviceNotify);
			}
			
		}
			
	}
	
	
}

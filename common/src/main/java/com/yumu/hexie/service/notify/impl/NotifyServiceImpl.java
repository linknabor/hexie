package com.yumu.hexie.service.notify.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.notify.PayNotifyDTO;
import com.yumu.hexie.integration.notify.PayNotifyDTO.AccountNotification;
import com.yumu.hexie.integration.notify.PayNotifyDTO.ServiceNotification;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.user.BankCardRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.customservice.CustomService;
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
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private CustomService customService;
	

	/**
	 * 	1.优惠券核销
		2.积分
		3.绑卡记录quickToken和卡号
		4.绑定房屋
		5.缴费到账通知
		6.自定服务
	 */
	@Transactional
	@Override
	public void notify(PayNotifyDTO payNotifyDTO) {
		
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
			//4.绑定所缴纳物业费的房屋
			wuyeService.bindHouseByTradeAsync(payNotifyDTO.getBindSwitch(), user, payNotifyDTO.getOrderId());
		}
		
		//5.通知物业相关人员，收费到账
		AccountNotification accountNotify = payNotifyDTO.getAccountNotify();
		accountNotify.setOrderId(payNotifyDTO.getOrderId());
		sendPayNotificationAsync(accountNotify);
		
		//6.自定义服务
		ServiceNotification serviceNotification = payNotifyDTO.getServiceNotify();
		serviceNotification.setOrderId(payNotifyDTO.getOrderId());
		sendServiceNotificationAsync(serviceNotification);
		
		//7.更新自定义服务订单状态
		customService.notifyPayByServplat(payNotifyDTO.getOrderId());
		
	}
	
	/**
	 * 到账消息推送
	 */
	@Override
	public void sendPayNotificationAsync(AccountNotification accountNotification) {
		
		if (accountNotification == null) {
			return;
		}
		
		int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				String value = objectMapper.writeValueAsString(accountNotification);
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_PAY_QUEUE, value);
				isSuccess = true;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				retryTimes++;
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					log.error(e.getMessage(), e);
				}
			}
		}
		
	}
	
	/**
	 * 服务消息推送
	 */
	@Override
	public void sendServiceNotificationAsync(ServiceNotification serviceNotification) {
		
		if (serviceNotification == null) {
			return;
		}
		
		int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				String value = objectMapper.writeValueAsString(serviceNotification);
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_SERVICE_QUEUE, value);
				isSuccess = true;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				retryTimes++;
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					log.error(e.getMessage(), e);
				}
			}
		}
		
			
	}
	
	
}

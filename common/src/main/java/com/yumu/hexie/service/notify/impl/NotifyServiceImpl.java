package com.yumu.hexie.service.notify.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.RedisLock;
import com.yumu.hexie.integration.notify.PartnerNotification;
import com.yumu.hexie.integration.notify.PayNotification;
import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;
import com.yumu.hexie.integration.notify.PayNotification.ServiceNotification;
import com.yumu.hexie.integration.notify.ConversionNotification;
import com.yumu.hexie.integration.notify.InvoiceNotification;
import com.yumu.hexie.integration.notify.WorkOrderNotification;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.BankCardRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.notify.NotifyService;
import com.yumu.hexie.service.shequ.WuyeService;
import com.yumu.hexie.service.user.PointService;

@Service
public class NotifyServiceImpl implements NotifyService {
	
	private static final Logger log = LoggerFactory.getLogger(NotifyServiceImpl.class);
	
	@Autowired
	private UserRepository userRepository;
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
	

	/**
	 * 	1.优惠券核销
		2.积分
		3.绑卡记录quickToken和卡号
		4.绑定房屋
		5.缴费到账通知
		6.自定服务接单通知
		7.自定义服务订单状体更新
		8.商品订单到账通知
	 */
	@Transactional
	@Override
	public void notify(PayNotification payNotification) {
		
		String tradeWaterId = payNotification.getOrderId();
		String key = ModelConstant.KEY_NOITFY_PAY_DUPLICATION_CHECK + tradeWaterId;
		Long result = RedisLock.lock(key, redisTemplate, 3600l);
		if (0 == result) {
			log.info("tradeWaterId : " + tradeWaterId + ", already notified, will skip ! ");
			return;
		}
		//1.更新红包状态
		notifyWuyeCouponConsumeAsync(payNotification.getOrderId(), payNotification.getCouponId());
		
		User user = null;
		if (!StringUtils.isEmpty(payNotification.getWuyeId())) {
			List<User> userList = userRepository.findByWuyeId(payNotification.getWuyeId());
			if (userList == null || userList.isEmpty()) {
				log.info("can not find user, wuyeId : " + payNotification.getWuyeId() + ", tradeWaterId : " + payNotification.getOrderId());
			}else {
				user = userList.get(0);
			}

		}
		
		if (user != null) {
			//2.添加芝麻积分
			if (systemConfigService.isCardServiceAvailable(user.getAppId())) {
				if (!StringUtils.isEmpty(payNotification.getOrderId())) {
					String pointKey = "wuyePay-" + payNotification.getOrderId();
					pointService.addPointAsync(user, payNotification.getPoints(), pointKey);
				}
			}else {
				String pointKey = "zhima-bill-" + user.getId() + "-" + payNotification.getOrderId();
				pointService.updatePoint(user, "10", pointKey);
			}
			//3.如果是绑卡支付，记录用户的quicktoken
			if (!StringUtils.isEmpty(payNotification.getCardNo()) && !StringUtils.isEmpty(payNotification.getQuickToken())) {
				bankCardRepository.updateBankCardByAcctNoAndUserId(payNotification.getQuickToken(), payNotification.getQuickToken(), user.getId());
			}
			//4.绑定所缴纳物业费的房屋
			if ("0".equals(payNotification.getTranType())) {	//0管理费， 1其他收费
				wuyeService.bindHouseByTradeAsync(payNotification.getBindSwitch(), user, payNotification.getOrderId(), "4");
			}
			
		}
		
		//5.通知物业相关人员，收费到账
		AccountNotification accountNotify = payNotification.getAccountNotify();
		if (accountNotify!=null) {
			accountNotify.setOrderId(payNotification.getOrderId());
			if (accountNotify.getFeePrice() != null) {
				sendPayNotificationAsync(accountNotify);
				sendPayNotification4BinderAsync(accountNotify);
			}
			
		}
		//6.自定义服务
		ServiceNotification serviceNotification = payNotification.getServiceNotify();
		if (serviceNotification!=null) {
			serviceNotification.setOrderId(payNotification.getOrderId());
			sendServiceNotificationAsync(serviceNotification);
		}
		
		//7.更新serviceOrder订单状态
		updateServiceOrderStatusAsync(payNotification.getOrderId());
		
		//8.通知相关人员发货
		notifyDeliveryAsync(payNotification.getOrderId());
	}
	
	/**
	 * 到账消息推送(给物业配置的工作人推送)
	 */
	@Override
	public void sendPayNotificationAsync(AccountNotification accountNotification) {
		
		if (accountNotification == null) {
			log.info("accountNotification is null, will return ! ");
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
		
		String key = ModelConstant.KEY_ASSIGN_CS_ORDER_DUPLICATION_CHECK + serviceNotification.getOrderId();
		Long result = RedisLock.lock(key, redisTemplate, 3600l);
		log.info("result : " + result);
		if (0 == result) {
			log.info("trade : " + serviceNotification.getOrderId() + ", already in the send queue, will skip .");
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
	
	/**
	 * 订单状态更新
	 */
	@Override
	public void updateServiceOrderStatusAsync(String orderId) {
		
		if (StringUtils.isEmpty(orderId)) {
			log.info("updateServiceOrderStatusAsync: orderId is null, will return ! ");
			return;
		}
		
		int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_UPDATE_ORDER_STATUS_QUEUE, orderId);
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
	 * 通知发货人员发货
	 */
	@Override
	public void notifyDeliveryAsync(String orderId) {
		
		if (StringUtils.isEmpty(orderId)) {
			log.info("notifyDeliveryAsync: orderId is null, will return ! ");
			return;
		}
		
		int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_DELIVERY_QUEUE, orderId);
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

	@Override
	public void updatePartner(List<PartnerNotification> list) {
		
		if (list==null || list.isEmpty()) {
			log.info("updatePartner: partnerNotification is null, will return ! ");
			return;
		}
		
		int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				String value = objectMapper.writeValueAsString(list);
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_PARTNER_REFUND_QUEUE, value);
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
	 * 电商类退款
	 */
	@Override
	public void notifyEshopRefund(String orderId) {
	
		if (StringUtils.isEmpty(orderId)) {
			log.info("notifyRefund: orderId is null, will return ! ");
			return;
		}
		
		int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_ESHOP_REFUND_QUEUE, orderId);
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
	 * 物业优惠券状态更新
	 */
	@Override
	public void notifyWuyeCouponConsumeAsync(String orderId, String couponId) {
		
		if (StringUtils.isEmpty(couponId)) {
			log.info("notifyWuyeCouponConsumeAsync: couponId is empty, will return ! ");
			return;
		}
		
		int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				Map<String, String> map = new HashMap<>();
				map.put("orderId", orderId);
				map.put("couponId", couponId);
				
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				String value = objectMapper.writeValueAsString(map);
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_WUYE_COUPON_QUEUE, value);

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
	 * 到账消息推送(给房屋绑定者推)
	 */
	@Override
	public void sendPayNotification4BinderAsync(AccountNotification accountNotification) {
		
		if (accountNotification == null) {
			log.info("accountNotification is null, will return ! ");
			return;
		}
		
		int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				String value = objectMapper.writeValueAsString(accountNotification);
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_HOUSE_BINDER_QUEUE, value);
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
	 * 工单消息队列
	 */
	@Override
	public void notifyWorkOrderMsgAsync(WorkOrderNotification workOrderNotification) {
		
		String orderId = workOrderNotification.getOrderId();
		String timestamp = workOrderNotification.getTimestamp();
		String checkKey = orderId + "_" + timestamp;
		String key = ModelConstant.KEY_NOTIFY_WORK_ORDER_DUPLICATION_CHECK + checkKey;
		Long result = RedisLock.lock(key, redisTemplate, 3600l);
		if (0 == result) {
			log.info("orderId : " + orderId + ", already notified, will skip ! ");
			return;
		}
		
		if (StringUtils.isEmpty(workOrderNotification.getOrderId())) {
			log.info("notifyWorkOrderMsgAsync: orderid is empty, will return ! ");
			return;
		}
		
		int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				String value = objectMapper.writeValueAsString(workOrderNotification);
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_WORKORER_MSG_QUEUE, value);

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
	 * 其他业务转工单
	 */
	@Override
	public void notifyConversionAsync(ConversionNotification notification) {
		
		String orderId = notification.getOrderId();
		String timestamp = notification.getTimestamp();
		String checkKey = orderId + "_" + timestamp;
		String key = ModelConstant.KEY_NOTIFY_CONVERSION_DUPLICATION_CHECK + checkKey;
		Long result = RedisLock.lock(key, redisTemplate, 3600l);
		if (0 == result) {
			log.info("orderId : " + orderId + ", already notified, will skip ! ");
			return;
		}
		
		if (StringUtils.isEmpty(notification.getOrderId())) {
			log.info("notifyWorkOrderConversionAsync: orderid is empty, will return ! ");
			return;
		}
		
		int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				String value = objectMapper.writeValueAsString(notification);
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_CONVERSION_MSG_QUEUE, value);

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
	 * 开票/红冲 成功通知
	 */
	@Override
	public void notifyInvoiceMsgAsync(InvoiceNotification invoiceNotification) {
		
		String orderId = invoiceNotification.getOrderId();
		
		if (StringUtils.isEmpty(orderId)) {
			log.info("notifyWorkOrderConversionAsync: orderid is empty, will return ! ");
			return;
		}
		
		String checkKey = orderId;
		String key = ModelConstant.KEY_INVOICE_NOTIFICATION_LOCK + checkKey;
		Long result = RedisLock.lock(key, redisTemplate, 3600l*24*10);
		if (0 == result) {
			log.info("invoiceNo : " + orderId + ", already notified, will skip ! ");
			return;
		}
		
		int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				String value = objectMapper.writeValueAsString(invoiceNotification);
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_INVOICE_NOTIFICATION_QUEUE, value);

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

package com.yumu.hexie.service.notify.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.yumu.hexie.integration.notify.*;
import com.yumu.hexie.service.shequ.vo.InteractCommentNotice;
import com.yumu.hexie.service.user.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.RedisLock;
import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.BankCardRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.cache.CacheService;
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
	private UserService userService;
	@Autowired
	@Qualifier("stringRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private CacheService cacheService;

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
		Long result = RedisLock.lock(key, redisTemplate, 3600L);
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
				//将绑定房屋选项写入缓存，待入账根据选项判断是否帮业主绑定房屋
		        String bindHouKey = ModelConstant.KEY_TRADE_BIND_HOU + payNotification.getOrderId();
		        String bindHouse = redisTemplate.opsForValue().get(bindHouKey);
		        log.info("tradeWaterId : " + tradeWaterId + ", +bindHouse : " + bindHouse);
		        if ("1".equals(bindHouse)) {
		        	wuyeService.bindHouseByTradeAsync(bindHouse, user, payNotification.getOrderId(), "4");
				}
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
		sendServiceNotificationAsync(payNotification.getOrderId());
//		ServiceNotification serviceNotification = payNotification.getServiceNotify();
//		log.error("serviceNotification :" + serviceNotification);
//		if (serviceNotification!=null) {
//			sendServiceNotificationAsync(payNotification.getOrderId());
//		}

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
	public void sendServiceNotificationAsync(String orderId) {

		if (StringUtils.isEmpty(orderId)) {
			return;
		}
		
		int retryTimes = 0;
		boolean isSuccess = false;
		while(!isSuccess && retryTimes < 3) {
			try {
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_SERVICE_QUEUE, orderId);
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
	public void notifyEshopRefund(Map<String, Object> map) {
		Object orderId = map.get("trade_water_id");
		if (StringUtils.isEmpty(orderId)) {
			log.info("notifyRefund: orderId is null, will return ! ");
			return;
		}
		String str = JSON.toJSONString(map);

		int retryTimes = 0;
		boolean isSuccess = false;

		while(!isSuccess && retryTimes < 3) {
			try {
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_NOTIFY_ESHOP_REFUND_QUEUE, str);
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
		Long result = RedisLock.lock(key, redisTemplate, 3600L);
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
		Long result = RedisLock.lock(key, redisTemplate, 3600L);
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
		String applyId = invoiceNotification.getApplyId();
		
		if (StringUtils.isEmpty(orderId)) {
			log.info("notifyInvoiceMsgAsync: orderid is empty, will return ! ");
			return;
		}

		String key = ModelConstant.KEY_INVOICE_NOTIFICATION_LOCK + applyId;
		Long result = RedisLock.lock(key, redisTemplate, 3600L * 24);
		if (0 == result) {
			log.info("invoice msg notification, applyId : " + applyId + ", already notified, will skip ! ");
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
	
	/**
	 * 电子收据开具成功通知
	 */
	@Override
	public void sendReceiptMsgAsync(ReceiptNotification receiptNotification) {
		
		String receiptId = receiptNotification.getReceiptId();
		String openid = receiptNotification.getOpenid();
		if (StringUtils.isEmpty(receiptId)) {
			log.info("sendReceiptMsgAsync: receiptId is empty, will return ! ");
			return;
		}
		String key = ModelConstant.KEY_RECEIPT_NOTIFICATION_LOCK + receiptId + ":" + openid;
		Long result = RedisLock.lock(key, redisTemplate, 3600L * 24);
		if (0 == result) {
			log.info("receipt msg notification, receiptId : " + receiptId + ", already notified, will skip ! ");
			return;
		}
		
		int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				String value = objectMapper.writeValueAsString(receiptNotification);
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_RECEIPT_NOTIFICATION_QUEUE, value);

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
	 * 释放发票申请的锁
	 */
	@Override
	public void releaseInvoiceApplicationLock(String tradeWaterId) {
		
		if (StringUtils.isEmpty(tradeWaterId)) {
			log.info("releaseInvoiceApplicationLock: tradeWaterId is empty, will return ! ");
			return;
		}
		log.info("will remove invoice apply lock, trade_water_id : " + tradeWaterId);
		String key = ModelConstant.KEY_INVOICE_APPLICATIONF_FLAG + tradeWaterId;
		redisTemplate.delete(key);
		
	}

	@Override
	public void noticeComment(InteractCommentNotice notice) throws Exception {
		if(notice == null) {
			log.error("推送内容为空");
			return;
		}
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		String value = objectMapper.writeValueAsString(notice);
		redisTemplate.opsForList().rightPush(ModelConstant.interactReplyNoticeQueue, value);
	}

	@Override
	public void noticeEvaluate(InteractCommentNotice notice) throws Exception {
		if(notice == null) {
			log.error("评价推送内容为空");
			return;
		}
		if(StringUtils.isEmpty(notice.getInteractId())
				|| StringUtils.isEmpty(notice.getContent())
				|| StringUtils.isEmpty(notice.getUserName())
				|| StringUtils.isEmpty(notice.getAppid())) {
			log.error("评价推送内容为空");
			return;
		}
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		String value = objectMapper.writeValueAsString(notice);
		redisTemplate.opsForList().rightPush(ModelConstant.interactGradeNoticeQueue, value);
	}

	@Override
	public void noticeUserBindHouseByCC(CcBindHouseNotification notice) throws Exception {
		//1.通过传过来的小程序openid查询是否存在用户
		String miniopenid = notice.getOpenid();
		String unionid = notice.getUnionid();
		User user = null;
		if (!StringUtils.isEmpty(unionid)) {
			List<User> list = userRepository.findByUnionid(unionid);
			if (list!=null && list.size()>0) {
				user = list.get(0);
			}
		}
		if (user == null) {
			//春川换了appid，这里兼容老用户，先拿老的appid去查，如果能查到，把老用户的miniappid和miniopenid更掉
			if("wxde89512c4cbfdad9".equals(notice.getAppid())) {
				String ori_appid = "wx0c81e6687f6f5e43";
				List<User> list = userRepository.findByTelAndMiniAppId(notice.getPhone(), ori_appid);
				if (list!=null && list.size()>0) {
					user = list.get(0);
				}
			}
			if(user == null) {
				if (!StringUtils.isEmpty(miniopenid)) {
					user = userService.getByMiniopenid(miniopenid);
				}
			}
		}

		//2.新增或更新用户
		if (user == null) {
			user = new User();
			user.setOpenid("0");    //TODO
			user.setShareCode(DigestUtils.md5Hex("UID[" + UUID.randomUUID() + "]"));
		}
		user.setMiniAppId(notice.getAppid());
		user.setMiniopenid(notice.getOpenid());
		user.setUnionid(notice.getUnionid());
		user.setCspId(notice.getCsp_id());
		user.setSectId(notice.getSect_id());
		user.setTel(notice.getPhone());
		user.setRegisterDate(System.currentTimeMillis());
		user.setXiaoquName(notice.getSect_name());
		userRepository.save(user);

		//3.生成wuyeId
		if(StringUtils.isEmpty(user.getWuyeId())) {
			String wuyeId = userService.bindWuYeIdSync(user);
			user.setWuyeId(wuyeId);
		}
		//4.根据传过来的房屋，绑定/解绑房屋
		String data_type = notice.getData_type();
		if("1".equals(data_type)) { //新增绑定
			wuyeService.bindHouseNoStmt(user, notice.getHouse_id(), notice.getArea());
		} else if("2".equals(data_type)) { //解绑
			wuyeService.deleteHouse(user, notice.getHouse_id());
		} else {
			log.error("data_type值不合法，本次不做绑房子操作");
		}
		//清缓存
		cacheService.clearUserCache(cacheService.getCacheKey(user));
	}

	@Override
	public void noticeRenovation(RenovationNotification notice) throws Exception {
		if(notice == null) {
			log.error("推送内容为空");
			return;
		}
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		String value = objectMapper.writeValueAsString(notice);
		redisTemplate.opsForList().rightPush(ModelConstant.renovationNoticeQueue, value);
	}

}

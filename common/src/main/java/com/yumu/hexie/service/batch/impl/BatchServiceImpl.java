package com.yumu.hexie.service.batch.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.wechat.constant.ConstantAlipay;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.integration.wuye.WuyeUtil;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.resp.HouseListVO;
import com.yumu.hexie.integration.wuye.vo.HexieHouse;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.batch.BatchService;
import com.yumu.hexie.service.card.WechatCardQueueTask;
import com.yumu.hexie.service.notify.NotifyQueueTask;
import com.yumu.hexie.service.shequ.WuyeQueueTask;
import com.yumu.hexie.service.shequ.WuyeService;
import com.yumu.hexie.service.subscribemsg.WechatSubscribeMsgQueueTask;
import com.yumu.hexie.service.user.CouponQueueTask;

@Service
public class BatchServiceImpl implements BatchService {

	private static Logger logger = LoggerFactory.getLogger(BatchServiceImpl.class);

	@Autowired
	private WuyeService wuyeService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private WechatCardQueueTask wechatCardQueueTask;
	
	@Autowired
	private WuyeQueueTask wuyeQueueTask;
	
	@Autowired
	private NotifyQueueTask notifyQueueTask;
	
	@Autowired
	private CouponQueueTask couponQueueTask;
	
	@Autowired
	private WechatSubscribeMsgQueueTask wechatSubscribeMsgQueueTask;
	
	@Value("${mainServer}")
	private Boolean mainServer;
	
	/*微信参数begin#################################*/
	@Value("${wechat.appId}")
	private String wechatAppId;
	
	@Value("${wechat.appSecret}")
	private String wechatAppSecret;
	
	@Value("${wechat.componentAppId}")
	private String wechatComponentAppId;
	
	@Value("${wechat.mchId}")
	private String wechatMchId;
	
	@Value("${wechat.mchKey}")
	private String wechatMchKey;
	
	@Value("${wechat.certPath}")
	private String wechatCertPath;
	
	@Value("${wechat.unifiedUrl}")
	private String wechatUnifiedUrl;
	
	@Value("${wechat.notifyUrl}")
	private String wechatNotifyUrl;
	/*微信参数end#################################*/
	
	/*支付宝参数begin#################################*/
	@Value("${alipay.appId}")
	private String alipayAppId;
	
	@Value("${alipay.appSecret}")
	private String alipayAppSecret;
	
	@Value("${alipay.gateway}")
	private String alipayGateway;
	
	@Value("${alipay.appPrivateKey}")
	private String alipayAppPrivateKey;
	
	@Value("${alipay.publicKey}")
	private String alipayPublicKey;
	/*支付宝参数end#################################*/
	
	@PostConstruct
	public void runBatch() throws InterruptedException {
		
		if (mainServer) {	//BK程序不跑下面的队列轮询
			return;
		}
		init();
		
		wuyeQueueTask.bindHouseByTrade();
		wechatCardQueueTask.eventSubscribe();
		wechatCardQueueTask.eventUserGetCard();
		wechatCardQueueTask.eventUpdateCard();
		wechatCardQueueTask.updatePointAsync();
		wechatCardQueueTask.wuyeRefund();
		notifyQueueTask.sendWuyeNotificationAysc();
		notifyQueueTask.sendCustomServiceNotificationAysc();
		notifyQueueTask.updateOpereratorAysc();
		notifyQueueTask.updateServiceCfgAysc();
		notifyQueueTask.updateOrderStatusAysc();
		notifyQueueTask.sendDeliveryNotificationAsyc();
		notifyQueueTask.updatePartnerAsync();
		notifyQueueTask.eshopRefundAsync();
		couponQueueTask.gainCouponAsync();
		notifyQueueTask.consumeWuyeCouponAsync();
		notifyQueueTask.sendWuyeNotification4HouseBinderAysc();
		wechatSubscribeMsgQueueTask.eventSubscribeMsg();
//		userQueueTask.eventSubscribe();
//		userQueueTask.eventUnsubscribe();
		notifyQueueTask.sendWorkOrderMsgNotificationAsyc();
		notifyQueueTask.handleConversionAsyc();

		logger.info("异步队列任务启动完成。");
		
	}
	
	private void init()	{
		
		logger.info("start to init constant ...is mainServer : " + mainServer);
		Constants.MAIN_SERVER = mainServer;
		
		ConstantWeChat.APPID = wechatAppId;
		ConstantWeChat.APPSECRET = wechatAppSecret;
		ConstantWeChat.MERCHANT_ID = wechatMchId;
		ConstantWeChat.MERCHANT_KEY = wechatMchKey;
		ConstantWeChat.KEYSTORE = wechatCertPath;
		ConstantWeChat.UNIFIEDURL = wechatUnifiedUrl;
		ConstantWeChat.NOTIFYURL = wechatNotifyUrl;
		ConstantWeChat.COMPONENT_APPID = wechatComponentAppId;
		
		ConstantAlipay.APPID = alipayAppId;
		ConstantAlipay.SECRET = alipayAppSecret;
		ConstantAlipay.APP_PRIVATE_KEY = alipayAppPrivateKey;
		ConstantAlipay.PUBLIC_KEY = alipayPublicKey;
		ConstantAlipay.GATEWAY = alipayGateway;
	}

	@Override
	public void updateUserShareCode() {
		List<User> list = userRepository.getShareCodeIsNull();
		for (User user : list) {
			try {
				String shareCode = DigestUtils.md5Hex("UID[" + UUID.randomUUID() + "]");
				user.setShareCode(shareCode);
				userRepository.save(user);
			} catch (Exception e) {
				logger.error("user保存失败：" + user.getId());
			}
		}

	}

	@Override
	public void updateRepeatUserShareCode() {
		List<String> repeatUserList = userRepository.getRepeatShareCodeUser();
		for (String string : repeatUserList) {
			List<User> uList = userRepository.getUserByShareCode(string);
			for (User user2 : uList) {
				try {
					String shareCode = DigestUtils.md5Hex("UID[" + UUID.randomUUID() + "]");
					user2.setShareCode(shareCode);
					userRepository.save(user2);
				} catch (Exception e) {
					logger.error("user保存失败：" + user2.getId());
				}
			}
		}

	}

	/**
	 * 手工绑定房屋
	 * @param userId
	 * @param tradeWaterId
	 */
	@Override
	public void fixBindHouse(String userId, String tradeWaterId) {

		User user = userRepository.findById(Long.valueOf(userId)).get();
		wuyeService.bindHouseByTradeAsync("1", user, tradeWaterId);
	}


	@Override
	public void bindHouseBatch(String appId) {

		Assert.hasText(appId, "appId不能为空");
		
		List<User> userList = userRepository.findByAppId(appId);
		
		for (User user : userList) {
			
			if (StringUtils.isEmpty(user.getTel())) {
				continue;
			}
			if (!StringUtils.isEmpty(user.getSectId())) {
				continue;
			}
			BaseResult<HouseListVO> baseResult = WuyeUtil.queryHouse(user);
			HouseListVO vo = baseResult.getData();
			if (vo!=null) {
				List<HexieHouse> houseList = vo.getHou_info();
				if (houseList!=null && !houseList.isEmpty()) {
					HexieHouse hexieHouse = houseList.get(0);
					
					user.setTotalBind(houseList.size());
					user.setXiaoquName(hexieHouse.getSect_name());
					user.setProvince(hexieHouse.getProvince_name());
					user.setCity(hexieHouse.getCity_name());
					user.setCounty(hexieHouse.getRegion_name());
					user.setSectId(hexieHouse.getSect_id());	
					user.setCspId(hexieHouse.getCsp_id());
					if (!StringUtils.isEmpty(hexieHouse.getOffice_tel())) {
						user.setOfficeTel(hexieHouse.getOffice_tel());
					}
					userRepository.save(user);
					
				}
			}
		}
		
	}
	
	/**
	 * 补sectId不为空但为零的情况
	 */
	@Override
	public void bindHouseZeroSect() {

		String sectId = "0";
		List<User> userList = userRepository.findBySectId(sectId);
		
		for (User user : userList) {
			
			if (StringUtils.isEmpty(user.getTel())) {
				continue;
			}
			BaseResult<HouseListVO> baseResult = WuyeUtil.queryHouse(user);
			HouseListVO vo = baseResult.getData();
			if (vo!=null) {
				List<HexieHouse> houseList = vo.getHou_info();
				if (houseList!=null && !houseList.isEmpty()) {
					HexieHouse hexieHouse = houseList.get(0);
					
					user.setTotalBind(houseList.size());
					user.setXiaoquName(hexieHouse.getSect_name());
					user.setProvince(hexieHouse.getProvince_name());
					user.setCity(hexieHouse.getCity_name());
					user.setCounty(hexieHouse.getRegion_name());
					user.setSectId(hexieHouse.getSect_id());	
					user.setCspId(hexieHouse.getCsp_id());
					if (!StringUtils.isEmpty(hexieHouse.getOffice_tel())) {
						user.setOfficeTel(hexieHouse.getOffice_tel());
					}
					userRepository.save(user);
					
				}
			}
		}
		
	}


}

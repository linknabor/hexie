package com.yumu.hexie.service.card.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.wechat.entity.card.ActivateReq;
import com.yumu.hexie.integration.wechat.entity.card.ActivateResp;
import com.yumu.hexie.integration.wechat.entity.card.ActivateTempInfoResp;
import com.yumu.hexie.integration.wechat.entity.card.ActivateTempInfoResp.Info.ActivateField;
import com.yumu.hexie.integration.wechat.entity.card.ActivateUrlReq;
import com.yumu.hexie.integration.wechat.entity.card.ActivateUrlResp;
import com.yumu.hexie.integration.wechat.entity.card.DecryptCodeResp;
import com.yumu.hexie.integration.wechat.entity.card.GetUserCardResp;
import com.yumu.hexie.integration.wechat.entity.card.PreActivateReq;
import com.yumu.hexie.integration.wechat.service.CardService;
import com.yumu.hexie.integration.wuye.vo.RefundDTO;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.card.WechatCard;
import com.yumu.hexie.model.card.WechatCardCatagory;
import com.yumu.hexie.model.card.WechatCardCatagoryRepository;
import com.yumu.hexie.model.card.WechatCardRepository;
import com.yumu.hexie.model.card.dto.EventGetCardDTO;
import com.yumu.hexie.model.card.dto.EventSubscribeDTO;
import com.yumu.hexie.model.card.dto.EventUpdateCardDTO;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.card.WechatCardService;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.PointService;

@Service
public class WechatCardServiceImpl implements WechatCardService {
	
	private static Logger logger = LoggerFactory.getLogger(WechatCardServiceImpl.class);
	
	@Autowired
	private WechatCardCatagoryRepository wechatCardCatagoryRepository;
	
	@Autowired
	private WechatCardRepository wechatCardRepository;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private SystemConfigService systemConfigService;
	
	@Autowired
	private GotongService gotongService;
	
	@Autowired
	private CardService cardService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private PointService pointService;
	
	/**
	 * 获取微信会员卡套。卡套相当于一个卡的模板，里面是没有积分的，只有规则。
	 * @param cardType
	 * @param appId
	 */
	@Override
	public WechatCardCatagory getWechatCardCatagoryByCardTypeAndAppId(int cardType, String appId) {

		String cardKey = appId + "_" + cardType;
		WechatCardCatagory wechatCard = (WechatCardCatagory) redisTemplate.opsForHash().get(ModelConstant.KEY_WECHAT_CARD_CATAGORY, cardKey);
		if (wechatCard == null) {
			wechatCard = wechatCardCatagoryRepository.findByCardTypeAndAppId(cardType, appId);
			if (wechatCard == null) {
				logger.error("当前公众号[" + appId + "]未配置任何微信卡券。");
			}else {
				redisTemplate.opsForHash().put(ModelConstant.KEY_WECHAT_CARD_CATAGORY, cardKey, wechatCard);
			}
		}
		return wechatCard;
	}
	
	/**
	 * 获取微信会员卡套。
	 * @param cardType
	 * @param cardId
	 */
	@Override
	public WechatCardCatagory getWechatCardCatagoryByCardId(String cardId) {

		WechatCardCatagory wechatCard = (WechatCardCatagory) redisTemplate.opsForHash().get(ModelConstant.KEY_WECHAT_CARD_CATAGORY, cardId);
		if (wechatCard == null) {
			wechatCard = wechatCardCatagoryRepository.findByCardId(cardId);
			if (wechatCard == null) {
				logger.error("未查询到当前卡券信息：[" + cardId + "]。");
			}else {
				redisTemplate.opsForHash().put(ModelConstant.KEY_WECHAT_CARD_CATAGORY, cardId, wechatCard);
			}
		}
		return wechatCard;
	}
	
	
	/**
	 * 用户关注事件
	 * 1.发客服消息发会员卡
	 * 2.发出的卡券记录到数据库
	 */
	@Transactional
	@Override
	public void eventSubscribe(EventSubscribeDTO eventSubscribeDTO) {

		User user = eventSubscribeDTO.getUser();
		WechatCardCatagory wechatCardCatagory = getWechatCardCatagoryByCardTypeAndAppId(ModelConstant.WECHAT_CARD_TYPE_MEMBER, user.getAppId());
		if (wechatCardCatagory == null) {
			throw new BizValidateException("未配置微信会员卡券。");
		}
		/*1.记录卡券倒数据库*/
		WechatCard wechatCard = wechatCardRepository.findByCardIdAndUserOpenId(wechatCardCatagory.getCardId(), user.getOpenid());
		if (wechatCard == null) {
			wechatCard = new WechatCard();
			wechatCard.setCardId(wechatCardCatagory.getCardId());
			wechatCard.setCardType(wechatCardCatagory.getCardType());
			wechatCard.setOuterStr(ModelConstant.CARD_GET_SUBSCRIBE);
			wechatCard.setUserAppId(user.getAppId());
			wechatCard.setUserOpenId(user.getOpenid());
			wechatCard.setStatus(ModelConstant.CARD_STATUS_NONE);
			wechatCardRepository.save(wechatCard);
		}
		
		/*2.发送消息给用户*/
		String accessToken = systemConfigService.queryWXAToken(user.getAppId());
		ActivateUrlReq activateUrlReq = new ActivateUrlReq();
		activateUrlReq.setCardId(wechatCardCatagory.getCardId());
		activateUrlReq.setOuterStr(ModelConstant.CARD_GET_SUBSCRIBE);
		ActivateUrlResp activateUrlResp = cardService.getMemberCardActivateUrl(activateUrlReq, accessToken);
		logger.info("activateUrlResp : " + activateUrlResp);
		if (!"0".equals(activateUrlResp.getErrcode())) {
			throw new BizValidateException("获取开卡组件失败 : " + activateUrlResp.getErrmsg());
		}
		eventSubscribeDTO.setCardId(wechatCardCatagory.getCardId());
		eventSubscribeDTO.setGetCardUrl(activateUrlResp.getUrl());
		boolean isSuccess = gotongService.sendSubscribeMsg(eventSubscribeDTO);
		if (!isSuccess) {
			throw new BizValidateException("发送卡券客服消息失败。");
		}
		
	}
	
	/**
	 * 用户领卡事件
	 * 更新现有卡状态，没有的新增
	 */
	public void eventGetCard(EventGetCardDTO eventGetCardDTO) {
		
		WechatCard wechatCard = wechatCardRepository.findByCardIdAndUserOpenId(eventGetCardDTO.getCardId(), eventGetCardDTO.getOpenid());
		if (wechatCard == null) {
			wechatCard = new WechatCard();
			WechatCardCatagory wechatCardCatagory = wechatCardCatagoryRepository.findByCardId(eventGetCardDTO.getCardId());
			if (wechatCardCatagory == null) {
				throw new BizValidateException("未配置card_id为["+eventGetCardDTO.getCardId()+"]的卡券");
			}
			wechatCard.setCardType(wechatCardCatagory.getCardType());
			wechatCard.setUserAppId(eventGetCardDTO.getAppId());
			wechatCard.setUserOpenId(eventGetCardDTO.getOpenid());
		}
		wechatCard.setCardId(eventGetCardDTO.getCardId());
		wechatCard.setCardCode(eventGetCardDTO.getCardCode());
		wechatCard.setIsRestoreMemberCard(eventGetCardDTO.getIsRestoreMemberCard());
		wechatCard.setOldCardCode(eventGetCardDTO.getOldUserCardCode());
		wechatCard.setOuterStr(eventGetCardDTO.getOutStr());
		wechatCard.setStatus(ModelConstant.CARD_STATUS_GET);
		wechatCard.setSourceScene(eventGetCardDTO.getSourceScene());
		wechatCardRepository.save(wechatCard);
		
	}

	/**
	 * 会员卡激活。
	 * 1.用返回的acitvate_ticket获取用户开卡时填写的个人信息（一般手机号就够了）
	 * 2.解码微信返回的code
	 * 3.用code去激活卡
	 * 4.根据openid做用户和卡的关联，如果没有用户的(新关注的user表没有记录，取关重新关注的则有记录)需要先生成新用户
	 * 5.更新数据库中卡状态
	 */
	@Transactional
	@Override
	public User activate(PreActivateReq preActivateReq) {
		
		WechatCard wechatCard = wechatCardRepository.findByCardIdAndUserOpenId(preActivateReq.getCardId(), preActivateReq.getOpenid());
		if (wechatCard != null && ModelConstant.CARD_STATUS_ACTIVATED == wechatCard.getStatus()) {
			logger.info("当前用户卡券已激活。卡券code:" + wechatCard.getCardCode());
			User cardUser = userRepository.findById(wechatCard.getUserId());
			if (cardUser == null) {
				List<User> cardUserList = userRepository.findByOpenid(wechatCard.getUserOpenId());
				if (cardUserList!=null && !cardUserList.isEmpty()) {
					cardUser = cardUserList.get(cardUserList.size()-1);
				}else {
					cardUser = new User();
					cardUser.setAppId(wechatCard.getUserAppId());
				}
			}
			if (StringUtils.isEmpty(cardUser.getTel())) {
				cardUser.setTel(wechatCard.getTel());
				cardUser.setRegisterDate(wechatCard.getCreateDate());
				cardUser.setPoint(wechatCard.getBonus());
				cardUser.setNewRegiste(true);
				userRepository.save(cardUser);
			}
			return cardUser;
		}
		
		//下面2个字段先要decode
		String activateTicket = preActivateReq.getActivateTicket();
		String encryptCode = preActivateReq.getEncryptCode();
		
		/*1.用返回的acitvate_ticket获取用户开卡时填写的个人信息*/
		Map<String, String> map = new HashMap<>();
		map.put("activate_ticket", activateTicket);
		WechatCardCatagory wechatCardCatagory = getWechatCardCatagoryByCardId(preActivateReq.getCardId());
		String accessToken = systemConfigService.queryWXAToken(wechatCardCatagory.getAppId());
		ActivateTempInfoResp activateTempInfoResp = cardService.getActivateTempInfo(map, accessToken);
		logger.info("activateTempInfoResp :" + activateTempInfoResp);
		if (!"0".equals(activateTempInfoResp.getErrcode())) {
			throw new BizValidateException("获取用户开卡信息失败 : " + activateTempInfoResp.getErrmsg());
		}
		List<ActivateField> fieldList = activateTempInfoResp.getInfo().getCommon_field_list();
		String mobile = "";
		for (ActivateField activateField : fieldList) {
			if ("USER_FORM_INFO_FLAG_MOBILE".equals(activateField.getName())) {	//只取一个手机号
				mobile = activateField.getValue();
				logger.info("get user mobile : " + mobile);
				break;
			}
		}
		/*2.解码微信返回的code */
		map = new HashMap<>();
		map.put("encrypt_code", encryptCode);
		DecryptCodeResp decryptCodeResp = cardService.decryptMemberCardCode(map, accessToken);	
		logger.info("decryptCodeResp : " + decryptCodeResp);
		if (!"0".equals(decryptCodeResp.getErrcode())) {
			throw new BizValidateException("卡code解码失败 : " + decryptCodeResp.getErrmsg());
		}
		
		/*3.用code激活卡。 先查看是不是老用户，老用户需要把现有芝麻转换成积分*/
		String bonusRecord = "新用户开卡积分领取。";
		List<User> userList = userRepository.findByOpenid(preActivateReq.getOpenid());
		User user = null;
		if (userList == null || userList.isEmpty()) {
			logger.info("openid: " + preActivateReq.getOpenid() + "， 用户尚未产生，将使用初始积分。");
		}else {
			user = userList.get(userList.size()-1);
			bonusRecord = "老用户积分兑换。";
		}
		//积分转换
		int points = 88;	//新用户给88积分。
		if (user != null && user.getPoint() != 0) {
			points = user.getPoint();	//老用户直接取。
		}
		String cardCode = decryptCodeResp.getCode();
		ActivateReq activateReq = new ActivateReq();
		activateReq.setCardId(preActivateReq.getCardId());
		activateReq.setCode(cardCode);
		activateReq.setMembershipNumber(cardCode);
		activateReq.setInitBonus(String.valueOf(points));
		activateReq.setInitBonusRecord(bonusRecord);
		ActivateResp activateResp = cardService.activateMemberCard(activateReq, accessToken);
		logger.info("activateResp : " + activateResp);
		if (!"0".equals(activateResp.getErrcode())) {
			throw new BizValidateException("会员卡激活失败：" + activateResp.getErrmsg());
		}
		
		/*4.根据openid做用户和卡的关联
		 * 1.如果没有用户的(新关注的user表没有记录，取关重新关注的则有记录)需要先生成新用户
		 * 2.如果有用户，但是未注册的（无手机号）的，需要绑定手机号
		 * 3.如果有用户，并且注册了的
		 */
		if (user == null) {
			user = new User();
			user.setOpenid(preActivateReq.getOpenid());
			user.setTel(mobile);
			user.setAppId(wechatCardCatagory.getAppId());
			user.setAge(0);
			user.setCityId(0l);
			user.setCountyId(0l);
			user.setCurrentAddrId(0l);
			user.setLvdou(0);
			user.setProvinceId(0l);
			user.setStatus(0);
			user.setXiaoquId(0l);
			user.setZhima(0);
			user.setRegisterDate(System.currentTimeMillis());
			user.setNewRegiste(true);
			user.setPoint(points);
			user = userRepository.save(user);
		}else if (StringUtils.isEmpty(user.getTel())) {
			user.setTel(mobile);
			user.setRegisterDate(System.currentTimeMillis());
			user.setNewRegiste(true);
			user.setPoint(points);
			user = userRepository.save(user);
		}else {
			user.setPoint(points);
			user = userRepository.save(user);
		}
		
		/*5.更新数据库中卡状态*/
		wechatCard = wechatCardRepository.findByCardIdAndUserOpenId(preActivateReq.getCardId(), preActivateReq.getOpenid());
		if (wechatCard == null) {
			//从未关注过公众号号的用户走其他途径(二维码领券等其他渠道)进入公众号领券激活会进到这里。
			logger.error("微信卡券尚未创建。 card_id : " + preActivateReq.getCardId() + ", openid : " + preActivateReq.getOpenid() + ", 将会新建卡片。");
			wechatCard = new WechatCard();
			wechatCard.setCardId(preActivateReq.getCardId());
			wechatCard.setUserOpenId(preActivateReq.getOpenid());
			wechatCard.setCardType(ModelConstant.WECHAT_CARD_TYPE_MEMBER);
			wechatCard.setOuterStr(preActivateReq.getOuterStr());	//未设置过的途径的应该是0
			wechatCard.setUserAppId(wechatCardCatagory.getAppId());
			wechatCard.setTel(mobile);
			
		}
		wechatCard.setCardCode(cardCode);
		wechatCard.setStatus(ModelConstant.CARD_STATUS_ACTIVATED);
		wechatCard.setBonus(points);
		wechatCard.setTel(mobile);
		if (user!=null) {	//老用户
			wechatCard.setUserId(user.getId());
			wechatCard.setUserName(user.getName());
		}
		wechatCardRepository.save(wechatCard);
		return user;
	}

	/**
	 * 页面获取微信会员卡激活链接
	 * @param user
	 * @return url
	 */
	@Override
	public String getActivateUrlOnPage(User user) {

		WechatCardCatagory weCardCatagory = getWechatCardCatagoryByCardTypeAndAppId(ModelConstant.WECHAT_CARD_TYPE_MEMBER, user.getAppId());
		if (weCardCatagory == null) {
			logger.info("getActivateUrlOnPage : current app doesn't support wechat member card !, appid : " + user.getAppId());
		}
		ActivateUrlReq activateUrlReq = new ActivateUrlReq();
		activateUrlReq.setCardId(weCardCatagory.getCardId());
		activateUrlReq.setOuterStr(ModelConstant.CARD_GET_REGISTER);
		String accessToken = systemConfigService.queryWXAToken(user.getAppId());
		ActivateUrlResp activateUrlResp = cardService.getMemberCardActivateUrl(activateUrlReq, accessToken);
		if (!"0".equals(activateUrlResp.getErrcode())) {
			logger.error("getActivateUrlOnPage , errmsg : " + activateUrlResp.getErrmsg());
			throw new BizValidateException("领取会员卡失败，请刷新后重试。");
		}
		return activateUrlResp.getUrl();
		
	}
	
	/**
	 * 根据用户openid获取微信会员卡
	 * @param openid
	 * @return
	 */
	@Override
	public WechatCard getWechatMemberCard(String openid) {
		
		WechatCard wechatCard = wechatCardRepository.findByCardTypeAndUserOpenId(ModelConstant.WECHAT_CARD_TYPE_MEMBER, openid);
		if (wechatCard == null) {
			wechatCard = new WechatCard();
		}
		return wechatCard;
	}

	/**
	 * 物业缴费退款
	 */
	@Override
	public void wuyeRefund(RefundDTO refundDTO) {

		String tradeWaterId = refundDTO.getTradeWaterId();
		String key = ModelConstant.KEY_WUYE_REFUND_ORDER + tradeWaterId;
		long value = stringRedisTemplate.opsForValue().increment(key, 1);
		if (value == 1) {
			String json;
			try {
				json = JacksonJsonUtil.getMapperInstance(false).writeValueAsString(refundDTO);
				stringRedisTemplate.opsForList().rightPush(ModelConstant.KEY_WUYE_REFUND_QUEUE, json);
				stringRedisTemplate.expire(key, 30, TimeUnit.MINUTES);
			} catch (JsonProcessingException e) {
				logger.error(e.getMessage(), e);
			}
			
		}
	}

	/**
	 * 用户更新卡事件
	 * 1.由于小程序端也会更新用户积分，所以先要去微信查询用户的真实积分
	 * 2.接步骤1，如果微信返回的用户积分和本地数据库里记录的不一样，说明是小程序端更新了积分或之前由于其他原因导致积分更新没有被通知到。需要做一次积分同步的操作，并记下该条同步记录
	 * 3.接步骤1，如果微信返回的用户积分和本地数据库里记录的一致，则说明是公众号的积分操作，不做更新。
	 */
	@Transactional
	@Override
	public void eventUpdateCard(EventUpdateCardDTO eventUpdateCardDTO) {

		//1.去微信查询用户当前的卡券积分
		String cardId = eventUpdateCardDTO.getCardId();
		String cardCode = eventUpdateCardDTO.getCardCode();
		
		WechatCard wechatCard = wechatCardRepository.findByCardCode(cardCode);
		if (wechatCard == null) {
			throw new BizValidateException("数据库中未查询到当前卡号["+cardCode+"]的会员卡。 eventUpdateCardDTO : " + eventUpdateCardDTO);
		}
		Map<String ,String> requestMap = new HashMap<>();
		requestMap.put("card_id", cardId);
		requestMap.put("code", cardCode);
		String accessToken = systemConfigService.queryWXAToken(eventUpdateCardDTO.getAppId());
		GetUserCardResp getUserCardResp = cardService.getUserCardInfo(requestMap, accessToken);
		logger.info("getUserCardResp : " + getUserCardResp);
		if (!"0".equals(getUserCardResp.getErrcode())) {
			throw new BizValidateException("查询会员卡详细信息失败：" + getUserCardResp.getErrmsg());
		}
		
		String serverBonus = getUserCardResp.getBonus();	//微信的积分
		int localBonus = wechatCard.getBonus();	//本地积分
		int wechatBonus = Integer.parseInt(serverBonus);
		if (localBonus == wechatBonus) {	
			logger.info("本地积分与微信积分相同，将跳过积分同步。");
			return;
		}
		
		/*
		 * 2.本地积分与微信积分不同，需要同步
		 */
		int increment = wechatBonus - localBonus;	//本次增量
		User user = new User();
		user.setOpenid(eventUpdateCardDTO.getOpenid());
		user.setAppId(eventUpdateCardDTO.getAppId());
		String key = "syncWechatPoint-" + eventUpdateCardDTO.getCreateTime();
		pointService.updatePoint(user, String.valueOf(increment), key, false);	//false代表不通知微信，仅仅本地更新
	
	
	}


}
	
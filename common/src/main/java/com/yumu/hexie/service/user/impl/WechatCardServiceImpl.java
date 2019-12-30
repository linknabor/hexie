package com.yumu.hexie.service.user.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yumu.hexie.integration.wechat.entity.card.ActivateReq;
import com.yumu.hexie.integration.wechat.entity.card.ActivateResp;
import com.yumu.hexie.integration.wechat.entity.card.ActivateUrlReq;
import com.yumu.hexie.integration.wechat.entity.card.ActivateUrlResp;
import com.yumu.hexie.integration.wechat.entity.card.DecryptCodeResp;
import com.yumu.hexie.integration.wechat.entity.card.PreActivateReq;
import com.yumu.hexie.integration.wechat.service.CardService;
import com.yumu.hexie.integration.wechat.vo.SubscribeVO;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.model.user.WechatCard;
import com.yumu.hexie.model.user.WechatCardCatagory;
import com.yumu.hexie.model.user.WechatCardCatagoryRepository;
import com.yumu.hexie.model.user.WechatCardRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.WechatCardService;

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
	
	/**
	 * 获取微信会员卡套。卡套相当于一个卡的模板，里面是没有积分的，只有规则。
	 * @param cardType
	 * @param appId
	 */
	@Override
	public WechatCardCatagory getWechatCardCatagoryByCardTypeAndAppId(int cardType, String appId) {

		String cardKey = appId + "_" + cardType;
		WechatCardCatagory wechatCard = (WechatCardCatagory) redisTemplate.opsForHash().get(ModelConstant.KEY_WECHAT_CARD, cardKey);
		if (wechatCard == null) {
			wechatCard = wechatCardCatagoryRepository.findByCardTypeAndAppId(cardType, appId);
			if (wechatCard == null) {
				logger.error("当前公众号[" + appId + "]未配置任何微信卡券。");
			}else {
				redisTemplate.opsForHash().put(ModelConstant.KEY_WECHAT_CARD, cardKey, wechatCard);
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
	public WechatCardCatagory getWechatCardCatagoryByCardTypeAndCardId(int cardType, String cardId) {

		String cardKey = cardId + "_" + cardType;
		WechatCardCatagory wechatCard = (WechatCardCatagory) redisTemplate.opsForHash().get(ModelConstant.KEY_WECHAT_CARD, cardKey);
		if (wechatCard == null) {
			wechatCard = wechatCardCatagoryRepository.findByCardTypeAndCardId(cardType, cardId);
			if (wechatCard == null) {
				logger.error("未查询到当前卡券信息：[" + cardId + "]。");
			}else {
				redisTemplate.opsForHash().put(ModelConstant.KEY_WECHAT_CARD, cardKey, wechatCard);
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
	public void subscribeEvent(SubscribeVO subscribeVO) {

		User user = subscribeVO.getUser();
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
			wechatCard.setStatus(ModelConstant.CARD_STATUS_SENT);
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
		subscribeVO.setCardId(wechatCardCatagory.getCardId());
		subscribeVO.setGetCardUrl(activateUrlResp.getUrl());
		boolean isSuccess = gotongService.sendSubscribeMsg(subscribeVO);
		if (!isSuccess) {
			throw new BizValidateException("发送卡券客服消息失败。");
		}
		
	}

	/**
	 * 会员卡激活。
	 * 1.先解码微信返回的code
	 * 2.用code去激活卡
	 * 3.更新数据库中卡状态
	 */
	@Transactional
	@Override
	public void acctivate(PreActivateReq preActivateReq) {
		
		WechatCard wechatCard = wechatCardRepository.findByCardIdAndUserOpenId(preActivateReq.getCardId(), preActivateReq.getOpenid());
		if (wechatCard != null && ModelConstant.CARD_STATUS_ACTIVATED == wechatCard.getStatus()) {
			logger.info("当前用户卡券已激活。卡券code:" + wechatCard.getCardCode());
			return;
		}
		
		/*1.解码微信返回的code */
		String encryptCode = preActivateReq.getEncryptCode();
		try {
			encryptCode = URLDecoder.decode(encryptCode, "utf-8");
			
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		Map<String, String> map = new HashMap<>();
		map.put("encrypt_code", encryptCode);
		WechatCardCatagory wechatCardCatagory = getWechatCardCatagoryByCardTypeAndCardId(ModelConstant.WECHAT_CARD_TYPE_MEMBER, preActivateReq.getCardId());
		String accessToken = systemConfigService.queryWXAToken(wechatCardCatagory.getAppId());
		DecryptCodeResp decryptCodeResp = cardService.decryptMemberCardCode(map, accessToken);	
		logger.info("decryptCodeResp : " + decryptCodeResp);
		if (!"0".equals(decryptCodeResp.getErrcode())) {
			throw new BizValidateException("卡code解码失败 : " + decryptCodeResp.getErrmsg());
		}
		
		/*2.用code激活卡*/
		//先查看是不是老用户，老用户需要把现有芝麻转换成积分
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
		int points = 0;
		if (user != null) {
			points = convertZhima(user.getZhima());
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
		
		/*3.更新数据库中卡状态*/
		wechatCard = wechatCardRepository.findByCardIdAndUserOpenId(preActivateReq.getCardId(), preActivateReq.getOpenid());
		wechatCard.setCardCode(cardCode);
		wechatCard.setStatus(ModelConstant.CARD_STATUS_ACTIVATED);
		if (user!=null) {
			wechatCard.setUserId(user.getId());
			wechatCard.setUserName(user.getName());
			user.setPoints(points);
			userRepository.save(user);
		}
		wechatCardRepository.save(wechatCard);
	}

	/**
	 * 芝麻转换成积分
	 * @param zhima
	 * @return
	 */
	private int convertZhima(int zhima) {
		int points = zhima;
		if (points == 0) {
			points = 88;
		}else if (points < 800) {
			points = 800;
		}else if (points > 8800) {
			points = 8800;
		}
		return points;
	}

}

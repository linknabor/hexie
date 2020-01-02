package com.yumu.hexie.service.card.impl;

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
import com.yumu.hexie.integration.wechat.entity.card.ActivateTempInfoResp;
import com.yumu.hexie.integration.wechat.entity.card.ActivateTempInfoResp.Info.ActivateField;
import com.yumu.hexie.integration.wechat.entity.card.ActivateUrlReq;
import com.yumu.hexie.integration.wechat.entity.card.ActivateUrlResp;
import com.yumu.hexie.integration.wechat.entity.card.DecryptCodeResp;
import com.yumu.hexie.integration.wechat.entity.card.PreActivateReq;
import com.yumu.hexie.integration.wechat.service.CardService;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.card.WechatCard;
import com.yumu.hexie.model.card.WechatCardCatagory;
import com.yumu.hexie.model.card.WechatCardCatagoryRepository;
import com.yumu.hexie.model.card.WechatCardRepository;
import com.yumu.hexie.model.card.dto.EventGetCardDTO;
import com.yumu.hexie.model.card.dto.EventSubscribeDTO;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.card.WechatCardService;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.exception.BizValidateException;

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
			wechatCard.setCardCode(eventGetCardDTO.getCardCode());
			wechatCard.setCardId(eventGetCardDTO.getCardId());
			
			WechatCardCatagory wechatCardCatagory = wechatCardCatagoryRepository.findByCardId(eventGetCardDTO.getCardId());
			wechatCard.setCardType(wechatCardCatagory.getCardType());
			wechatCard.setUserAppId(eventGetCardDTO.getAppId());
			wechatCard.setUserOpenId(eventGetCardDTO.getOpenid());
		}
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
	public String acctivate(PreActivateReq preActivateReq) {
		
		WechatCard wechatCard = wechatCardRepository.findByCardIdAndUserOpenId(preActivateReq.getCardId(), preActivateReq.getOpenid());
		if (wechatCard != null && ModelConstant.CARD_STATUS_ACTIVATED == wechatCard.getStatus()) {
			logger.info("当前用户卡券已激活。卡券code:" + wechatCard.getCardCode());
			return "";
		}
		
		//下面2个字段先要decode
		String activateTicket = preActivateReq.getActivateTicket();
		String encryptCode = preActivateReq.getEncryptCode();
		try {
			activateTicket = URLDecoder.decode(activateTicket, "utf-8");
			encryptCode = URLDecoder.decode(encryptCode, "utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
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
		
		if (true) {	//just for test activate
			return wechatCardCatagory.getAppId();
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
		
		/*4.根据openid做用户和卡的关联，如果没有用户的(新关注的user表没有记录，取关重新关注的则有记录)需要先生成新用户*/
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
			user.setPoints(points);
			user = userRepository.save(user);
		}
		
		/*5.更新数据库中卡状态*/
		wechatCard = wechatCardRepository.findByCardIdAndUserOpenId(preActivateReq.getCardId(), preActivateReq.getOpenid());
		if (wechatCard == null) {
			//从未关注过公众号号的用户走其他途径(二维码领券等其他渠道)进入公众号领券激活会进到这里。
			logger.error("微信卡券尚未创建。 card_id : " + preActivateReq.getCardId() + ", openid : " + preActivateReq.getOpenid() + ", 将会新建卡片。");
			wechatCard = new WechatCard();
			wechatCard.setCardCode(preActivateReq.getCardId());
			wechatCard.setUserOpenId(preActivateReq.getOpenid());
			wechatCard.setCardType(ModelConstant.WECHAT_CARD_TYPE_MEMBER);
			wechatCard.setOuterStr(preActivateReq.getOuterStr());	//未设置过的途径的应该是0
			wechatCard.setUserAppId(wechatCardCatagory.getAppId());
			wechatCard.setTel(mobile);
			
		}
		wechatCard.setCardCode(cardCode);
		wechatCard.setStatus(ModelConstant.CARD_STATUS_ACTIVATED);
		if (user!=null) {	//老用户
			wechatCard.setUserId(user.getId());
			wechatCard.setUserName(user.getName());
		}
		wechatCardRepository.save(wechatCard);
		return wechatCard.getUserAppId();
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

package com.yumu.hexie.service.user.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.yumu.hexie.integration.wechat.entity.card.PreActivateReq;
import com.yumu.hexie.integration.wechat.service.CardService;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.WechatCardCatagory;
import com.yumu.hexie.model.user.WechatCardCatagoryRepository;
import com.yumu.hexie.service.user.WechatCardService;

public class WechatCardServiceImpl implements WechatCardService {
	
	private static Logger logger = LoggerFactory.getLogger(WechatCardServiceImpl.class);
	
	@Autowired
	private WechatCardCatagoryRepository wechatCardCatagoryRepository;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private CardService cardService;
	
	/**
	 * 获取微信会员卡套。卡套相当于一个卡的模板，里面是没有积分的，只有规则。
	 */
	@Override
	public WechatCardCatagory getWechatCardCatagory(int cardType, String appId) {

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
	 * 会员卡预激活。根据微信回调到页面的参数获取activate_ticket
	 */
	@Override
	public void preActivate(PreActivateReq preActivateReq) {
		
		
	}

}

package com.yumu.hexie.service.user.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

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

}

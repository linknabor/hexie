package com.yumu.hexie.service.user.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yumu.hexie.integration.wechat.entity.card.UpdateUserCardReq;
import com.yumu.hexie.integration.wechat.entity.card.UpdateUserCardResp;
import com.yumu.hexie.integration.wechat.service.CardService;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.card.WechatCard;
import com.yumu.hexie.model.card.WechatCardRepository;
import com.yumu.hexie.model.user.PointRecord;
import com.yumu.hexie.model.user.PointRecordRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.PointService;

@Service("pointService")
public class PointServiceImpl implements PointService {
	
	private static Logger logger = LoggerFactory.getLogger(PointServiceImpl.class);

	@Inject
	private PointRecordRepository pointRecordRepository;
	@Inject
	private UserRepository userRepository;
	@Autowired
	private WechatCardRepository wechatCardRepository;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private CardService cardService;
	@Autowired
	private SystemConfigService systemConfigService;
	
	/**
	 * 新增积分
	 * @param user
	 * @param point 本次增加的积分
	 * @param key 形式为：wuyePay-userId-tradeWaterId
	 * 
	 * 步骤：
	 * 1.更新pointRecord表，记录本次更新的流水
	 * 2.更新user表的point字段
	 * 3.更新wechatCard表的bonus字段
	 * 4.请求微信更新会员卡积分
	 */
	@Override
	@Transactional
	public void addPoint(User user, String point, String key) {
		
		PointRecord pr = new PointRecord();
		pr.setKeyStr(key);
		List<PointRecord> prList = pointRecordRepository.findAllByKeyStr(key);
		if (prList!=null && !prList.isEmpty()) {
			return;
		}
		
		BigDecimal bigPoint = new BigDecimal(point);
		bigPoint = bigPoint.setScale(0, RoundingMode.HALF_UP);	//金额四舍五入，只保留整数部分
		int addPoint = bigPoint.intValue();	//本次要增加的积分
		
		if (addPoint == 0) {
			logger.info("本次缴费金额：" + point + "元，产生积分：" + addPoint + "。will skip .");
			return;
		}
		
		User currentUser = userRepository.findOne(user.getId());
		int totalPoint = currentUser.getPoint() + addPoint;	//需要设置的积分全量值，传入的数值会直接显示
		
		//0.请求微信更新微信卡券积分
		WechatCard wechatCard = wechatCardRepository.findByCardTypeAndUserOpenId(ModelConstant.WECHAT_CARD_TYPE_MEMBER, currentUser.getOpenid());
		boolean needUpdateCard = false;
		if (wechatCard == null || ModelConstant.CARD_STATUS_ACTIVATED != wechatCard.getStatus()) {
			logger.error("当前用户尚未领取会员卡或者会员卡尚未激活，将跳过与微信同步会员卡积分。");
		}else {
			needUpdateCard = true;
		}
		
		//1.积分记录
		pr = new PointRecord();
		pr.setType(ModelConstant.POINT_TYPE_JIFEN);
		pr.setUserId(currentUser.getId());
		pr.setPoint(bigPoint.intValue());
		pr.setKeyStr(key);
		pointRecordRepository.save(pr);
		
		//2.用户表积分字段更新
		int ret = userRepository.updatePointByUserId(addPoint, currentUser.getId(), currentUser.getPoint());
		if (ret == 0) {
			throw new BizValidateException("更新用户积分失败， 用户ID:" + currentUser.getId() + ", keyStr : " + key);
		}
		
		//3.卡券表
		if (needUpdateCard) {
			int currBonus = wechatCard.getBonus();
			int retcard = wechatCardRepository.updateCardByCardCode(addPoint, wechatCard.getCardCode(), currBonus);
			if (retcard == 0) {
				throw new BizValidateException("更新用户卡券积分失败， card code:" + wechatCard.getCardCode() + ", keyStr : " + key);
			}
		}
		
		//4.请求微信更新会员卡积分。这个放在最后一步，以防上面报错回滚
		UpdateUserCardReq updateUserCardReq = new UpdateUserCardReq();
		updateUserCardReq.setCardId(wechatCard.getCardId());
		updateUserCardReq.setCode(wechatCard.getCardCode());
		updateUserCardReq.setAddBonus(String.valueOf(addPoint));
		updateUserCardReq.setBonus(String.valueOf(totalPoint));
		updateUserCardReq.setRecordBonus("消费" + point + "元，获得" + addPoint + "积分。");
		String accessToken = systemConfigService.queryWXAToken(wechatCard.getUserAppId());
		UpdateUserCardResp updateUserCardResp = cardService.updateUserMemeberCard(updateUserCardReq, accessToken);
		logger.info("updateUserCardResp : " + updateUserCardResp);
		if (!"0".equals(updateUserCardResp.getErrcode())) {
			throw new BizValidateException("同步微信会员卡积分失败， errmsg : " + updateUserCardResp.getErrmsg());
		}
		
		stringRedisTemplate.expire(key, 30, TimeUnit.MINUTES);
	
		
	}
	
	public static void main(String[] args) {
		
		String point = "99.49";
		BigDecimal bigPoint = new BigDecimal(point);
		bigPoint = bigPoint.setScale(0, RoundingMode.HALF_UP);	//金额四舍五入
		System.out.println(bigPoint);
	}
	
}

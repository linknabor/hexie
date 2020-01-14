package com.yumu.hexie.service.user.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.inject.Inject;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.JacksonJsonUtil;
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
	private CardService cardService;
	@Autowired
	private SystemConfigService systemConfigService;
	
	
	private void addZhima(User user, int point, String key) {
		if(!StringUtils.isEmpty(key)) {
			List<PointRecord> rs = pointRecordRepository.findAllByKeyStr(key);
			if(rs != null&&rs.size()>0) {
				return;
			}
		}
		PointRecord pr = new PointRecord();
		pr.setType(ModelConstant.POINT_TYPE_ZIMA);
		pr.setUserId(user.getId());
		pr.setPoint(point);
		pr.setKeyStr(key);
		pointRecordRepository.save(pr);
		user.setZhima(user.getZhima()+point);
		userRepository.save(user);
	}
	
	/**
	 * 新增积分
	 * @param user
	 * @param point 本次修改的积分，退款请传入负值
	 * @param key 形式为：wuyePay-tradeWaterId
	 * @param isRefund 是否退款
	 * 
	 * 步骤：
	 * 1.更新pointRecord表，记录本次更新的流水
	 * 2.更新user表的point字段
	 * 3.更新wechatCard表的bonus字段
	 * 4.请求微信更新会员卡积分
	 */
	@Override
	@Transactional
	public void updatePoint(User user, String point, String key) {
		
		if (systemConfigService.isCardServiceAvailable(user.getAppId())) {
			updatePoint(user, point, key, true);
		}else {
			if (new BigDecimal(point).compareTo(BigDecimal.ZERO) > 0) {
				addZhima(user, Integer.parseInt(point), key);
			}else {
				//未开通卡券服务的公众号，如果是退款操作不做任何处理
			}
			
		}
		
	}
	
	/**
	 * 新增积分
	 * @param user
	 * @param point 本次修改的积分，退款请传入负值
	 * @param key 形式为：wuyePay-userId-tradeWaterId
	 * @param notifyWechat 是否通知微信
	 * 
	 * 步骤：
	 * 1.更新pointRecord表，记录本次更新的流水
	 * 2.更新wechatCard表的bonus字段
	 * 3.更新user表的point字段
	 * 4.请求微信更新会员卡积分
	 */
	@Override
	@Transactional
	public void updatePoint(User user, String point, String key, boolean notifyWechat) {
		
		if (StringUtils.isEmpty(user.getAppId())) {	//这一段是给物业退款用的，因为退款传过来的只有用户的wuyeId，没有其他信息
			List<User> wuyeUserList = userRepository.findByWuyeId(user.getWuyeId());
			if (wuyeUserList == null || wuyeUserList.isEmpty()) {
				logger.warn("未查询到物业ID为[" +user.getWuyeId()+"]的用户。");
				return;
			}
			user = wuyeUserList.get(wuyeUserList.size()-1);
		}
		
		boolean isCardServiceAvailable = systemConfigService.isCardServiceAvailable(user.getAppId());
		if (!isCardServiceAvailable) {
			logger.info("当前公众号["+user.getAppId()+"]未开通会员卡服务， will skip.");
			return;
		}
		PointRecord pr = new PointRecord();
		pr.setKeyStr(key);
		List<PointRecord> prList = pointRecordRepository.findAllByKeyStr(key);
		if (prList!=null && !prList.isEmpty()) {
			logger.info("key:"+key+"重复，本次积分已有过更新，will skip .");
			return;
		}
		
		BigDecimal bigPoint = new BigDecimal(point);
		bigPoint = bigPoint.setScale(0, RoundingMode.HALF_UP);	//金额四舍五入，只保留整数部分
		int addPoint = bigPoint.intValue();	//本次要增加的积分
		
		if (addPoint == 0) {
			logger.info("本次更新金额：" + point + "元，产生积分：" + addPoint + "。will skip .");
			return;
		}
		
		User currentUser = userRepository.findOne(user.getId());
		if (currentUser == null) {
			List<User> userList = userRepository.findByOpenid(user.getOpenid());
			if (userList == null || userList.isEmpty()) {
				logger.error("根据user id 以及 openid 都无法查询到当前用户, user : " + user);
				currentUser = new User();
				currentUser.setOpenid(user.getOpenid());
			}else {
				currentUser = userList.get(userList.size()-1);
			}
		}
		WechatCard wechatCard = wechatCardRepository.findByCardTypeAndUserOpenId(ModelConstant.WECHAT_CARD_TYPE_MEMBER, currentUser.getOpenid());
		int currPoint = wechatCard.getBonus();
		int totalPoint = currPoint + addPoint;	//需要设置的积分全量值，传入的数值会直接显示
		
		//1.积分记录
		pr = new PointRecord();
		pr.setType(ModelConstant.POINT_TYPE_JIFEN);
		pr.setUserId(currentUser.getId());
		pr.setKeyStr(key);
		pr.setPoint(bigPoint.intValue());
		pr.setPointSnapshot(currPoint);
		pointRecordRepository.save(pr);
		
		//2.卡券表
		boolean needUpdateCard = false;
		if (wechatCard == null || ModelConstant.CARD_STATUS_ACTIVATED != wechatCard.getStatus()) {
			logger.error("当前用户尚未领取会员卡或者会员卡尚未激活，将跳过与微信同步会员卡积分。");
		}else {
			needUpdateCard = true;
		}
		if (needUpdateCard) {
			int currBonus = wechatCard.getBonus();
			if (currBonus != currPoint) {
				logger.error("当前用户表中的积分 与 卡券表中的积分不同。");
				//向微信查询 TODO
			}
			int retcard = wechatCardRepository.updateCardByCardCodeIncremently(addPoint, wechatCard.getCardCode(), currBonus);
			if (retcard == 0) {
				throw new BizValidateException("更新用户卡券积分失败， card code:" + wechatCard.getCardCode() + ", keyStr : " + key);
			}
		}
		
		
		//3.用户表积分字段更新
		if (currentUser.getId() == 0) {
			logger.info("未查询到用户["+user.getOpenid()+"]，将跳过更新user表point字段。");
		}else {
			int ret = userRepository.updatePointByTotal(totalPoint, currentUser.getId());
			if (ret == 0) {
				throw new BizValidateException("更新用户积分失败， 用户ID:" + currentUser.getId() + ", keyStr : " + key);
			}
		}
		
		//4.请求微信更新会员卡积分。这个放在最后一步，以防上面报错回滚
		if (needUpdateCard && notifyWechat) {
			UpdateUserCardReq updateUserCardReq = new UpdateUserCardReq();
			updateUserCardReq.setCardId(wechatCard.getCardId());
			updateUserCardReq.setCode(wechatCard.getCardCode());
			updateUserCardReq.setAddBonus(String.valueOf(addPoint));
			String displayPoint = String.valueOf(totalPoint);	//负积分显示0
			if (totalPoint < 0) {
				displayPoint = "0";
			}
			updateUserCardReq.setBonus(displayPoint);
			if (addPoint > 0) {
				updateUserCardReq.setRecordBonus("本次消费" + point + "元，获得" + addPoint + "积分。");
			}else {
				updateUserCardReq.setRecordBonus("本次退款" + point + "元，扣除" + addPoint + "积分。");
			}
			String accessToken = systemConfigService.queryWXAToken(wechatCard.getUserAppId());
			UpdateUserCardResp updateUserCardResp = cardService.updateUserMemeberCard(updateUserCardReq, accessToken);
			logger.info("updateUserCardResp : " + updateUserCardResp);
			if (!"0".equals(updateUserCardResp.getErrcode())) {
				throw new BizValidateException("同步微信会员卡积分失败， errmsg : " + updateUserCardResp.getErrmsg());
			}
		}
	
		
	}
	
	public static void main(String[] args) throws JSONException {
		
		UpdateUserCardReq updateUserCardReq = new UpdateUserCardReq();
		updateUserCardReq.setCode("333811823730");
		updateUserCardReq.setCardId("phZbVwDCWR5PBv99JjKmRzlVosn0");
		updateUserCardReq.setAddBonus("10000");
		updateUserCardReq.setRecordBonus("for test");
		
		String json = JacksonJsonUtil.beanToJson(updateUserCardReq);
		System.out.println(json);
	}
	
	
	
}

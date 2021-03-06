package com.yumu.hexie.service.user.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.yumu.hexie.common.util.AppUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantAlipay;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.integration.wechat.entity.AccessTokenOAuth;
import com.yumu.hexie.integration.wechat.entity.card.ActivateReq;
import com.yumu.hexie.integration.wechat.entity.card.ActivateResp;
import com.yumu.hexie.integration.wechat.entity.user.UserWeiXin;
import com.yumu.hexie.integration.wechat.service.CardService;
import com.yumu.hexie.integration.wechat.service.OAuthService;
import com.yumu.hexie.integration.wuye.WuyeUtil;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.card.WechatCard;
import com.yumu.hexie.model.card.WechatCardRepository;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.common.WechatCoreService;
import com.yumu.hexie.service.coupon.CouponStrategy;
import com.yumu.hexie.service.coupon.CouponStrategyFactory;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.PointService;
import com.yumu.hexie.service.user.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {

	private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Inject
	private UserRepository userRepository;
	@Inject
	private WechatCoreService wechatCoreService;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private WechatCardRepository wechatCardRepository;
	@Autowired
	private CardService cardService;
	@Autowired
	private SystemConfigService systemConfigService;
	@Autowired
	private PointService pointService;
	@Autowired
	private CouponStrategyFactory couponStrategyFactory;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private AlipayClient alipayClient;
		
	@Value("${mainServer}")
	private Boolean mainServer;
	
	@Override
	public User getById(long uId) {
		return userRepository.findById(uId);
	}

	public List<User> getByOpenId(String openId) {
		return userRepository.findByOpenid(openId);
	}
	
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_USER_CACHED, key = "#sessonUser.openid", unless = "#result == null")
	public User getByOpenIdFromCache(User sessonUser) {
		
		User dbUser = null;
		List<User> userList = userRepository.findByOpenid(sessonUser.getOpenid());
		if (userList!=null) {
			for (User baseduser : userList) {
				if (baseduser.getId() == sessonUser.getId()) {
					dbUser = baseduser;
					break;
				}else if (baseduser.getOriUserId() == sessonUser.getId() && !ConstantWeChat.APPID.equals(baseduser.getAppId())) {	//从其他公众号迁移过来的用户，登陆时session中应该是源系统的userId，所以跟原系统的比较。
					dbUser = baseduser;
					break;
				}
			}
		}
		return dbUser;
	}

	@Override
	public User findwuyeId(String wuyeId) {
		List<User> users = userRepository.findByWuyeId(wuyeId);
		User user = new User();
		if(users.size() > 0) {
			user = users.get(0);
		}
		return user;
	}

	@Override
	public UserWeiXin getOrSubscibeUserByCode(String code) {
		return getTpSubscibeUserByCode(code, null);
	}

	@Override
	public UserWeiXin getTpSubscibeUserByCode(String code, String oriApp) {
		UserWeiXin user = wechatCoreService.getByOAuthAccessToken(code, oriApp);
		if (user == null) {
			throw new BizValidateException("微信信息不正确");
		}
		logger.info("userWeiXin is : " + user);
		return user;
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_CACHED, key = "#weixinUser.openid")
	public User updateUserLoginInfo(UserWeiXin weixinUser, String oriApp) {

		String openId = weixinUser.getOpenid();
		User userAccount = multiFindByOpenId(openId);

		if (userAccount == null) {
			userAccount = new User();
			userAccount.setOpenid(weixinUser.getOpenid());
			userAccount.setName(weixinUser.getNickname());
			userAccount.setHeadimgurl(weixinUser.getHeadimgurl());
			userAccount.setNickname(weixinUser.getNickname());
			userAccount.setSubscribe(weixinUser.getSubscribe());
			userAccount.setSex(weixinUser.getSex());
			userAccount.setCountry(weixinUser.getCountry());
			userAccount.setProvince(weixinUser.getProvince());
			userAccount.setCity(weixinUser.getCity());
			userAccount.setLanguage(weixinUser.getLanguage());
			userAccount.setSubscribe_time(weixinUser.getSubscribe_time());
			//这时候新用户还没有生成user，ID是空值,uid取uuid打MD5。
			userAccount.setShareCode(DigestUtils.md5Hex("UID[" + UUID.randomUUID() + "]"));

		} else {

			if (StringUtil.isEmpty(userAccount.getNickname())) {
				userAccount.setName(weixinUser.getNickname());
				userAccount.setHeadimgurl(weixinUser.getHeadimgurl());
				userAccount.setNickname(weixinUser.getNickname());
				userAccount.setSex(weixinUser.getSex());
				if (StringUtil.isEmpty(userAccount.getCountry()) || StringUtil.isEmpty(userAccount.getProvince())) {
					userAccount.setCountry(weixinUser.getCountry());
					userAccount.setProvince(weixinUser.getProvince());
					userAccount.setCity(weixinUser.getCity());
				}
				userAccount.setLanguage(weixinUser.getLanguage());
				// 从网页进入时下面两个值为空
				userAccount.setSubscribe_time(weixinUser.getSubscribe_time());
				userAccount.setSubscribe(weixinUser.getSubscribe());

			} else if (weixinUser.getSubscribe() != null && weixinUser.getSubscribe() != userAccount.getSubscribe()) {
				userAccount.setSubscribe(weixinUser.getSubscribe());
				userAccount.setSubscribe_time(weixinUser.getSubscribe_time());
			}
		}

		// 更新用户appId
		if (StringUtils.isEmpty(userAccount.getAppId())) {
			if (StringUtils.isEmpty(oriApp)) {
				userAccount.setAppId(ConstantWeChat.APPID); // 合协用户填这个
			} else {
				userAccount.setAppId(oriApp); // 其他系统用户填自己的appId
			}
		}
		
		//关联用户会员卡信息
		WechatCard wechatCard = wechatCardRepository.findByCardTypeAndUserOpenId(ModelConstant.WECHAT_CARD_TYPE_MEMBER, userAccount.getOpenid());
		if (wechatCard != null) {
			logger.info("user [ " + userAccount.getOpenid()+ " ] has already got card. will syn card status to user. ");
			logger.info("card status : " + wechatCard.getStatus());
			if (ModelConstant.CARD_STATUS_ACTIVATED == wechatCard.getStatus()) {
				int points = wechatCard.getBonus();
				if (points == 0) {	//新用户，送88积分
					points += 88;
				}
				userAccount.setPoint(points);
			}
			if (StringUtil.isEmpty(wechatCard.getUserId())) {
				logger.info("update card user info. card :" + wechatCard.getId());
				wechatCardRepository.updateCardUserInfo(userAccount.getId(), userAccount.getName(), wechatCard.getId());
			}
		}
		userRepository.save(userAccount);
		return userAccount;
	}

	@Override
	public User multiFindByOpenId(String openId) {
		List<User> userList = userRepository.findByOpenid(openId);
		User userAccount = null;
		if (userList != null && userList.size() > 0) {
			if (userList.size() == 1) {
				userAccount = userList.get(0);
			} else {
				userAccount = userList.get(userList.size() - 1);
			}
		}
		return userAccount;
	}

	@Override
	public User saveProfile(long userId, String nickName, int sex) {

		User user = userRepository.findById(userId);
		user.setNickname(nickName);
		user.setSex(sex);
		return userRepository.save(user);
	}

	@Override
	public User bindPhone(User user, String phone) {
		user.setTel(phone);
		if (user.getStatus() == 0 && StringUtil.isNotEmpty(user.getTel())) {
			user.setStatus(ModelConstant.USER_STATUS_BINDED);
		}
		if (!systemConfigService.isCardServiceAvailable(user.getAppId())) {
			pointService.updatePoint(user, "100", "zm-binding-" + user.getId());
		}
		return userRepository.save(user);
	}
	
	@Override
	@Async
	public void bindWuYeId(User user) {
		 //绑定物业信息
    	try {
    		if(StringUtil.isEmpty(user.getWuyeId()) ){
    			BaseResult<HexieUser> r = WuyeUtil.userLogin(user);
        		if(r.isSuccess()) {
        			User dbUser = userRepository.findById(user.getId());
        			if (dbUser != null) {
//        				dbUser.setWuyeId(r.getData().getUser_id());
//                		userRepository.save(dbUser);
        				userRepository.updateUserWuyeId(r.getData().getUser_id(), dbUser.getId());
					}
        		}
    		}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	@Override
	public UserWeiXin getOrSubscibeUserByOpenId(String appId, String openid) {

		UserWeiXin user = wechatCoreService.getUserInfo(appId, openid);
		return user;
	}

	/**
	 * @param user
	 * @return
	 * @see com.yumu.hexie.service.user.UserService#save(com.yumu.hexie.model.user.User)
	 */
	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	/**
	 * @param code
	 * @return
	 * @see com.yumu.hexie.service.user.UserService#queryByShareCode(java.lang.String)
	 */
	@Override
	public User queryByShareCode(String code) {
		List<User> users = userRepository.findByShareCode(code);
		return users.size() > 0 ? users.get(0) : null;
	}

	@Override
	public List<User> getByTel(String tel) {
		return userRepository.findByTel(tel);
	}

	public List<String> getRepeatShareCodeUser() {

		return userRepository.getRepeatShareCodeUser();
	}

	@Override
	public List<User> getShareCodeIsNull() {

		return userRepository.getShareCodeIsNull();
	}

	@Override
	public List<User> getUserByShareCode(String shareCode) {
		return userRepository.getUserByShareCode(shareCode);
	}

	/**
	 * 防止用户短时间内重复调用login接口
	 */
	@Override
	public boolean checkDuplicateLogin(HttpSession httpSession) {

		boolean isDuplicateRequest = false;
		String sessionId = httpSession.getId();
		logger.info("user session : " + sessionId);

		String key = ModelConstant.KEY_USER_LOGIN + sessionId;
		
		boolean absent = redisTemplate.opsForValue().setIfAbsent(key, sessionId, 2, TimeUnit.SECONDS);
		if (!absent) {
			isDuplicateRequest = true;	//已经存在的情况下
		}
		return isDuplicateRequest;
	}

	/**
	 * 注册
	 */
	@Override
	@Transactional
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_CACHED, key = "#user.openid")
	public User simpleRegister(User user) {

		/*查看用户是否领卡：如果已领卡，需要自动激活卡片。*/
		WechatCard wechatCard = wechatCardRepository.findByCardTypeAndUserOpenId(ModelConstant.WECHAT_CARD_TYPE_MEMBER, user.getOpenid());
		if (wechatCard == null) {
			logger.info("用户[" + user.getOpenid() + "]未领卡");
		}else {
			boolean needUpdateCard = false;
			if (StringUtil.isEmpty(wechatCard.getUserId())) {
				wechatCard.setUserId(user.getId());
				wechatCard.setUserName(user.getName());
				wechatCard.setTel(user.getTel());
				needUpdateCard = true;
			}
			logger.info("用户["+user.getOpenid()+"], card status : " + wechatCard.getStatus());
			if (ModelConstant.CARD_STATUS_GET == wechatCard.getStatus()) {	//如果已领卡，需要激活
				
				ActivateReq activateReq = new ActivateReq();
				activateReq.setCardId(wechatCard.getCardId());
				activateReq.setCode(wechatCard.getCardCode());
				int point = user.getPoint();	//新用户送88积分。老用户，积分已经做过转换，直接取lvdou的值
				if (point == 0) {	
					point = 88;
					user.setPoint(point);
				}
				activateReq.setInitBonus(String.valueOf(point));
				activateReq.setInitBonusRecord("用户积分兑换。");
				activateReq.setMembershipNumber(wechatCard.getCardCode());
				String accessToken = systemConfigService.queryWXAToken(wechatCard.getUserAppId()); 
				ActivateResp activateResp = cardService.activateMemberCard(activateReq, accessToken);
				if (!"0".equals(activateResp.getErrcode())) {
					throw new BizValidateException("创建用户失败。 errmsg : " + activateResp.getErrmsg());
				}
				wechatCard.setStatus(ModelConstant.CARD_STATUS_ACTIVATED);
				wechatCard.setBonus(point);
				needUpdateCard = true;
			}
			if (needUpdateCard) {
				wechatCardRepository.save(wechatCard);
			}
			
		}
		
		CouponStrategy registerCouponStrategy = couponStrategyFactory.getRegisterStrategy(user);
		registerCouponStrategy.sendCoupon(user);
		user.setRegisterDate(System.currentTimeMillis());
        save(user);
        return user;
		
	}
	
	/**
	 * 获取用户授权信息(静默)
	 * @param code
	 * @param appid
	 * @return
	 */
	@Override
	public AccessTokenOAuth getAccessTokenOAuth(String code, String appid){
		
		Assert.hasText(code, "code不能为空。");
		Assert.hasText(appid, "appid不能为空。");
		AccessTokenOAuth  auth = null;
		if (!AppUtil.isMainApp(appid)) {
			String componentAccessToken = redisRepository.getComponentAccessToken(ConstantWeChat.KEY_COMPONENT_ACESS_TOKEN);
			auth = OAuthService.getOAuthAccessToken(code, appid, componentAccessToken);
		}
		return auth;
	}
	
	@Override
	public AccessTokenOAuth getAlipayAuth(String code) {
		
		Assert.hasText(code, "code不能为空。");
		AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
		request.setCode(code);
		request.setGrantType(ConstantAlipay.AUTHORIZATION_TYPE);
		AccessTokenOAuth oAuth = new AccessTokenOAuth();
		try {
			logger.info("alipayClient: " + alipayClient);
			logger.info("grantType: " + ConstantAlipay.AUTHORIZATION_TYPE);
		    AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
		    oAuth.setOpenid(oauthTokenResponse.getUserId());
		    oAuth.setAccessToken(oauthTokenResponse.getAccessToken());
		} catch (AlipayApiException e) {
			throw new BizValidateException(e.getMessage(), e);
		}
		return oAuth;
		
	}

	@Override
	@Transactional
	public boolean eventSubscribe(User user) {
		
		boolean updated = false;
		List<User> userList = userRepository.findByOpenid(user.getOpenid());
		if (userList!=null && !userList.isEmpty()) {
			updated = true;
		}
		for (User dbuser : userList) {
			dbuser.setSubscribe(user.getSubscribe());
			dbuser.setSubscribe_time(user.getSubscribe_time());
			userRepository.save(dbuser);
		}
		return updated;
		
	}

	@Override
	@Transactional
	public boolean eventUnsubscribe(User user) {
		
		boolean updated = false;
		List<User> userList = userRepository.findByOpenid(user.getOpenid());
		if (userList!=null && !userList.isEmpty()) {
			updated = true;
		}
		for (User dbuser : userList) {
			dbuser.setSubscribe(user.getSubscribe());
			dbuser.setUnsubscribeDate(user.getUnsubscribeDate());
			userRepository.save(dbuser);
		}
		return updated;
		
	}


}

package com.yumu.hexie.service.user.impl;

import java.util.List;
import java.util.Map;
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
import com.yumu.hexie.integration.wechat.entity.MiniUserPhone;
import com.yumu.hexie.integration.wechat.entity.UserMiniprogram;
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
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.event.dto.BaseEventDTO;
import com.yumu.hexie.model.market.RgroupCart;
import com.yumu.hexie.model.redis.Keys;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.user.MiniUserPageAccess;
import com.yumu.hexie.model.user.OrgOperator;
import com.yumu.hexie.model.user.OrgOperatorRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.common.WechatCoreService;
import com.yumu.hexie.service.coupon.CouponStrategy;
import com.yumu.hexie.service.coupon.CouponStrategyFactory;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.page.PageConfigService;
import com.yumu.hexie.service.user.PointService;
import com.yumu.hexie.service.user.RegionService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.service.user.req.SwitchSectReq;

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
	@Autowired
	private RegionService regionService;
	@Autowired
	private PageConfigService pageConfigService;
	@Autowired
	private OrgOperatorRepository orgOperatorRepository;
	
	@Value("${mainServer}")
	private Boolean mainServer;
	
	@Value("${wechat.miniprogramAppId}")
    private String miniprogramAppid;
	
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
		if (userAccount == null) {	//如果是空，根据unionid再差一遍
			if (!StringUtils.isEmpty(weixinUser.getUnionid())) {
				userAccount = getByUnionid(weixinUser.getUnionid());
			}
		}

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
			userAccount.setUnionid(weixinUser.getUnionid());
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
				if (!StringUtils.isEmpty(weixinUser.getUnionid())) {
					userAccount.setUnionid(weixinUser.getUnionid());
				}
				// 从网页进入时下面两个值为空
				userAccount.setSubscribe_time(weixinUser.getSubscribe_time());
				userAccount.setSubscribe(weixinUser.getSubscribe());
				if (StringUtils.isEmpty(userAccount.getOpenid())) {
					userAccount.setOpenid(weixinUser.getOpenid());	//如果小程序用户先创建，这个用户是没有openid的，后续从公众号登陆进来要更新openid
				}

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
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_CACHED, key = "#user.openid")
	public String bindWuYeIdSync(User user) {
		 //绑定物业信息
		String wuyeId = "";
    	try {
    		if(StringUtil.isEmpty(user.getWuyeId()) ){
    			BaseResult<HexieUser> r = WuyeUtil.userLogin(user);
        		if(r.isSuccess()) {
        			User dbUser = userRepository.findById(user.getId());
        			if (dbUser != null) {
//        				dbUser.setWuyeId(r.getData().getUser_id());
//                		userRepository.save(dbUser);
        				userRepository.updateUserWuyeId(r.getData().getUser_id(), dbUser.getId());
        				wuyeId = r.getData().getUser_id();
					}
        		}
    		}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
    	return wuyeId;
	}
	

	@Override
	public UserWeiXin getOrSubscibeUserByOpenId(String appId, String openid) {

		UserWeiXin user = wechatCoreService.getUserInfo(appId, openid);
		return user;
	}

	/**
	 * @param user
	 * @return
	 * @see UserService#save(User)
	 */
	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	/**
	 * @param code
	 * @return
	 * @see UserService#queryByShareCode(String)
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
	public boolean checkDuplicateLogin(HttpSession httpSession, String code) {

		boolean isDuplicateRequest = false;
		String sessionId = httpSession.getId();
		logger.info("user session : " + sessionId);

		String key = ModelConstant.KEY_USER_LOGIN + sessionId+"_"+code;	//检查code,一个code只能在一次登陆中使用，如果重复，则不再向腾讯请求
		
		boolean absent = redisTemplate.opsForValue().setIfAbsent(key, 1, 1, TimeUnit.HOURS);
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
	
	@Override
	@Transactional
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_CACHED, key = "#user.openid")
	public User switchSect(User user, SwitchSectReq switchSectReq) {
		
		User dbUser = userRepository.findById(user.getId());
		if (!dbUser.getSectId().equals(switchSectReq.getSectId())) {
			dbUser.setXiaoquName(switchSectReq.getSectName());
			dbUser.setProvince(switchSectReq.getProvince());
			dbUser.setCity(switchSectReq.getCity());
			dbUser.setCounty(switchSectReq.getCounty());
			dbUser.setSectId(switchSectReq.getSectId());	
			dbUser.setCspId(switchSectReq.getCspId());
			dbUser.setOfficeTel(switchSectReq.getOfficeTel());
			List<Region> regionList = regionService.findAllBySectId(switchSectReq.getSectId());
			if (regionList != null && regionList.size() > 0) {
				Region region = regionList.get(0);
				dbUser.setXiaoquId(region.getId());
			}
			dbUser = userRepository.save(dbUser);
		}
		return dbUser;
		
	}
	
	@Override
    public UserMiniprogram getWechatMiniUserSessionKey(String code) throws Exception {
        Assert.hasText(code, "code不能为空。");
        return wechatCoreService.getMiniUserSessionKey(code);
    }

    @Override
    @Transactional
    public User saveMiniUserSessionKey(UserMiniprogram miniUser) {
    	logger.info("miniUser : " + miniUser);
        String unionid = miniUser.getUnionid();	
        String miniopenid = miniUser.getOpenid();
        User userAccount = null;
        if (!StringUtils.isEmpty(unionid)) {
        	userAccount = getByUnionid(unionid);
		}
        if (userAccount == null) {
        	//这种情况适用于没有unionid的小程序，即没有在开放平台绑定公众号和小程序的情况
			if (!StringUtils.isEmpty(miniopenid)) {
				userAccount = getByMiniopenid(miniopenid);
			}
		}
        if (userAccount == null) {
            userAccount = new User();
            userAccount.setOpenid("0");    //TODO
            userAccount.setUnionid(miniUser.getUnionid());
            userAccount.setMiniopenid(miniUser.getOpenid());
            userAccount.setMiniAppId(miniprogramAppid);
            userAccount.setShareCode(DigestUtils.md5Hex("UID[" + UUID.randomUUID() + "]"));
        } else {
        	if(StringUtils.isEmpty(userAccount.getMiniopenid())) {
                userAccount.setMiniopenid(miniUser.getOpenid());
                userAccount.setMiniAppId(miniprogramAppid);
        	}
        	if (StringUtil.isEmpty(userAccount.getUnionid())) {
        		userAccount.setUnionid(miniUser.getUnionid());
			}
        }
        String key = ModelConstant.KEY_USER_SESSION_KEY + userAccount.getUnionid();
        String savedSessionKey = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(savedSessionKey) || !savedSessionKey.equals(miniUser.getSessionKey())) {
            redisTemplate.opsForValue().set(key, miniUser.getSessionKey(), 71, TimeUnit.HOURS);    //官方3天失效，也就是72小时
        }
        userRepository.save(userAccount);
        return userAccount;
    }

    /**
	 * 重新缓存user，如果用户在建立小程序用户之前，已经有了公众号用户
	 * @param user
	 * @return
	 */
	@Override
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_CACHED, key = "#user.openid")
	public void recacheMiniUser(User user) {
		
	}


    @Override
    public User getByUnionid(String unionid) {
    	List<User> list = userRepository.findByUnionid(unionid);
    	User user = null;
    	if (list!=null && list.size()>0) {
    		user = list.get(0);
		}
        return user;
    }

    /**
	 * 校验小程序用户页面访问权限
	 * 分两部分权限：1普通物业用户，2机构用户
	 * @param user
	 * @param page
	 */
	@Override
	public boolean validateMiniPageAccess(User user, String page) {
		
		logger.info("validateMiniPageAccess, user: " + user + ", page : " + page);
		
		boolean isValid = true;
		if (StringUtils.isEmpty(page)) {
			return isValid;
		}
		if(StringUtils.isEmpty(user.getRoleId()) && "pages/index/index".equals(page)) {
			return isValid;
		}
		if(StringUtils.isEmpty(user.getRoleId()) && "pages/rgroup/index".equals(page)) {
			return isValid;
		}
		if (StringUtils.isEmpty(user.getRoleId())) {
			isValid = false;
			return isValid;
		}
		if (!StringUtils.isEmpty(page)) {
			MiniUserPageAccess pageAccess = pageConfigService.getMiniPageAccess(page, user.getRoleId());
			if (pageAccess == null) {
				isValid = false;
			}
		}
		logger.info("validateMiniPageAccess, isValid 1: " + isValid + ", page : " + page);
		if (!isValid) {
			MiniUserPageAccess pageAccess = pageConfigService.getMiniPageAccess(page, user.getRoleId());
			if (pageAccess != null) {
				isValid = true;
			}
		}
		logger.info("validateMiniPageAccess, isValid 2: " + isValid + ", page : " + page);
		return isValid;
		
	}

	/**
	 * 根据用户id查询所在的机构信息，只有roleId不为空的用户去查。因为B端用户相对C端用户是极少数，数量级不在一个级别。没必要每个用户都查一遍
	 */
	@Override
	public OrgOperator getOrgOperator(User user) {
		OrgOperator operator = null;
		if (!StringUtil.isEmpty(user.getRoleId())) {
			operator = orgOperatorRepository.findByUserIdAndRoleId(user.getId(), user.getRoleId());
		}
		return operator;
		
	}
	
	/**
	 * 获取小程序用户的手机号
	 * @param code
	 * @return
	 */
	@Override
	public MiniUserPhone getMiniUserPhone(String code) {
		
		Assert.hasText(code, "授权码不能为空。");
		MiniUserPhone miniUserPhone = null;
		try {
			miniUserPhone = wechatCoreService.getMiniUserPhone(code);
			if (!StringUtils.isEmpty(miniUserPhone.getErrorcode())) {
				throw new BizValidateException(miniUserPhone.getErrmsg());
			}
			return miniUserPhone;
			
		} catch (Exception e) {
			throw new BizValidateException(e.getMessage(), e);
		}
		
	}
	
	@Override
    @Transactional
    public User saveMiniUserPhone(User user, MiniUserPhone miniUserPhone) {
        
		String phone = miniUserPhone.getPhone_info().getPhoneNumber();
		String purePhone = miniUserPhone.getPhone_info().getPurePhoneNumber();	//不带区号的手机号
		User miniUser = getById(user.getId());
        List<User> userList = getByTel(phone);	//根据手机号关联现有用户
        if (userList == null) {
        	userList = getByTel(purePhone);	//根据手机号关联现有用户
		}
        User userAccount = null;
        if (userList != null && userList.size() > 0) {	//用手机号将小程序用户和公众号用户关联起来。如果存在一对多的情况，优先关联合协的用户。
			if (userList.size() == 1) {
				userAccount = userList.get(0);
			} else {
				for (User dbUser : userList) {
					if (ConstantWeChat.APPID.equals(dbUser.getAppId())) {
						userAccount = dbUser;
						break;
					}
				}
			}
		}

        if (userAccount == null) {	//如果数据库中没有关联的用户，直接更新手机号
        	userAccount = miniUser;
        	userAccount.setTel(phone);
        } else {	//如果数据库中有关联的用户，需要合并老记录
            userAccount.setUnionid(miniUser.getUnionid());
            userAccount.setMiniopenid(miniUser.getMiniopenid());
            userAccount.setMiniAppId(miniUser.getMiniAppId());
            userAccount.setTel(phone);
            if (!StringUtils.isEmpty(miniUser.getHeadimgurl())) {
				userAccount.setHeadimgurl(miniUser.getHeadimgurl());
			}
            if (!StringUtils.isEmpty(miniUser.getNickname())) {
				userAccount.setNickname(miniUser.getNickname());
				userAccount.setName(miniUser.getNickname());
			}
            
			if (miniUser.getId()!=userAccount.getId()) {
				//删除已经登陆形成的新用户
				userRepository.deleteById(miniUser.getId());
				
				//合并用户购物车的商品
				String cartKey = Keys.uidRgroupCartKey(miniUser.getId());	//根据被合并用户的id，获取购物车
				RgroupCart cart = redisRepository.getRgroupCart(cartKey);
				if (cart != null) {
					cartKey = Keys.uidRgroupCartKey(userAccount.getId());	//更换cartKey
					redisRepository.setRgroupCart(cartKey, cart);
				}
			}
            
        }
        userRepository.flush();
        userRepository.save(userAccount);
        return userAccount;
    }
	
	@Override
    @Transactional
    @CacheEvict(cacheNames = ModelConstant.KEY_USER_CACHED, key = "#user.openid")
    public User updateUserInfo(User user, Map<String, String> map) {
		
		User dbUser = userRepository.findById(user.getId());
		String avatarUrl  = map.get("avatarUrl");
		String nickName = map.get("nickName");
		dbUser.setHeadimgurl(avatarUrl);
		dbUser.setNickname(nickName);
		dbUser.setName(nickName);
		userRepository.save(dbUser);
		return dbUser;
		
	}

	/**
	 * 通过微信关注事件，绑定小程序用户
	 * 1.已经关注过合协的用户，并且已经形成用户的，则关联小程序用户（如果小程序用户存在）
	 * 2.新关注用户，在user表中并未形成数据的，这里需要新建一个
	 */
	@Override
	@Transactional
	public boolean bindMiniUser(BaseEventDTO baseEventDTO) {
		
		String openid = baseEventDTO.getOpenid();
		String appid = baseEventDTO.getAppId();
		
		if (!ConstantWeChat.APPID.equals(appid)) {
			logger.info("appid: " + appid + ", not hexie subscribe event, will skip. ");
			return true;
		}
		
		User user = multiFindByOpenId(openid);
		if (user != null) {	
			logger.warn("user already exists, will skip. openid : " + openid);
			return true;
		}
		//未查询到用户存在两种情况：1.用户未没有产生小程序用户，2用户产生了小程序用户，但未和公众号openid关联上
		UserWeiXin userWeiXin = com.yumu.hexie.integration.wechat.service.UserService.getUserInfo(openid, systemConfigService.queryWXAToken(appid));
		if (userWeiXin == null) {
			logger.warn("can't find user from wechat, openid : " + openid);
			return true;
		}
		String unionid = userWeiXin.getUnionid();
		if (StringUtils.isEmpty(unionid)) {
			logger.warn("user does not have unionid, openid : " + openid + ", appid : " + appid);
			return true;
		}
		User miniUser = getByUnionid(unionid);
		if (miniUser == null) {
			//如果数据库中没有有关联的用户，需要新建用户
			logger.info("no hexie user, will create new user, user openid : " + openid);
			User newUser = new User();
			newUser.setOpenid(openid);
			newUser.setAppId(appid);
			newUser.setAge(20);
			newUser.setCityId(0l);
			newUser.setCountyId(0l);
			newUser.setCurrentAddrId(0l);
			newUser.setProvinceId(0l);
			newUser.setXiaoquId(0);
			newUser.setStatus(0);
			newUser.setRegisterDate(System.currentTimeMillis());
			newUser.setNewRegiste(false);
			newUser.setUnionid(unionid);
			simpleRegister(newUser);
			
		} else {
			logger.info("find miniapp user, miniopenid : " + miniUser.getMiniopenid());
			//如果数据库中有关联的用户，更新该小程序用户的openid和appid
			miniUser.setOpenid(openid);
			miniUser.setAppId(appid);
	        userRepository.save(miniUser);
		}
		
		return true;
	}

	
	/**
	 * 通过微信查看小程序事件，更新用户的unionid
	 */
	@Override
	@Transactional
	public boolean updateUserUnionid(BaseEventDTO baseEventDTO) {
		
		String openid = baseEventDTO.getOpenid();
		String appid = baseEventDTO.getAppId();
		
		if (!ConstantWeChat.APPID.equals(appid)) {
			logger.info("appid: " + appid + ", not hexie viewMiniProgram event, will skip. ");
			return true;
		}
		
		User user = multiFindByOpenId(openid);
		if (user != null) {	
			//未查询到用户存在两种情况：1.用户未没有产生小程序用户，2用户产生了小程序用户，但未和公众号openid关联上
			UserWeiXin userWeiXin = com.yumu.hexie.integration.wechat.service.UserService.getUserInfo(openid, systemConfigService.queryWXAToken(appid));
			if (userWeiXin == null) {
				logger.warn("can't find user from wechat, openid : " + openid);
				return true;
			}
			String unionid = userWeiXin.getUnionid();	//这里只对合协的用户有效，因为只有合协公众号的用户跟小程序共享unionid。其他公众号的用户，关注需要关注合协新产生一个用户
			if (StringUtils.isEmpty(unionid)) {
				logger.warn("user does not have unionid, openid : " + openid + ", appid : " + appid);
				return true;
			}
			User unionUser = getByUnionid(unionid);
			if (unionUser != null) {
				logger.warn("user unionid exists. will skip .");
				return true;
			}
			user.setUnionid(unionid);
	        userRepository.save(user);
		} else {
//			UserWeiXin userWeiXin = com.yumu.hexie.integration.wechat.service.UserService.getUserInfo(openid, systemConfigService.queryWXAToken(appid));
//			if (userWeiXin == null) {
//				logger.warn("can't find user from wechat, openid : " + openid);
//				return true;
//			}
//			String unionid = userWeiXin.getUnionid();
//			user = new User();
//			user.setOpenid(openid);
//			user.setAppId(appid);
//			user.setAge(20);
//			user.setCityId(0l);
//			user.setCountyId(0l);
//			user.setCurrentAddrId(0l);
//			user.setProvinceId(0l);
//			user.setXiaoquId(0);
//			user.setStatus(0);
//			user.setRegisterDate(System.currentTimeMillis());
//			user.setNewRegiste(false);
//			user.setUnionid(unionid);
//			simpleRegister(user);
		}
		return true;
	}
	
	@Override
	public User getByMiniopenid(String miniopenid) {
		return userRepository.findByMiniopenid(miniopenid);
	}

}

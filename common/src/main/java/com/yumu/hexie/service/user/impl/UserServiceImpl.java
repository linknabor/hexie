package com.yumu.hexie.service.user.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import com.yumu.hexie.integration.wuye.WuyeUtil2;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

import com.alipay.api.internal.util.AlipayEncrypt;
import com.yumu.hexie.common.util.AppUtil;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.alipay.entity.AliMiniUserPhone;
import com.yumu.hexie.integration.alipay.service.AuthService;
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
import com.yumu.hexie.integration.wuye.vo.HexieAddress;
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
import com.yumu.hexie.model.user.NewLionUser;
import com.yumu.hexie.model.user.NewLionUserRepository;
import com.yumu.hexie.model.user.OrgOperator;
import com.yumu.hexie.model.user.OrgOperatorRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.cache.CacheService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.common.WechatCoreService;
import com.yumu.hexie.service.coupon.CouponStrategy;
import com.yumu.hexie.service.coupon.CouponStrategyFactory;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.page.PageConfigService;
import com.yumu.hexie.service.user.AddressService;
import com.yumu.hexie.service.user.PointService;
import com.yumu.hexie.service.user.RegionService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.service.user.dto.H5UserDTO;
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
	private RegionService regionService;
	@Autowired
	private PageConfigService pageConfigService;
	@Autowired
	private OrgOperatorRepository orgOperatorRepository;
	@Autowired
	private AuthService authService;
	@Autowired
	private WuyeUtil2 wuyeUtil2;
	@Autowired
	private AddressService addressService;
	@Autowired
	private NewLionUserRepository newLionUserRepository;
	@Autowired
	private CacheService cacheService;

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
	@Cacheable(cacheNames = ModelConstant.KEY_USER_CACHED, key = "#sessonUser.openid", unless = "#dbUser == null")
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

	/**
	 * 这个函数只有公众号或者h5用户调用，只根据openid判断，没有判断miniopenid,小程序用户不要调用
	 */
	@Override
	@Transactional
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_CACHED, key = "#weixinUser.openid", condition = "#weixinUser.openid != null")
	public User updateUserLoginInfo(UserWeiXin weixinUser, String oriApp) {

		String openId = weixinUser.getOpenid();
		User userAccount = multiFindByOpenId(openId);
		if (userAccount == null) {	//如果是空，根据unionid再查一遍
			if (!StringUtils.isEmpty(weixinUser.getUnionid())) {
				List<User> users = getUsersByUnionid(weixinUser.getUnionid());
				userAccount = users.stream().filter(u -> !StringUtils.isEmpty(u.getMiniopenid()) && StringUtils.isEmpty(u.getAppId())).findFirst().orElse(null);
			}
		}
		if (userAccount == null) {
			userAccount = new User();
			String appid = StringUtils.isEmpty(oriApp)?ConstantWeChat.APPID:oriApp;
			userAccount.setAppId(appid);
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
			if (StringUtils.isEmpty(userAccount.getAppId())) {
				String appid = StringUtils.isEmpty(oriApp)?ConstantWeChat.APPID:oriApp;
				userAccount.setAppId(appid);
				userAccount.setOpenid(weixinUser.getOpenid());	//如果小程序用户先创建，这个用户是没有openid的，后续从公众号登陆进来要更新openid
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
				
			}
		}
		if (weixinUser.getOpenid().equals(userAccount.getOpenid())) {
			if(weixinUser.getSubscribe() != null && weixinUser.getSubscribe() != userAccount.getSubscribe()) {
				userAccount.setSubscribe(weixinUser.getSubscribe());
				userAccount.setSubscribe_time(weixinUser.getSubscribe_time());
			}
		}
		if (!StringUtils.isEmpty(weixinUser.getUnionid()) && StringUtils.isEmpty(userAccount.getUnionid())) {
			userAccount.setUnionid(weixinUser.getUnionid());
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
    		//清除用户缓存
    		cacheService.clearUserCache(cacheService.getCacheKey(user));
    		
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

		//把用户注册信息传入平台，目的是根据手机号绑定房子
		try {
			BaseResult<String> r = wuyeUtil2.pushUserRegisterUrl(user);
			if ("99".equals(r.getResult())) {
				logger.error(r.getMessage());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
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
		
		return getAlipayAuth(null, code);
		
	}
		
	@Override
	public AccessTokenOAuth getAlipayAuth(String appid, String code) {
		
		return authService.getAlipayAuth(appid, code);
		
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
	
	/**
	 * @param user
	 * @param openid 用于redisCache的key，不要移除这个参数
	 * @param switchSectReq
	 */
	@Override
	@Transactional
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_CACHED, key = "#openid")
	public User switchSect(User user, String openid, SwitchSectReq switchSectReq) {
		
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
    public UserMiniprogram getWechatMiniUserSessionKey(String miniAppid, String code) throws Exception {
		Assert.hasText(miniAppid, "miniappid不能为空。");
		Assert.hasText(code, "code不能为空。");
        return wechatCoreService.getMiniUserSessionKey(miniAppid, code);
    }

    @Override
    @Transactional
    public User saveMiniUserSessionKey(UserMiniprogram miniUser) {
    	logger.info("miniUser : " + miniUser);
        String unionid = miniUser.getUnionid();	
        String miniopenid = miniUser.getOpenid();
        String miniappid = miniUser.getAppid();
        User userAccount = null;
        List<User> users = null;
        if (!StringUtils.isEmpty(unionid)) {
        	users = getUsersByUnionid(unionid);
		}
      //这种情况适用于没有unionid的小程序，即没有在开放平台绑定公众号和小程序的情况
        if (users == null || users.isEmpty()) {
			if (!StringUtils.isEmpty(miniopenid)) {
				userAccount = getByMiniopenid(miniopenid);
				if (userAccount == null) {
		            userAccount = new User();
		            userAccount.setOpenid("0");    //TODO
		            userAccount.setUnionid(miniUser.getUnionid());
		            userAccount.setMiniopenid(miniUser.getOpenid());
		            userAccount.setMiniAppId(miniUser.getAppid());
		            userAccount.setShareCode(DigestUtils.md5Hex("UID[" + UUID.randomUUID() + "]"));
		        }
			}
		} 
        /*
         * 	unionid一对多的情况
         * 1.已有小程序用户(miniopenid有值的情况)，不做更新
         * 2.都没miniopenid的情况下，优先更新合协那条,没有合协的，则更新后一条
         */
        else {
        	if (users.size() == 1) {
        		userAccount = users.get(0);
			} else {
				//判断登录小程序用户的小程序appid是否和库里的相同
				userAccount = users.stream().filter(u -> miniappid.equals(u.getMiniAppId())).findFirst().orElse(null);
				if (userAccount == null) {
					userAccount = users.stream().findFirst().orElse(null);
				}
			}
        	if (userAccount != null && StringUtils.isEmpty(userAccount.getMiniopenid())) {
                userAccount.setMiniopenid(miniUser.getOpenid());
                userAccount.setMiniAppId(miniUser.getAppid());
			}
        }
        String key = ModelConstant.KEY_USER_SESSION_KEY + userAccount.getMiniopenid();
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
	public void recacheMiniUser(User user) {
		cacheService.clearUserCache(cacheService.getCacheKey(user));
	}
	
	/**
	 * 重新缓存user，如果用户在建立小程序用户之前，已经有了公众号用户
	 * @param user
	 * @return
	 */
	@Override
	public void recacheAliMiniUser(User user) {
		cacheService.clearUserCache(cacheService.getCacheKey(user));
	}


    @Override
    @Deprecated
    public User getByUnionid(String unionid) {
    	List<User> list = userRepository.findByUnionid(unionid);
    	User user = list.stream().findFirst().orElse(null);
        return user;
    }
    
    @Override
    public List<User> getUsersByUnionid(String unionid) {
    	List<User> list = userRepository.findByUnionid(unionid);
        return list;
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
	public MiniUserPhone getMiniUserPhone(User user, String code) {
		
		Assert.hasText(code, "授权码不能为空。");
		MiniUserPhone miniUserPhone = null;
		try {
			miniUserPhone = wechatCoreService.getMiniUserPhone(user.getMiniAppId(), code);
			if (!StringUtils.isEmpty(miniUserPhone.getErrorcode())) {
				throw new BizValidateException(miniUserPhone.getErrmsg());
			}
			return miniUserPhone;
			
		} catch (Exception e) {
			throw new BizValidateException(e.getMessage(), e);
		}
		
	}
	
	/**
	 * 获取小程序用户的手机号
	 * @param code
	 * @return
	 */
	@Override
	public AliMiniUserPhone getAlipayMiniUserPhone(User user, String encryptedData) {
		
		Assert.hasText(encryptedData, "encryptedData不能为空。");
		AliMiniUserPhone aliMiniUserPhone = null;
		try {
			String decrptyContent = AlipayEncrypt.decryptContent(encryptedData, "AES", "Csf90IejcIrzDVi3f2nSXw==", "utf8");
			logger.info("decrptyContent: " + decrptyContent);
			aliMiniUserPhone =  JacksonJsonUtil.getMapperInstance(false).readValue(decrptyContent, AliMiniUserPhone.class);
			if (!StringUtils.isEmpty(aliMiniUserPhone.getSubCode())) {
				throw new BizValidateException(aliMiniUserPhone.getSubMsg());
			}
			return aliMiniUserPhone;
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
		if (miniUser == null) {
			miniUser = user;	//这个user是session缓存里的，一般从数据库里查询出来的miniUser不会为空，除非人为在后台数据库删除了用户数据，但没有删除用户缓存
		}
        List<User> userList = getByTel(phone);	//根据手机号关联现有用户
        if (userList == null) {
        	userList = getByTel(purePhone);	//根据手机号关联现有用户
		}
        logger.info("user phone : " + phone);
        User userAccount = null;
        if (userList != null && userList.size() > 0) {	//用手机号将小程序用户和公众号用户关联起来。
        	for (User dbUser : userList) {
				String appid = systemConfigService.getMiniProgramMappedApp(miniUser.getMiniAppId());	//获取小程序对应的公众号appid
				if (StringUtils.isEmpty(appid)) {
					continue;
				}
				if (StringUtils.isEmpty(dbUser.getAppId())) {
					continue;
				}
				if (dbUser.getAppId().equals(appid)) {
					userAccount = dbUser;
					break;
				}
			}
		}

        if (userAccount == null) {	//如果数据库中没有关联的用户，直接更新手机号
        	logger.info("can't find phone user in db, will update. user:  " + miniUser);
        	userAccount = miniUser;
        	userAccount.setTel(phone);
        } else {	//如果数据库中有关联的用户，需要合并老记录
        	logger.info("find phone user in db, will merge mini user to db user !");
        	logger.info("mini user: " + miniUser);
        	logger.info("db user: " + userAccount);
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
	 * 1.小程序用户已存在，但公众号用户不存在的（没有openid或者openid为0的），需要将公众号用户的openid和appid更新到该小程序用户上
	 * 2.新关注用户，在user表中并未形成数据的，这里需要新建一个
	 */
	@Override
	@Transactional
	public boolean bindMiniUser(BaseEventDTO baseEventDTO) {
		
		String openid = baseEventDTO.getOpenid();
		String appid = baseEventDTO.getAppId();
		
		if (!systemConfigService.isMiniprogramAvailabe(appid)) {
			logger.info("appid: " + appid + ", not support app subscribe event, will skip. ");
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
		List<User> users = getUsersByUnionid(unionid);
		User dbUser = users.stream().filter(u -> !StringUtils.isEmpty(u.getMiniopenid()) && StringUtils.isEmpty(u.getAppId())).findFirst().orElse(null);
		if (dbUser == null) {
			//如果数据库中没有有关联的用户，需要新建用户
			logger.info("no related wechat user, will create new user, user openid : " + openid + ", appid : " + appid);
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
			//如果数据库中有关联的用户，更新该小程序用户的openid和appid
			logger.info("find miniapp user, miniopenid : " + dbUser.getMiniopenid());
			dbUser.setOpenid(openid);
			dbUser.setAppId(appid);
			userRepository.save(dbUser);
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
		
		if (!systemConfigService.isMiniprogramAvailabe(appid)) {
			logger.info("appid: " + appid + ", viewMiniProgram event does not support, will skip. ");
			return true;
		}
		
		User user = multiFindByOpenId(openid);
		logger.info("findByOpenid user : " + user);
		//未查询到用户存在两种情况：1.用户未没有产生小程序用户，2用户产生了小程序用户，但未和公众号openid关联上
		UserWeiXin userWeiXin = com.yumu.hexie.integration.wechat.service.UserService.getUserInfo(openid, systemConfigService.queryWXAToken(appid));
		if (userWeiXin == null) {
			logger.warn("can't find user from wechat, openid : " + openid);
			return true;
		}
		String unionid = userWeiXin.getUnionid();	//不是每个公众号都会返回unionid的，只有在共享平台绑定过的才有
		logger.info("updateUserUnionid, unionid : " + unionid);
		if (user != null && StringUtils.isEmpty(user.getUnionid()) && !StringUtils.isEmpty(unionid)) {	
			user.setUnionid(unionid);
	        userRepository.save(user);
		}
		boolean needCreate = false;
		if (user == null) {
			if (!StringUtils.isEmpty(unionid)) {
				List<User> users = getUsersByUnionid(unionid);
				User unionUser = users.stream().filter(u -> !StringUtils.isEmpty(u.getMiniopenid()) && StringUtils.isEmpty(u.getAppId())).findFirst().orElse(null);
				if (unionUser != null) {
					logger.info("updateUserUnionid, unionUser : " + unionUser);
					unionUser.setOpenid(openid);
					unionUser.setAppId(appid);
					userRepository.save(unionUser);
				} else {
					needCreate = true;
				}
			} else {
				needCreate = true;
			}
			if (needCreate) {
				user = new User();
				user.setOpenid(openid);
				user.setAppId(appid);
				user.setAge(20);
				user.setCityId(0l);
				user.setCountyId(0l);
				user.setCurrentAddrId(0l);
				user.setProvinceId(0l);
				user.setXiaoquId(0);
				user.setStatus(0);
				user.setRegisterDate(System.currentTimeMillis());
				user.setNewRegiste(false);
				user.setUnionid(unionid);
				simpleRegister(user);
			}
		}
		return true;
	}
	
	@Override
	public User getByMiniopenid(String miniopenid) {
		User retUser = null;
		List<User> userList = userRepository.findByMiniopenid(miniopenid);
		if (userList != null) {
			if (userList.size() == 1) {
				retUser = userList.get(0);
			} else if (userList.size() == 2) {
				logger.warn("user size larger than 1, miniopenid : " + miniopenid);
				retUser = compareUser(userList.get(0), userList.get(1));
			} else {
				logger.warn("user size larger than 2, miniopenid : " + miniopenid);
				retUser = compareUser(userList.get(0), userList.get(1));
			}
		}
		return retUser;
	}
	
	/**
	 * 用户多条的情况下获取规则
	 * @param user1
	 * @param user2
	 * @return
	 */
	private User compareUser(User user1, User user2) {
		User retUser = null;
		if (!StringUtils.isEmpty(user1.getTel()) && StringUtils.isEmpty(user2.getTel())) {
			retUser = user1;
		} else if (StringUtils.isEmpty(user1.getTel()) && !StringUtils.isEmpty(user2.getTel())) {
			retUser = user2;
		} else if (!StringUtils.isEmpty(user1.getTel()) && !StringUtils.isEmpty(user2.getTel())) {
			if (!StringUtils.isEmpty(user1.getOpenid()) && !"0".equals(user1.getOpenid()) && 
					!StringUtils.isEmpty(user2.getOpenid()) && !"0".equals(user2.getOpenid())) {
				if (!StringUtils.isEmpty(user1.getSectId()) && !"0".equals(user1.getSectId())) {
					retUser = user1;
				} 
				if (!StringUtils.isEmpty(user2.getSectId()) && !"0".equals(user2.getSectId())) {
					retUser = user2;
				}
			} else  if (!StringUtils.isEmpty(user1.getOpenid()) && !"0".equals(user1.getOpenid())) {
				retUser = user1;
			} else if (!StringUtils.isEmpty(user2.getOpenid()) && !"0".equals(user2.getOpenid())) {
				retUser = user2;
			} else {
				retUser = user1;	//都有手机号，都有openid的情况下，并且都绑定过房子的情况下，随机取第一条
			}
		}
		if (retUser == null) {
			if (!StringUtils.isEmpty(user1.getWuyeId())) {
				retUser = user1;
			} else if (!StringUtils.isEmpty(user2.getWuyeId())) {
				retUser = user2;
			}
		}
		return retUser;
	}
	
	/**
	 * 新增阿里用户，并更新缓存
	 */
	@Override
    @Transactional
    public User saveAlipayMiniUserToken(AccessTokenOAuth accessTokenOAuth) {
    	logger.info("accessTokenOAuth : " + accessTokenOAuth);
        
    	String userId = accessTokenOAuth.getOpenid();
    	User userAccount = new User();
    	userAccount.setAliuserid(userId);
        userAccount.setAliappid(accessTokenOAuth.getAppid());
        userAccount.setShareCode(DigestUtils.md5Hex("UID[" + UUID.randomUUID() + "]"));
        userRepository.save(userAccount);
        String key = ModelConstant.KEY_ALI_USER_AUTH_TOKEN + userId;
        String savedSessionKey = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(savedSessionKey) || !savedSessionKey.equals(accessTokenOAuth.getAccessToken())) {
            redisTemplate.opsForValue().set(key, accessTokenOAuth.getAccessToken(), 359, TimeUnit.HOURS);    //官方15天失效，也就是360小时
        }
        return userAccount;
    }
	
//	@Override
//	public User getUserByAliUserId(String aliUserId) {
//		
//		if (StringUtils.isEmpty(aliUserId)) {
//			throw new BizValidateException("user_id不能为空。");
//		}
//		List<User> userList = userRepository.findByAliuserid(aliUserId);
//		return userList.stream().findFirst().orElse(null);
//	}
	
	/**
	 * 根据支付用应用appid和支付宝用户user_id查询用户
	 * @param aliUserId 支付宝用户id
	 * @param appid 支付宝应用id
	 */
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_USER_CACHED, key = "#appid+'_'+#aliUserId", unless = "#result == null")
	public User getUserByAliUserIdAndAliAppid(String aliUserId, String appid) {
		
		if (StringUtils.isEmpty(aliUserId)) {
			throw new BizValidateException("user_id不能为空。");
		}
		if (StringUtils.isEmpty(appid)) {
			throw new BizValidateException("appid不能为空。");
		}
		List<User> userList = userRepository.findByAliuseridAndAliappid(aliUserId, appid);
		return userList.stream().findFirst().orElse(null);
	}
	
	@Override
	@Transactional
	public User saveH5User(User user, H5UserDTO h5UserDTO) throws Exception {
		
		logger.info("saveH5User, h5UserDTO : " + h5UserDTO);
		if (StringUtils.isEmpty(h5UserDTO.getUserId())) {
			throw new BizValidateException("user_id不能为空。");
		}
		
		if (StringUtils.isEmpty(h5UserDTO.getAppid())) {
			throw new BizValidateException("appid不能为空。");
		}
		
		if (!ModelConstant.KEY_USER_SYS_LIFEPAY.equals(h5UserDTO.getFrom()) && StringUtils.isEmpty(h5UserDTO.getCellId())) {
			throw new BizValidateException("cell_id不能为空。");
		}
		
		if (ModelConstant.KEY_USER_SYS_LIFEPAY.equals(h5UserDTO.getFrom()) && StringUtils.isEmpty(h5UserDTO.getHouNo())) {
			throw new BizValidateException("户号不能为空。");
		}
		
		if (user == null) {
			user = new User();
			if (ModelConstant.H5_USER_TYPE_ALIPAY.equals(h5UserDTO.getClientType())) {
				user.setAliuserid(h5UserDTO.getUserId());
				user.setAliappid(h5UserDTO.getAppid());
			} else if (ModelConstant.H5_USER_TYPE_MINNI.equals(h5UserDTO.getClientType())) {
				user.setMiniopenid(h5UserDTO.getUserId());
				user.setMiniAppId(h5UserDTO.getAppid());
//				user.setOpenid(h5UserDTO.getUserId());
//				user.setAppId(h5UserDTO.getAppid());
			}
		}
		
		if (ModelConstant.KEY_USER_SYS_SHWY.equals(h5UserDTO.getFrom())) {
			if (!ModelConstant.KEY_USER_SYS_SHWY.equals(user.getOriSys())) {
				Long auId = null;
				if (!StringUtils.isEmpty(h5UserDTO.getAuId())) {
					auId = Long.valueOf(h5UserDTO.getAuId());
				}
				user.setOriUserId(auId);
				user.setOriSys(ModelConstant.KEY_USER_SYS_SHWY);
			}
			user.setTel(h5UserDTO.getMobile());
		} else if (ModelConstant.KEY_USER_SYS_LIFEPAY.equals(h5UserDTO.getFrom())) {
			user.setOriSys(ModelConstant.KEY_USER_SYS_LIFEPAY);
		}
		BaseResult<HexieUser> baseResult = wuyeUtil2.h5UserLogin(h5UserDTO);
		if (!baseResult.isSuccess()) {
			if ("99".equals(baseResult.getResult())) {
				throw new BizValidateException(baseResult.getMessage());
			} else if ("199".equals(baseResult.getResult())) {
				throw new BizValidateException(ModelConstant.EXCEPTION_BIZ_TYPE_H5LOGIN, 0l, baseResult.getMessage());
			}
		}
		
		HexieUser hexieUser = baseResult.getData();
		HexieAddress hexieAddress = new HexieAddress();
		BeanUtils.copyProperties(hexieUser, hexieAddress);
		
		addressService.updateDefaultAddress(user, hexieAddress);
		Integer totalBind = user.getTotalBind();
		if (totalBind == null) {
			totalBind = 0;
		}
		if (!StringUtils.isEmpty(hexieUser.getTotal_bind())) {
			if (hexieUser.getTotal_bind() > 0) {
				totalBind = hexieUser.getTotal_bind();	//如果值不为空，说明是跑批程序返回回来的，直接取值即可，如果值是空，走下面的else累加即可
			}
		}
		if (totalBind == 0) {
			totalBind = totalBind + 1;
		}
		user.setTotalBind(totalBind);
		user.setWuyeId(hexieUser.getUser_id());
		user.setXiaoquName(hexieUser.getSect_name());
		user.setProvince(hexieUser.getProvince_name());
		user.setCity(hexieUser.getCity_name());
		user.setCounty(hexieUser.getRegion_name());
		user.setSectId(hexieUser.getSect_id());	
		user.setCspId(hexieUser.getCsp_id());
		user.setOfficeTel(hexieUser.getOffice_tel());
		return userRepository.save(user);
	}
	
	@Override
	public List<User> getUserByOriSysAndOriUserId(String oriSys, Long oriUserId) {
		
		return userRepository.findByOriSysAndOriUserId(oriSys, oriUserId);
	}
	
	@Override
	public List<NewLionUser> getNewLionUserByMobile(String mobile) {
		
		if (StringUtils.isEmpty(mobile)) {
			throw new BizValidateException("用户手机号不能为空");
		}
		return newLionUserRepository.findByMobile(mobile);
		
	}
	
	@Override
	public List<User> getChunChuanUserByMobile(String mobile) {
		
		if (StringUtils.isEmpty(mobile)) {
			throw new BizValidateException("用户手机号不能为空");
		}
		return userRepository.findByTel(mobile);
		
	}

}

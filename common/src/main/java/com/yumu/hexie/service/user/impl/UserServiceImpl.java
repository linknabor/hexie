package com.yumu.hexie.service.user.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.integration.wechat.entity.user.UserWeiXin;
import com.yumu.hexie.integration.wuye.WuyeUtil;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.WechatCoreService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.PointService;
import com.yumu.hexie.service.user.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {

	private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Inject
	private UserRepository userRepository;

	@Inject
	private PointService pointService;

	@Inject
	private WechatCoreService wechatCoreService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Override
	public User getById(long uId) {
		return userRepository.findOne(uId);
	}

	public List<User> getByOpenId(String openId) {
		return userRepository.findByOpenid(openId);
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
			userAccount.setShareCode(DigestUtils.md5Hex("UID[" + userAccount.getId() + "]"));

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
		
		pointService.addZhima(userAccount, 5,
				"zm-login-" + DateUtil.dtFormat(new Date(), "yyyy-MM-dd") + userAccount.getId());
		userAccount = userRepository.save(userAccount);
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

		User user = userRepository.findOne(userId);
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
		pointService.addZhima(user, 100, "zm-binding-" + user.getId());
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
        			dbUser.setWuyeId(r.getData().getUser_id());
            		userRepository.save(dbUser);
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

		Object object = redisTemplate.opsForValue().get(key);
		if (object == null) {
			redisTemplate.opsForValue().set(key, sessionId, 2, TimeUnit.SECONDS); // 设置3秒过期，3秒内任何请求不予处理
		} else {

			isDuplicateRequest = true;
		}
		return isDuplicateRequest;
	}


}

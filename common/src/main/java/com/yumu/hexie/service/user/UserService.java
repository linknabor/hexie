package com.yumu.hexie.service.user;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.yumu.hexie.integration.wechat.entity.AccessTokenOAuth;
import com.yumu.hexie.integration.wechat.entity.user.UserWeiXin;
import com.yumu.hexie.model.subscribemsg.UserSubscribeMsg;
import com.yumu.hexie.model.user.User;


/**
 * 用户服务
 */
public interface UserService {

    public User getById(long uId);
    public List<User> getByOpenId(String openId);
    public List<User> getByTel(String tel);
	//获取用户信息
	public UserWeiXin getOrSubscibeUserByCode(String code);
	
	//第三方授权获取用户信息
	public UserWeiXin getTpSubscibeUserByCode(String code, String oriApp);
	
    public UserWeiXin getOrSubscibeUserByOpenId(String appId, String openid);
	
	//从profile页面进行修改用户信息
	public User saveProfile(long userId,String nickName,int sex);
    public User save(User user);
    public User bindPhone(User user,String phone);
	
	public User queryByShareCode(String code);
	
	public List<String> getRepeatShareCodeUser();
	
	public List<User> getShareCodeIsNull();
	
	public List<User> getUserByShareCode(String shareCode);
	User updateUserLoginInfo(UserWeiXin weixinUser, String oriApp);
	User multiFindByOpenId(String openId);
	void bindWuYeId(User user);
	boolean checkDuplicateLogin(HttpSession httpSession);
	
	//注册
	User simpleRegister(User user);
	AccessTokenOAuth getAccessTokenOAuth(String code, String appid);
	AccessTokenOAuth getAlipayAuth(String code);
	//根据openid从数据库中获取缓存
	User getByOpenIdFromCache(User sessonUser);
	//获取用户订阅的模板ID列表
	List<UserSubscribeMsg> getSubscribeTemplate(User user, int type);

}

package com.yumu.hexie.service.user;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.yumu.hexie.integration.wechat.entity.user.UserWeiXin;
import com.yumu.hexie.integration.wechat.vo.SubscribeVO;
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
	
	//用户关注事件
	void subscribeEvent(SubscribeVO subscribeVO);


}

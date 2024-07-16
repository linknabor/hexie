package com.yumu.hexie.service.user;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.yumu.hexie.integration.wechat.entity.AccessTokenOAuth;
import com.yumu.hexie.integration.wechat.entity.MiniUserPhone;
import com.yumu.hexie.integration.wechat.entity.UserMiniprogram;
import com.yumu.hexie.integration.wechat.entity.user.UserWeiXin;
import com.yumu.hexie.model.event.dto.BaseEventDTO;
import com.yumu.hexie.model.user.NewLionUser;
import com.yumu.hexie.model.user.OrgOperator;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.user.dto.H5UserDTO;
import com.yumu.hexie.service.user.req.SwitchSectReq;


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
	boolean checkDuplicateLogin(HttpSession httpSession, String code);
	
	/**
	 * 用户注册
	 * @param user
	 * @return
	 */
	User simpleRegister(User user);
	
	/**
	 * 微信获取用户授权token
	 * @param code
	 * @param appid
	 * @return
	 */
	AccessTokenOAuth getAccessTokenOAuth(String code, String appid);
	
	/**
	 * 支付宝获取用户授权
	 * @param code
	 * @return
	 */
	AccessTokenOAuth getAlipayAuth(String code);
	
	/**
	 * 支付宝获取用户授权 
	 * @param appid
	 * @param code
	 * @return
	 */
	AccessTokenOAuth getAlipayAuth(String appid, String code);
	
	/**
	 * 根据openid从数据库中获取缓存
	 * @param sessonUser
	 * @return
	 */
	User getByOpenIdFromCache(User sessonUser);
	
	/**
	 * 用户关注事件
	 * @param user
	 */
	boolean eventSubscribe(User user);
	
	/**
	 * 用户取关事件
	 * @param user
	 */
	boolean eventUnsubscribe(User user);

	User findwuyeId(String wuyeId);
	
	/**
	 * 用户切换小区
	 * @param user
	 * @param switchSectReq
	 * @return 
	 */
	User switchSect(User user, String openid, SwitchSectReq switchSectReq);
	
	/**
	 * 获取物业id
	 * @param user
	 * @return 
	 */
	String bindWuYeIdSync(User user);

	/**
	 * 获取微信小程序用户登陆key
	 * @param code
	 * @return
	 * @throws Exception
	 */
	UserMiniprogram getWechatMiniUserSessionKey(String code) throws Exception;
	
	/**
	 * 获取微信小程序用户登陆key
	 * @param miniAppid
	 * @param code
	 * @return
	 * @throws Exception
	 */
	UserMiniprogram getWechatMiniUserSessionKey(String miniAppid, String code) throws Exception;
	
	/**
	 * 通过unionid获取用户信息
	 * @param unionid
	 * @return
	 */
	User getByUnionid(String unionid);
	
	/**
	 * 保存小程序用户sessionKey
	 * @param miniUser
	 * @return
	 */
	User saveMiniUserSessionKey(UserMiniprogram miniUser);
	
	/**
	 * 缓存小程序用户
	 * @param user
	 */
	void recacheMiniUser(User user);
	
	/**
	 * 验证小程序菜单访问权限
	 * @param user
	 * @param page
	 * @return
	 */
	boolean validateMiniPageAccess(User user, String page);
	
	/**
	 * 获取用户机构信息
	 * @param userId
	 * @return
	 */
	OrgOperator getOrgOperator(User user);
	
	/**
	 * 获取小程序用户手机号
	 * @param code
	 * @return
	 */
	MiniUserPhone getMiniUserPhone(User user, String code);
	
	/**
	 * 保存小程序用户手机
	 * @param user
	 * @param miniUserPhone
	 * @return
	 */
	User saveMiniUserPhone(User user, MiniUserPhone miniUserPhone);
	
	/**
	 * 更新用户信息
	 * @param user
	 * @param map
	 * @return
	 */
	User updateUserInfo(User user, Map<String, String> map);
	
	/**
	 * 公众号关注事件绑定小程序用户
	 * @param baseEventDTO
	 * @return
	 */
	boolean bindMiniUser(BaseEventDTO baseEventDTO);
	
	/**
	 * 更新用户unionid
	 * @param baseEventDTO
	 * @return
	 */
	boolean updateUserUnionid(BaseEventDTO baseEventDTO);
	
	/**
	 * 根据小程序用户openid获取用户
	 * @param miniopenid
	 * @return
	 */
	User getByMiniopenid(String miniopenid);
	
	/**
	 * 保存支付宝用户和authToken
	 * @param accessTokenOAuth
	 * @return
	 */
	User saveAlipayMiniUserToken(AccessTokenOAuth accessTokenOAuth);
	
	/**
	 * 获取支付宝用户手机
	 * @param user
	 * @param code
	 * @return
	 */
	MiniUserPhone getAlipayMiniUserPhone(User user, String encryptedData);
	
	/**
	 * 根据支付宝用户id获取用户
	 * @param aliUserId
	 * @return
	 */
	User getUserByAliUserId(String aliUserId);
	
	/**
	 * 保存支付h5用户信息
	 * @param user
	 * @param aliUserDTO
	 * @return
	 * @throws Exception 
	 */
	User saveH5User(User user, H5UserDTO aliUserDTO) throws Exception;
	
	/**
	 * 根据源系统查询用户信息
	 * @param oriSys
	 * @param oriUserId
	 * @return
	 */
	List<User> getUserByOriSysAndOriUserId(String oriSys, Long oriUserId);
	
	/**
	 * 根据手机号查询新朗恩用户
	 * @param mobile
	 * @return
	 */
	List<NewLionUser> getNewLionUserByMobile(String mobile);

}

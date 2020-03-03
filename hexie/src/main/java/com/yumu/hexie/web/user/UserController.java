package com.yumu.hexie.web.user;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.common.util.RequestUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.integration.wechat.entity.user.UserWeiXin;
import com.yumu.hexie.model.card.WechatCard;
import com.yumu.hexie.model.localservice.HomeServiceConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.view.BgImage;
import com.yumu.hexie.model.view.BottomIcon;
import com.yumu.hexie.model.view.QrCode;
import com.yumu.hexie.model.view.WuyePayTabs;
import com.yumu.hexie.service.card.WechatCardService;
import com.yumu.hexie.service.common.SmsService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.o2o.OperatorService;
import com.yumu.hexie.service.page.PageConfigService;
import com.yumu.hexie.service.shequ.ParamService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import com.yumu.hexie.web.user.req.MobileYzm;
import com.yumu.hexie.web.user.req.SimpleRegisterReq;
import com.yumu.hexie.web.user.resp.UserInfo;


@Controller(value = "userController")
public class UserController extends BaseController{
	
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Inject
	private UserService userService;
	@Inject
	private SmsService smsService;
    @Inject
    private OperatorService operatorService;
    @Autowired
    private ParamService paramService;
    @Autowired
    private PageConfigService pageConfigService;
    @Autowired
    private WechatCardService wechatCardService;
    @Autowired
    private SystemConfigService systemConfigService;

    @Value(value = "${testMode}")
    private Boolean testMode;
	
	@RequestMapping(value = "/userInfo", method = RequestMethod.GET)
	@ResponseBody
    public BaseResult<UserInfo> userInfo(HttpSession session,@ModelAttribute(Constants.USER)User user,
    		HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		long beginTime = System.currentTimeMillis();
		User sessionUser = user;
		try {
			
			String oriApp = request.getParameter("oriApp");
			log.info("oriApp : " + oriApp);
			if (StringUtil.isEmpty(oriApp)) {
				oriApp = ConstantWeChat.APPID;
			}
			
			log.info("user in session :" + sessionUser);
			List<User> userList = userService.getByOpenId(user.getOpenid());
			if (userList!=null) {
				for (User baseduser : userList) {
					
					if (baseduser.getId() == user.getId()) {
						user = baseduser;
						break;
					}else if (StringUtils.isEmpty(baseduser.getId())&&baseduser.getOriUserId() == user.getId() ) {	//从其他公众号迁移过来的用户，登陆时session中应该是源系统的userId，所以跟原系统的比较。
						user = baseduser;
						break;
					}
				}
			}
			user = userService.getById(user.getId());
			if (user != null) {
				String userAppId = user.getAppId();	//如果根据session中信息获得的用户并非当前公众号的，比如宝房用户登陆合协公众号，则需要清空session，让他重新登陆
				if (!oriApp.equals(userAppId)) {
					user = null;
				}
			}
			log.info("user in db :" + user);
			if(user != null){
			    session.setAttribute(Constants.USER, user);
			    UserInfo userInfo = new UserInfo(user,operatorService.isOperator(HomeServiceConstant.SERVICE_TYPE_REPAIR,user.getId()));
			    Map<String, String> paramMap = paramService.getWuyeParamByUser(user);
			    userInfo.setCfgParam(paramMap);
			    
			    List<BottomIcon> iconList = pageConfigService.getBottomIcon(user.getAppId());
			    List<BgImage> bgImageList = pageConfigService.getBgImage(user.getAppId());
			    List<WuyePayTabs> tabsList = pageConfigService.getWuyePayTabs(user.getAppId());
			    userInfo.setIconList(iconList);
			    userInfo.setBgImageList(bgImageList);
			    userInfo.setWuyeTabsList(tabsList);
			    WechatCard wechatCard = wechatCardService.getWechatMemberCard(user.getOpenid());
			    if (wechatCard == null || StringUtils.isEmpty(wechatCard.getCardCode())) {
					//do nothing
				}else {
					userInfo.setPoint(wechatCard.getBonus());
				}
			    QrCode qrCode = pageConfigService.getQrCode(user.getAppId());
			    String qrLink = "";
			    if (qrCode != null) {
			    	qrLink = qrCode.getQrLink();
				}
			    userInfo.setQrCode(qrLink);
			    userInfo.setCardStatus(wechatCardService.getWechatMemberCard(user.getOpenid()).getStatus());
			    userInfo.setCardService(systemConfigService.isCardServiceAvailable(user.getAppId()));
			    userInfo.setCoronaPrevention(systemConfigService.coronaPreventionAvailable(user.getAppId()));
			    userInfo.setDonghu(systemConfigService.isDonghu(user.getAppId()));
			    long endTime = System.currentTimeMillis();
				log.info("user:" + user.getName() + "登陆，耗时：" + ((endTime-beginTime)/1000));

			    return new BaseResult<UserInfo>().success(userInfo);
			} else {
				log.error("current user id in session is not the same with the id in database. user : " + sessionUser + ", sessionId: " + session.getId());
				session.setMaxInactiveInterval(1);//将会话过期
				Thread.sleep(50);	//延时，因为上面设置了1秒。页面上也设置了延时，所以这里不需要1秒
				return new BaseResult<UserInfo>().success(null);
			}
		} catch (Exception e) {
			
			if (e instanceof BizValidateException) {
				throw (BizValidateException)e;
			}else {
				throw new Exception(e);
			}
		}
    }
	
	//检查当前用户是否注册，注册标准参考电话是否为有值。
	@RequestMapping(value = "/checkTell", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<?> checkTell(@ModelAttribute(Constants.USER)User user){
		if(user.getTel() != null && !user.getTel().equals("")) {
			 return BaseResult.successResult("用户已注册");
		}
		return BaseResult.fail("用户未注册");
	}

	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	@ResponseBody
    public BaseResult<UserInfo> profile(HttpSession session,@ModelAttribute(Constants.USER)User user,@RequestParam String nickName,@RequestParam int sex) throws Exception {
		user = userService.saveProfile(user.getId(), nickName, sex);
		if(user != null){
			session.setAttribute(Constants.USER, user);
	        return new BaseResult<UserInfo>().success(new UserInfo(user,
	            operatorService.isOperator(HomeServiceConstant.SERVICE_TYPE_REPAIR,user.getId())));
		} else {
            return new BaseResult<UserInfo>().failMsg("用户不存在！");
        }
    }
	
	@RequestMapping(value = "/login/{code}", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<UserInfo> login(HttpSession session,@PathVariable String code, @RequestBody(required = false) Map<String, String> postData) throws Exception {
		
		long beginTime = System.currentTimeMillis();
		User userAccount = null;
		String oriApp = postData.get("oriApp");
    	log.info("oriApp : " + oriApp);	//来源系统，如果为空，则说明来自于合协社区
    	
		if (StringUtil.isNotEmpty(code)) {
		    if(Boolean.TRUE.equals(testMode)) {
		        try{
			        Long id = Long.valueOf(code);
			    	userAccount = userService.getById(id);
		        }catch(Throwable t){}
		    }
		    if(userAccount == null) {
		    	
		    	if (userService.checkDuplicateLogin(session)) {
					throw new BizValidateException("正在登陆中，请耐心等待。");
				}
		    	
		    	UserWeiXin weixinUser = null;
		    	if (StringUtils.isEmpty(oriApp)) {
		    		weixinUser = userService.getOrSubscibeUserByCode(code);
				}else {
					weixinUser = userService.getTpSubscibeUserByCode(code, oriApp);
				}
		    	
		    	userAccount = userService.updateUserLoginInfo(weixinUser, oriApp);
		    }
		    
			session.setAttribute(Constants.USER, userAccount);
		}
		if(userAccount == null) {
		    return new BaseResult<UserInfo>().failMsg("用户不存在！");
		}
		long endTime = System.currentTimeMillis();
		log.info("user:" + userAccount.getName() + "login，耗时：" + ((endTime-beginTime)/1000));
		return new BaseResult<UserInfo>().success(new UserInfo(userAccount,
		    operatorService.isOperator(HomeServiceConstant.SERVICE_TYPE_REPAIR,userAccount.getId())));

    }
	
	/**
	 * 注册获取验证码
	 * @param request
	 * @param yzm
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getyzm", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<String> getYzm(HttpServletRequest request, @RequestBody MobileYzm yzm, @ModelAttribute(Constants.USER)User user) throws Exception {
		String requestIp = RequestUtil.getRealIp(request);
		log.info("getyzm request ip : " + requestIp);
		log.info("getyzm request mobile: " + requestIp);
		log.info("getyzm request header [Access-Control-Allow-Token]: " + request.getHeader("Access-Control-Allow-Token"));
		String token = request.getHeader("Access-Control-Allow-Token");
		if (StringUtils.isEmpty(token)) {
			return new BaseResult<String>().failMsg("invalid request!");
		}
		boolean result = smsService.sendVerificationCode(user, yzm.getMobile(), requestIp);
		if(!result) {
		    return new BaseResult<String>().failMsg("发送验证码失败");
		}
	    return  new BaseResult<String>().success("验证码发送成功");
    }
	
	/**
	 * 发票获取短信验证码
	 * @param request
	 * @param yzm
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getyzm1", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<String> getYzm1(HttpServletRequest request, @RequestBody MobileYzm yzm, 
    		@RequestParam(required = false) String trade_water_id) throws Exception {
		String requestIp = RequestUtil.getRealIp(request);
		log.info("getyzm1 trade_water_id : " + trade_water_id);
		log.info("getyzm1 request mobile: " + requestIp);
		log.info("getyzm1 request header [Access-Control-Allow-Token]: " + request.getHeader("Access-Control-Allow-Token"));
		String token = request.getHeader("Access-Control-Allow-Token");
		boolean result = smsService.verifySmsToken(trade_water_id, token);
		if (!result) {
			return new BaseResult<String>().failMsg("invalid request!");
		}
		result = smsService.sendVerificationCode(new User(), yzm.getMobile(), requestIp);
		if(!result) {
		    return new BaseResult<String>().failMsg("发送验证码失败");
		}
	    return  new BaseResult<String>().success("验证码发送成功");
    }
	
	@RequestMapping(value = "/savePersonInfo/{captcha}", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<UserInfo> savePersonInfo(HttpSession session,@RequestBody User editUser,@ModelAttribute(Constants.USER)User user,
			@PathVariable String captcha) throws Exception {
		if(StringUtil.equals(editUser.getTel(),user.getTel())) {
			user.setSex(editUser.getSex());
			user.setRealName(editUser.getRealName());
			user.setName(editUser.getName());
			session.setAttribute(Constants.USER, userService.save(user));

	        return new BaseResult<UserInfo>().success(new UserInfo(user));
		} else {
			if(!smsService.checkVerificationCode(editUser.getTel(),captcha)){
				return new BaseResult<UserInfo>().failMsg("短信校验失败！");
			} else {
				user.setTel(editUser.getTel());
				user.setSex(editUser.getSex());
				user.setRealName(editUser.getRealName());
				user.setName(editUser.getName());
				session.setAttribute(Constants.USER, userService.save(user));
	            return new BaseResult<UserInfo>().success(new UserInfo(user));
			}
		}
	}

    @RequestMapping(value = "/simpleRegister", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult<UserInfo> simpleRegister(HttpSession session, @ModelAttribute(Constants.USER)User user, 
    		@RequestBody SimpleRegisterReq req) throws Exception {
    	
        if(StringUtil.isEmpty(req.getMobile()) || StringUtil.isEmpty(req.getYzm())){
            return new BaseResult<UserInfo>().failMsg("信息请填写完整！");
        }
        boolean result = smsService.checkVerificationCode(req.getMobile(), req.getYzm());
        if(!result){
            return new BaseResult<UserInfo>().failMsg("验证码不正确。");
        } else {
            if(StringUtil.isNotEmpty(req.getName())) {
                user.setName(req.getName());
            }
            if (!StringUtils.isEmpty(req.getMobile())) {
            	 user.setTel(req.getMobile());
			}
            User savedUser = userService.simpleRegister(user);
            session.setAttribute(Constants.USER, savedUser);
            return new BaseResult<UserInfo>().success(new UserInfo(savedUser));

        }
    }
    
    @RequestMapping(value = "/cancelSubscribe", method = RequestMethod.GET)
	@ResponseBody
    public BaseResult<String> cancelSubscribe(String weixin_id){
    	List<User> userList = userService.getByOpenId(weixin_id);
    	if (userList!=null && userList.size()>0) {
    		for (User user : userList) {
    			user.setSubscribe(0);
        		userService.save(user);
			}
    		return  new BaseResult<String>().success("关注被取消"); 
		}else {
			return  new BaseResult<String>().failMsg("未找到该用户");
		}
    	 
    }
    
    @RequestMapping(value = "/saveLocation", method = RequestMethod.GET)
   	@ResponseBody
   	public BaseResult<String> saveLocation(String weixin_id,String Latitude,String Longitude){
    	List<User> userList = userService.getByOpenId(weixin_id);
    	if (userList!=null) {
			for (User user : userList) {
				user.setLatitude(Double.parseDouble(Latitude));//纬度
		   		user.setLongitude(Double.parseDouble(Longitude));//经度
				userService.save(user);
			}
			return new BaseResult<String>().success("地理位置保存成功!");
		}else {
			return new BaseResult<String>().failMsg("未找到该用户!");
		}
   		
    	 
    }
}

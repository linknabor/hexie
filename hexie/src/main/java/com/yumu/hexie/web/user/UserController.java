package com.yumu.hexie.web.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import com.yumu.hexie.integration.wechat.entity.AccessTokenOAuth;
import com.yumu.hexie.integration.wechat.entity.user.UserWeiXin;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.card.WechatCard;
import com.yumu.hexie.model.msgtemplate.MsgTemplate;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.view.BgImage;
import com.yumu.hexie.model.view.BottomIcon;
import com.yumu.hexie.model.view.CsHotline;
import com.yumu.hexie.model.view.Menu;
import com.yumu.hexie.model.view.QrCode;
import com.yumu.hexie.model.view.WuyePayTabs;
import com.yumu.hexie.service.card.WechatCardService;
import com.yumu.hexie.service.common.SmsService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.msgtemplate.WechatMsgService;
import com.yumu.hexie.service.o2o.OperatorDefinition;
import com.yumu.hexie.service.o2o.OperatorService;
import com.yumu.hexie.service.page.PageConfigService;
import com.yumu.hexie.service.shequ.ParamService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.service.user.req.SwitchSectReq;
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
    @Autowired
    private WechatMsgService wechatMsgService;
    @Autowired
	private StringRedisTemplate stringRedisTemplate;

    @Value(value = "${testMode}")
    private Boolean testMode;
	
	@RequestMapping(value = "/userInfo", method = RequestMethod.GET)
	@ResponseBody
    public BaseResult<UserInfo> userInfo(HttpServletRequest request, @ModelAttribute(Constants.USER)User user) throws Exception {
		
		long beginTime = System.currentTimeMillis();
		User dbUser = null;
		try {

			String oriApp = request.getParameter("oriApp");
			if (StringUtil.isEmpty(oriApp)) {
				oriApp = ConstantWeChat.APPID;
			}
			log.info("oriApp : " + oriApp);
			log.info("user in session :" + user);
			
			dbUser = userService.getByOpenIdFromCache(user);
			if (dbUser != null) {
				String userAppId = dbUser.getAppId();	//如果根据session中信息获得的用户并非当前公众号的，比如宝房用户登陆合协公众号，则需要清空session，让他重新登陆
				if (!oriApp.equals(userAppId)) {
					dbUser = null;
				}
			}
			
			log.info("user in db :" + dbUser);
			if(dbUser != null){
				
				long endTime = System.currentTimeMillis();
				BeanUtils.copyProperties(dbUser, user);
			    request.getSession().setAttribute(Constants.USER, user);
		    
			    OperatorDefinition odDefinition  = operatorService.defineOperator(user);
			    
				/* 2021-02-23 工作人远弹出消息订阅的窗口 start */
				List<MsgTemplate> msgTemplateListAll = wechatMsgService.getSubscribeMsgTemplate(user.getAppId(), ModelConstant.MSG_TYPE_SUBSCRIBE_MSG, ModelConstant.SUBSCRIBE_MSG_TEMPLATE_BIZ_TYPE_OPERATOR);
				List<String> templateIds = new ArrayList<>();
				for (MsgTemplate msgTemplate : msgTemplateListAll) {
					templateIds.add(msgTemplate.getValue());
				}
			    /* 2021-02-23 工作人远弹出消息订阅的窗口 end */
			    
			    UserInfo userInfo = new UserInfo(user, odDefinition, templateIds);

			    endTime = System.currentTimeMillis();
			    log.info("userInfo1，耗时：" + ((endTime-beginTime)));

			    Map<String, String> paramMap = paramService.getWuyeParam(user);
			    userInfo.setCfgParam(paramMap);
			    
			    boolean repairService = paramService.repairServiceAvailable(user);
			    userInfo.setRepairService(repairService);	//新版工单服务是否开通
			    
			    endTime = System.currentTimeMillis();
			    log.info("userInfo2，耗时：" + ((endTime-beginTime)));
			    
			    List<BottomIcon> iconList = pageConfigService.getBottomIcon(user.getAppId());
			    List<BgImage> bgImageList = pageConfigService.getBgImage(user.getAppId());
			    List<WuyePayTabs> tabsList = pageConfigService.getWuyePayTabs(user.getAppId());
			    userInfo.setIconList(iconList);
			    userInfo.setBgImageList(bgImageList);
			    userInfo.setWuyeTabsList(tabsList);
			    
			    List<Menu> menuList = new ArrayList<>();
			    /*
			     * 取菜单顺序：
			     * 1.绑定访问的用户，先取小区级别，没有取公司级别，公司级别没有的，取app级别的，再没有的，取默认菜单
			     * 2.未绑定房屋的用户，取默认菜单
			     */
			    if (!StringUtils.isEmpty(user.getSectId()) && !"0".equals(user.getSectId())) {
			    	menuList = pageConfigService.getMenuBySectId(user.getSectId());
			    }
			    if (menuList.isEmpty()) {
			    	if (!StringUtils.isEmpty(user.getCspId()) && !"0".equals(user.getCspId())) {
				    	menuList = pageConfigService.getMenuByCspId(user.getCspId());
					}
				}
			    if (menuList.isEmpty()) {
					menuList = pageConfigService.getMenuByAppid(user.getAppId());
				}
			    if (menuList.isEmpty()) {
		    		menuList = pageConfigService.getMenuByDefaultTypeLessThan(1);	//表示绑定了房屋的默认菜单
				}
			    if (menuList.isEmpty()) {
			    	menuList = pageConfigService.getMenuByDefaultTypeLessThan(2);	//未绑定房屋的默认菜单
				}
			    userInfo.setMenuList(menuList);
			    endTime = System.currentTimeMillis();
			    /*菜单结束*/
			    
			    log.info("user3，耗时：" + ((endTime-beginTime)));
			    
			    WechatCard wechatCard = wechatCardService.getWechatMemberCard(user.getOpenid());	//TODO 缓存，主要是积分的变动频率。更新积分时需要刷新缓存
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
			    
			    CsHotline csHotline = pageConfigService.getCsHotline(user.getAppId());
			    String hotline = "";
			    if (csHotline != null) {
			    	hotline = csHotline.getHotline();
				}
			    
			    userInfo.setQrCode(qrLink);
			    userInfo.setCsHotline(hotline);
			    userInfo.setCardStatus(wechatCard.getStatus());
			    userInfo.setCardService(systemConfigService.isCardServiceAvailable(user.getAppId()));
			    userInfo.setCoronaPrevention(systemConfigService.coronaPreventionAvailable(user.getAppId()));
			    userInfo.setDonghu(systemConfigService.isDonghu(user.getAppId()));
			    userInfo.setCardPayService(systemConfigService.isCardPayServiceAvailabe(user.getAppId()));
			    		    
			    endTime = System.currentTimeMillis();

				log.info("user:" + user.getName() + "登陆，耗时：" + ((endTime-beginTime)));

			    return new BaseResult<UserInfo>().success(userInfo);
			} else {
				log.error("current user id in session is not the same with the id in database. user : " + user + ", sessionId: " + request.getSession().getId());
				request.getSession().setMaxInactiveInterval(1);//将会话过期
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
			
		    OperatorDefinition odDefinition = operatorService.defineOperator(user);
		    /* 2021-02-23 工作人远弹出消息订阅的窗口 start */
		    List<MsgTemplate> msgTemplateListAll = wechatMsgService.getSubscribeMsgTemplate(user.getAppId(), ModelConstant.MSG_TYPE_SUBSCRIBE_MSG, ModelConstant.SUBSCRIBE_MSG_TEMPLATE_BIZ_TYPE_OPERATOR);
			List<String> templateIds = new ArrayList<>();
			for (MsgTemplate msgTemplate : msgTemplateListAll) {
				templateIds.add(msgTemplate.getValue());
			}
		    /* 2021-02-23 工作人远弹出消息订阅的窗口 end */
		    UserInfo userInfo = new UserInfo(user, odDefinition, templateIds);
		    
	        return new BaseResult<UserInfo>().success(userInfo);
		} else {
            return new BaseResult<UserInfo>().failMsg("用户不存在！");
        }
    }
	
	@RequestMapping(value = "/login/{code}", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<UserInfo> login(HttpSession session, @PathVariable String code, @RequestBody(required = false) Map<String, String> postData) throws Exception {
		
		long beginTime = System.currentTimeMillis();
		User userAccount = null;
		String oriApp = postData.get("oriApp");
		if (StringUtils.isEmpty(oriApp)) {
			oriApp = ConstantWeChat.APPID;
		}
    	log.info("oriApp : " + oriApp);	//来源系统，如果为空，则说明来自于合协社区
    	
		if (StringUtil.isNotEmpty(code)) {
		    if(Boolean.TRUE.equals(testMode)) {
		        try{
			        Long id = Long.valueOf(code);
			    	userAccount = userService.getById(id);
		        }catch(Throwable t){}
		    }
		    if(userAccount == null) {
		    	
		    	if (userService.checkDuplicateLogin(session, code)) {
					throw new BizValidateException(599, 0, "正在登陆中，请耐心等待。如较长时间无响应，请刷新重试。");
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
		
		OperatorDefinition odDefinition = operatorService.defineOperator(userAccount);
		/* 2021-02-23 工作人远弹出消息订阅的窗口 start */
		List<MsgTemplate> msgTemplateListAll = wechatMsgService.getSubscribeMsgTemplate(userAccount.getAppId(), ModelConstant.MSG_TYPE_SUBSCRIBE_MSG, ModelConstant.SUBSCRIBE_MSG_TEMPLATE_BIZ_TYPE_OPERATOR);
		List<String> templateIds = new ArrayList<>();
		for (MsgTemplate msgTemplate : msgTemplateListAll) {
			templateIds.add(msgTemplate.getValue());
		}
	    /* 2021-02-23 工作人远弹出消息订阅的窗口 end */
	    UserInfo userInfo = new UserInfo(userAccount, odDefinition, templateIds);
		return new BaseResult<UserInfo>().success(userInfo);

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
		
		String str = stringRedisTemplate.opsForValue().get("sms:blacklist");
		if (!StringUtils.isEmpty(str)) {
			if (str.indexOf(requestIp)>-1) {
				return new BaseResult<String>().failMsg("发送验证码失败");
			}
		}
		log.info("getyzm request ip : " + requestIp);
		log.info("getyzm request mobile: " + requestIp);
		log.info("getyzm request header [Access-Control-Allow-Token]: " + request.getHeader("Access-Control-Allow-Token"));
		String token = request.getHeader("Access-Control-Allow-Token");
		if (StringUtils.isEmpty(token)) {
			return new BaseResult<String>().failMsg("invalid request!");
		}
		if (yzm.getType() == 0) {
			yzm.setType(ModelConstant.SMS_TYPE_REG);
		}
		boolean result = smsService.sendVerificationCode(user, yzm.getMobile(), requestIp, yzm.getType());
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
		result = smsService.sendVerificationCode(new User(), yzm.getMobile(), requestIp, ModelConstant.SMS_TYPE_INVOICE);
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
			userService.save(user);
			session.setAttribute(Constants.USER, user);

	        return new BaseResult<UserInfo>().success(new UserInfo(user));
		} else {
			if(!smsService.checkVerificationCode(editUser.getTel(),captcha)){
				return new BaseResult<UserInfo>().failMsg("短信校验失败！");
			} else {
				user.setTel(editUser.getTel());
				user.setSex(editUser.getSex());
				user.setRealName(editUser.getRealName());
				user.setName(editUser.getName());
				userService.save(user);
				session.setAttribute(Constants.USER, user);
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
            userService.simpleRegister(user);
            session.setAttribute(Constants.USER, user);
            return new BaseResult<UserInfo>().success(new UserInfo(user));

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
    
    
    /**
     * 静默授权获取用户openid
     * @param code
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/authorize/{code}/{appid}", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<Map<String, String>> authorize(@PathVariable String code, @PathVariable String appid) throws Exception {
		
		Map<String, String> map = new HashMap<>();
		if (StringUtil.isNotEmpty(code)) {
			AccessTokenOAuth oauth = userService.getAccessTokenOAuth(code, appid);
	    	map.put("openid", oauth.getOpenid());
		}
		return new BaseResult<Map<String, String>>().success(map);
    }
    
    /**
     * 静默授权获取用户openid-alipay
     * @param code
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/authorizeAlipay/{code}", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<Map<String, String>> authorizeAlipay(@PathVariable String code) throws Exception {
		
		Map<String, String> map = new HashMap<>();
		if (StringUtil.isNotEmpty(code)) {
			AccessTokenOAuth oauth = userService.getAlipayAuth(code);
	    	map.put("userid", oauth.getOpenid());
		}
		return new BaseResult<Map<String, String>>().success(map);
    }
    
    /**
	 * 切换小区
	 * @param queryFeeSmsBillReq
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/switchSect", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<UserInfo> switchSect(HttpServletRequest request, @ModelAttribute(Constants.USER) User user, SwitchSectReq switchSectReq) throws Exception {
		
		long beginTime = System.currentTimeMillis();
		User dbUser = userService.switchSect(user, switchSectReq);
		BeanUtils.copyProperties(dbUser, user);
	    request.getSession().setAttribute(Constants.USER, user);
	    
	    OperatorDefinition odDefinition  = operatorService.defineOperator(user);
	    
		/* 2021-02-23 工作人远弹出消息订阅的窗口 start */
		List<MsgTemplate> msgTemplateListAll = wechatMsgService.getSubscribeMsgTemplate(user.getAppId(), ModelConstant.MSG_TYPE_SUBSCRIBE_MSG, ModelConstant.SUBSCRIBE_MSG_TEMPLATE_BIZ_TYPE_OPERATOR);
		List<String> templateIds = new ArrayList<>();
		for (MsgTemplate msgTemplate : msgTemplateListAll) {
			templateIds.add(msgTemplate.getValue());
		}
	    /* 2021-02-23 工作人远弹出消息订阅的窗口 end */
	    
	    UserInfo userInfo = new UserInfo(user, odDefinition, templateIds);

	    Map<String, String> paramMap = paramService.getWuyeParam(user);
	    userInfo.setCfgParam(paramMap);
	    
	    boolean repairService = paramService.repairServiceAvailable(user);
	    userInfo.setRepairService(repairService);	//新版工单服务是否开通
	    
	    List<BottomIcon> iconList = pageConfigService.getBottomIcon(user.getAppId());
	    List<BgImage> bgImageList = pageConfigService.getBgImage(user.getAppId());
	    List<WuyePayTabs> tabsList = pageConfigService.getWuyePayTabs(user.getAppId());
	    userInfo.setIconList(iconList);
	    userInfo.setBgImageList(bgImageList);
	    userInfo.setWuyeTabsList(tabsList);
	    
	    List<Menu> menuList = new ArrayList<>();
	    /*
	     * 取菜单顺序：
	     * 1.绑定访问的用户，先取小区级别，没有取公司级别，公司级别没有的，取app级别的，再没有的，取默认菜单
	     * 2.未绑定房屋的用户，取默认菜单
	     */
	    if (!StringUtils.isEmpty(user.getSectId()) && !"0".equals(user.getSectId())) {
	    	menuList = pageConfigService.getMenuBySectId(user.getSectId());
	    }
	    if (menuList.isEmpty()) {
	    	if (!StringUtils.isEmpty(user.getCspId()) && !"0".equals(user.getCspId())) {
		    	menuList = pageConfigService.getMenuByCspId(user.getCspId());
			}
		}
	    if (menuList.isEmpty()) {
			menuList = pageConfigService.getMenuByAppid(user.getAppId());
		}
	    if (menuList.isEmpty()) {
    		menuList = pageConfigService.getMenuByDefaultTypeLessThan(1);	//表示绑定了房屋的默认菜单
		}
	    if (menuList.isEmpty()) {
	    	menuList = pageConfigService.getMenuByDefaultTypeLessThan(2);	//未绑定房屋的默认菜单
		}
	    userInfo.setMenuList(menuList);
	    
	    WechatCard wechatCard = wechatCardService.getWechatMemberCard(user.getOpenid());	//TODO 缓存，主要是积分的变动频率。更新积分时需要刷新缓存
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
	    
	    CsHotline csHotline = pageConfigService.getCsHotline(user.getAppId());
	    String hotline = "";
	    if (csHotline != null) {
	    	hotline = csHotline.getHotline();
		}
	    
	    userInfo.setQrCode(qrLink);
	    userInfo.setCsHotline(hotline);
	    userInfo.setCardStatus(wechatCard.getStatus());
	    userInfo.setCardService(systemConfigService.isCardServiceAvailable(user.getAppId()));
	    userInfo.setCoronaPrevention(systemConfigService.coronaPreventionAvailable(user.getAppId()));
	    userInfo.setDonghu(systemConfigService.isDonghu(user.getAppId()));
	    userInfo.setCardPayService(systemConfigService.isCardPayServiceAvailabe(user.getAppId()));
	    		    
	    long endTime = System.currentTimeMillis();
		log.info("switch sect :" + user.getName() + ", 耗时：" + ((endTime-beginTime)));
		
	    return new BaseResult<UserInfo>().success(userInfo);
	}
    
}

package com.yumu.hexie.web.user;

import java.net.URLEncoder;
import java.util.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.yumu.hexie.integration.alipay.entity.AliMiniUserPhone;
import com.yumu.hexie.integration.wechat.constant.ConstantWd;
import com.yumu.hexie.service.user.dto.H5AuthorizeVo;
import com.yumu.hexie.service.wdwechat.WdService;
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
import com.yumu.hexie.integration.wechat.entity.MiniUserPhone;
import com.yumu.hexie.integration.wechat.entity.MiniUserPhone.PhoneInfo;
import com.yumu.hexie.integration.wechat.entity.UserMiniprogram;
import com.yumu.hexie.integration.wechat.entity.user.UserWeiXin;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.card.WechatCard;
import com.yumu.hexie.model.msgtemplate.MsgTemplate;
import com.yumu.hexie.model.user.OrgOperator;
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
import com.yumu.hexie.service.subscribemsg.AliSubscribeMsgService;
import com.yumu.hexie.service.subscribemsg.dto.SubscribeReq;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.service.user.dto.H5UserDTO;
import com.yumu.hexie.service.user.req.SwitchSectReq;
import com.yumu.hexie.vo.menu.GroupMenuInfo;
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
	@Autowired
	private WdService wdService;
	@Autowired
	private AliSubscribeMsgService aliSubscribeMsgService;

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

				//TODO 这个需要判断如果是农工商进来的用户，需要判断是否已经注册，如果没注册，根据传过来的token进行自动注册
				String token = request.getParameter("token");
				if(StringUtils.hasText(token) && ConstantWd.APPID.equals(user.getAppId())) {
					String phone = stringRedisTemplate.opsForValue().get("register:" + oriApp + ":" + token);
					if(StringUtils.hasText(phone)) {
						user.setTel(phone);
						user = userService.simpleRegister(user);
					}
				}
				//判断农工商用户是否做过同步
				if(ConstantWd.APPID.equals(user.getAppId()) && !StringUtils.hasText(user.getUniqueCode())) {
					wdService.syncUserInfo(user);
				}

			    request.getSession().setAttribute(Constants.USER, user);
		    
			    OperatorDefinition odDefinition = operatorService.defineOperator(user);

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
			    //TODO 如果是农工商用户，除了我们自己的物业板块，其他的跳转地址，要做特殊处理
				if(ConstantWd.APPID.equals(user.getAppId())) {
					if(StringUtils.hasText(user.getTel())) {
						for(BottomIcon icon : iconList) {
							if(!icon.getIconLink().contains("e-shequ")) {
								String url = icon.getIconLink();
								url = URLEncoder.encode(url, "UTF-8");
								String wdToken = Base64.getEncoder().encodeToString(user.getWuyeId().getBytes());
								icon.setIconLink(String.format(ConstantWd.CENTER_URL, icon.getAliasName(), url, wdToken));
							}
						}
					}
				}
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
			    if (!StringUtils.isEmpty(user.getSectId()) && !"0".equals(user.getSectId())) {	//绑定房屋的
			    	menuList = pageConfigService.getMenuBySectId(user.getSectId());
			    	if (menuList.isEmpty()) {
			    		if (!StringUtils.isEmpty(user.getCspId())) {
			    			menuList = pageConfigService.getMenuByCspId(user.getCspId());
						}
					}
			    	if (menuList.isEmpty()) {
			    		menuList = pageConfigService.getMenuByAppidAndDefaultTypeLessThan(user.getAppId(), 1);	//表示绑定了房屋的默认菜单
					}
			    	if (menuList.isEmpty()) {
				    	menuList = pageConfigService.getMenuByDefaultTypeLessThan(1);	//未绑定房屋的默认菜单(全局)
					}
			    } else {	//未绑定房屋的
			    	menuList = pageConfigService.getMenuByAppidAndDefaultTypeLessThan(user.getAppId(), 2);	//表示绑定了房屋的默认菜单
			    	if (menuList.isEmpty()) {
				    	menuList = pageConfigService.getMenuByDefaultTypeLessThan(2);	//未绑定房屋的默认菜单(全局)
					}
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
				log.info("current user id in session is not the same with the id in database. user : " + user + ", sessionId: " + request.getSession().getId());
				HttpSession httpSession = request.getSession();
				if (httpSession != null) {
					log.info("will invalidate current session, sessionId : " + httpSession.getId());
					//sessionAttr:sessionUser
					httpSession.removeAttribute(Constants.USER);
					httpSession.invalidate();
					Thread.sleep(1000l);
				}
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
		log.info("getyzm request mobile: " + yzm.getMobile());
		log.info("getyzm request header [Access-Control-Allow-Token]: " + request.getHeader("Access-Control-Allow-Token"));
		String token = request.getHeader("Access-Control-Allow-Token");
		if (StringUtils.isEmpty(token)) {
			return new BaseResult<String>().failMsg("invalid request!");
		}
		if (yzm.getType() == 0) {
			yzm.setType(ModelConstant.SMS_TYPE_REG);
		}
		User dbUser = userService.getById(user.getId());
		User currUser = null;
		if (dbUser != null) {
			currUser = dbUser;
		} else {
			currUser = user;
		}
		boolean result = smsService.sendVerificationCode(currUser, yzm.getMobile(), requestIp, yzm.getType());
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
		String token = request.getHeader("Access-Control-Allow-Token");
		if (StringUtils.isEmpty(token)) {
			token = request.getHeader("access-control-allow-token");
		}
		log.info("getyzm1 request header [Access-Control-Allow-Token]: " + token);
		
		int smsType = ModelConstant.SMS_TYPE_INVOICE;
		if (ModelConstant.SMS_TYPE_RECEIPT == yzm.getType()) {
			smsType = ModelConstant.SMS_TYPE_RECEIPT;
		}
		boolean result = smsService.verifySmsToken(trade_water_id, smsType, token, yzm.getAppid());
		if (!result) {
			return new BaseResult<String>().failMsg("invalid request!");
		}
		result = smsService.sendInvoiceVerificationCode(new User(), yzm.getMobile(), requestIp, smsType, trade_water_id);
		if(!result) {
		    return new BaseResult<String>().failMsg("发送验证码失败");
		}
	    return  new BaseResult<String>().success("验证码发送成功");
    }

	@RequestMapping(value = "/savePersonTel1", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<UserInfo> savePersonTel1(@RequestBody(required = false) Map<String, String> postData) {
		String tel = postData.get("tel");
		if(StringUtils.isEmpty(tel)) {
			return new BaseResult<UserInfo>().failMsg("更新手机号失败,手机号为空！");
		}
		String id = postData.get("id");
		if(StringUtils.isEmpty(id)) {
			return new BaseResult<UserInfo>().failMsg("用户编号不能为空");
		}
		User user = userService.getById(Long.parseLong(id));
		if(user != null) {
			//TODO 这里模拟修改手机号
			user.setTel(tel);
			userService.save(user);
			//如果是旺都用户，需要同步
			wdService.syncUserTel(user);
			return new BaseResult<UserInfo>().success(new UserInfo(user));
		} else {
			return new BaseResult<UserInfo>().failMsg("用户不存在");
		}
	}

	@RequestMapping(value = "/savePersonTel", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<UserInfo> savePersonTel(HttpSession session, @ModelAttribute(Constants.USER)User user, @RequestBody(required = false) Map<String, String> postData) {
		String tel = postData.get("tel");
		if(StringUtils.isEmpty(tel)) {
			return new BaseResult<UserInfo>().failMsg("更新手机号失败,手机号为空！");
		}
		//TODO 这里模拟修改手机号
		user.setTel(tel);
		userService.save(user);
		session.setAttribute(Constants.USER, user);

		//如果是旺都用户，需要同步
		wdService.syncUserTel(user);
		return new BaseResult<UserInfo>().success(new UserInfo(user));
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
	 * @param session
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/h5/authorize", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<UserInfo> authorize(HttpSession session, @RequestBody H5AuthorizeVo vo) throws Exception {
		User userAccount;
		if(!StringUtils.isEmpty(vo.getCode())) {
			if (userService.checkDuplicateLogin(session, vo.getCode())) {
				throw new BizValidateException(599, 0, "正在登陆中，请耐心等待。如较长时间无响应，请刷新重试。");
			}
			if(ModelConstant.H5_USER_TYPE_ALIPAY.equals(vo.getSourceType())) {
				AccessTokenOAuth userOauth = userService.getAlipayAuth(vo.getCode());
				if(StringUtils.isEmpty(userOauth.getAppid())) {
					userOauth.setAppid(vo.getAppid());
				}
				userAccount = userService.getUserByAliUserIdAndAliAppid(userOauth.getOpenid(), userOauth.getAppid());
				if (userAccount == null) {
					userAccount = userService.saveAlipayMiniUserToken(userOauth);
				}
			} else if(ModelConstant.H5_USER_TYPE_WECHAT.equals(vo.getSourceType())) {
				AccessTokenOAuth oAuth = userService.getAccessTokenOAuth(vo.getCode(), vo.getAppid());
				UserWeiXin weixinUser = new UserWeiXin();
				weixinUser.setOpenid(oAuth.getOpenid());
				weixinUser.setUnionid(oAuth.getUnionid());
				userAccount = userService.updateUserLoginInfo(weixinUser, vo.getAppid());
			} else {
				return new BaseResult<UserInfo>().failMsg("不支持的授权方式，请使用微信或支付宝");
			}
		} else {
			return new BaseResult<UserInfo>().failMsg("授权失败，请刷新重试！");
		}
		if(userAccount == null) {
			return new BaseResult<UserInfo>().failMsg("用户不存在！");
		}
		UserInfo userInfo = new UserInfo(userAccount);
		session.setAttribute(Constants.USER, userAccount);
		return new BaseResult<UserInfo>().success(userInfo);
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
	 * @param request
	 * @param user
	 * @param switchSectReq
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/switchSect", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<UserInfo> switchSect(HttpServletRequest request, @ModelAttribute(Constants.USER) User user, 
			@RequestBody SwitchSectReq switchSectReq) throws Exception {
		
		long beginTime = System.currentTimeMillis();
		String appid = user.getAppId();
		String openid = user.getOpenid();
	    String currAppid = switchSectReq.getAppid();
	    boolean isMiniUser = false;
	    if (!StringUtils.isEmpty(currAppid) && currAppid.equals(user.getMiniAppId())) {
	    	appid = user.getMiniAppId();
	    	openid = user.getMiniopenid();
	    	isMiniUser = true;
		}
		User dbUser = userService.switchSect(user, openid, switchSectReq);
		BeanUtils.copyProperties(dbUser, user);
	    request.getSession().setAttribute(Constants.USER, user);
	    
	    UserInfo userInfo = null;
	    if (!isMiniUser) {
	    	OperatorDefinition odDefinition  = operatorService.defineOperator(user);
	    	/* 2021-02-23 工作人远弹出消息订阅的窗口 start */
			List<MsgTemplate> msgTemplateListAll = wechatMsgService.getSubscribeMsgTemplate(appid, ModelConstant.MSG_TYPE_SUBSCRIBE_MSG, ModelConstant.SUBSCRIBE_MSG_TEMPLATE_BIZ_TYPE_OPERATOR);
			List<String> templateIds = new ArrayList<>();
			for (MsgTemplate msgTemplate : msgTemplateListAll) {
				templateIds.add(msgTemplate.getValue());
			}
		    /* 2021-02-23 工作人远弹出消息订阅的窗口 end */
		    
		    userInfo = new UserInfo(user, odDefinition, templateIds);

		    Map<String, String> paramMap = paramService.getWuyeParam(user);
		    userInfo.setCfgParam(paramMap);
		    
		    boolean repairService = paramService.repairServiceAvailable(user);
		    userInfo.setRepairService(repairService);	//新版工单服务是否开通
		    
		    List<BottomIcon> iconList = pageConfigService.getBottomIcon(appid);
		    List<BgImage> bgImageList = pageConfigService.getBgImage(appid);
		    List<WuyePayTabs> tabsList = pageConfigService.getWuyePayTabs(appid);
		    userInfo.setIconList(iconList);
		    userInfo.setBgImageList(bgImageList);
		    userInfo.setWuyeTabsList(tabsList);
		    List<Menu> menuList = new ArrayList<>();
		    /*
		     * 取菜单顺序：
		     * 1.绑定访问的用户，先取小区级别，没有取公司级别，公司级别没有的，取app级别的，再没有的，取默认菜单
		     * 2.未绑定房屋的用户，取默认菜单
		     */
		    if (!StringUtils.isEmpty(user.getSectId()) && !"0".equals(user.getSectId())) {	//绑定房屋的
		    	menuList = pageConfigService.getMenuBySectId(user.getSectId());
		    	if (menuList.isEmpty()) {
		    		menuList = pageConfigService.getMenuByCspId(user.getCspId());
				}
		    	if (menuList.isEmpty()) {
		    		menuList = pageConfigService.getMenuByAppidAndDefaultTypeLessThan(appid, 1);	//表示绑定了房屋的默认菜单
				}
		    	if (menuList.isEmpty()) {
			    	menuList = pageConfigService.getMenuByDefaultTypeLessThan(1);	//未绑定房屋的默认菜单(全局)
				}
		    } else {	//未绑定房屋的
		    	menuList = pageConfigService.getMenuByAppidAndDefaultTypeLessThan(appid, 2);	//表示绑定了房屋的默认菜单
		    	if (menuList.isEmpty()) {
			    	menuList = pageConfigService.getMenuByDefaultTypeLessThan(2);	//未绑定房屋的默认菜单(全局)
				}
		    }
		    userInfo.setMenuList(menuList);
		    
		    WechatCard wechatCard = wechatCardService.getWechatMemberCard(user.getOpenid());	//TODO 缓存，主要是积分的变动频率。更新积分时需要刷新缓存
		    if (wechatCard == null || StringUtils.isEmpty(wechatCard.getCardCode())) {
				//do nothing
			}else {
				userInfo.setPoint(wechatCard.getBonus());
			}
		    
		    QrCode qrCode = pageConfigService.getQrCode(appid);
		    String qrLink = "";
		    if (qrCode != null) {
		    	qrLink = qrCode.getQrLink();
			}
		    
		    CsHotline csHotline = pageConfigService.getCsHotline(appid);
		    String hotline = "";
		    if (csHotline != null) {
		    	hotline = csHotline.getHotline();
			}
		    
		    userInfo.setQrCode(qrLink);
		    userInfo.setCsHotline(hotline);
		    userInfo.setCardStatus(wechatCard.getStatus());
		    userInfo.setCardService(systemConfigService.isCardServiceAvailable(appid));
		    userInfo.setCoronaPrevention(systemConfigService.coronaPreventionAvailable(appid));
		    userInfo.setDonghu(systemConfigService.isDonghu(appid));
		    userInfo.setCardPayService(systemConfigService.isCardPayServiceAvailabe(appid));
		} else {
			String roleId = user.getRoleId();
	        if (StringUtils.isEmpty(roleId)) {
	            roleId = "99";
	        }
	        
	        OrgOperator orgOperator = userService.getOrgOperator(user);
	        userInfo = new UserInfo(user, orgOperator);
	        if (orgOperator != null) {
	        	List<GroupMenuInfo> orgMenuList = pageConfigService.getOrgMenu(roleId, orgOperator.getOrgType());
	            userInfo.setOrgMenuList(orgMenuList);
			}
	        
	        List<Menu> menuList = pageConfigService.getMenuByAppidAndDefaultTypeLessThan(user.getMiniAppId(), 2);	//表示绑定了房屋的默认菜单
		    userInfo.setMenuList(menuList);
		    userInfo.setPermission(true);

			Map<String, String> paramMap = paramService.getWuyeParam(user);
			userInfo.setCfgParam(paramMap);
		}
	   
	    		    
	    long endTime = System.currentTimeMillis();
		log.info("switch sect :" + user.getName() + ", 耗时：" + ((endTime-beginTime)));
		
	    return new BaseResult<UserInfo>().success(userInfo);
	}
	
	
	/**
     * 小程序登录
     *
     * @param session
     * @param code
     * @param reqPath
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/miniprogram/login/{code}", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult<UserInfo> miniLogin(HttpSession session, @PathVariable String code, @RequestBody(required = false) String reqPath) throws Exception {
        long beginTime = System.currentTimeMillis();
        User user = null;
        if (!StringUtils.isEmpty(code)) {
            if (user == null) {
                if (userService.checkDuplicateLogin(session, code)) {
                    throw new BizValidateException("正在登陆中，请耐心等待。如较长时间无响应，请刷新重试。");
                }
                UserMiniprogram miniUser = userService.getWechatMiniUserSessionKey(code);

                user = userService.saveMiniUserSessionKey(miniUser);
                if (!StringUtils.isEmpty(user.getOpenid()) && !"0".equals(user.getOpenid())) {
                	userService.recacheMiniUser(user);
                }
            }
            session.setAttribute(Constants.USER, user);
        }
        if (user == null) {
            return new BaseResult<UserInfo>().failMsg("用户不存在！");
        }
        long endTime = System.currentTimeMillis();
        log.info("user:" + user.getName() + "login，耗时：" + ((endTime - beginTime) / 1000));

        String roleId = user.getRoleId();
        if (StringUtils.isEmpty(roleId)) {
            roleId = "99";
        }
        
        OrgOperator orgOperator = userService.getOrgOperator(user);
        UserInfo userInfo = new UserInfo(user, orgOperator);
        
        List<Menu> menuList = pageConfigService.getMenuByAppidAndDefaultTypeLessThan(user.getMiniAppId(), 2);	//表示绑定了房屋的默认菜单
	    userInfo.setMenuList(menuList);

        if (orgOperator != null) {
        	List<GroupMenuInfo> orgMenuList = pageConfigService.getOrgMenu(roleId, orgOperator.getOrgType());
            userInfo.setOrgMenuList(orgMenuList);
		}
        userInfo.setPermission(true);
        return new BaseResult<UserInfo>().success(userInfo);
    }
    
    /**
     * 小程序登录
     *
     * @param session
     * @param code
     * @param reqPath
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/miniprogram/login/v2/{appid}/{code}", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult<UserInfo> miniLogin(HttpSession session, @PathVariable String appid, @PathVariable String code, @RequestBody(required = false) String reqPath) throws Exception {
        long beginTime = System.currentTimeMillis();
        User user = null;
        if (!StringUtils.isEmpty(code)) {
            if (user == null) {
                if (userService.checkDuplicateLogin(session, code)) {
                    throw new BizValidateException("正在登陆中，请耐心等待。如较长时间无响应，请刷新重试。");
                }
                UserMiniprogram miniUser = userService.getWechatMiniUserSessionKey(appid, code);

                user = userService.saveMiniUserSessionKey(miniUser);
                if (!StringUtils.isEmpty(user.getOpenid()) && !"0".equals(user.getOpenid())) {
                	userService.recacheMiniUser(user);
                }
            }
            session.setAttribute(Constants.USER, user);
        }
        if (user == null) {
            return new BaseResult<UserInfo>().failMsg("用户不存在！");
        }
        long endTime = System.currentTimeMillis();
        log.info("user:" + user.getName() + "login，耗时：" + ((endTime - beginTime) / 1000));

        String roleId = user.getRoleId();
        if (StringUtils.isEmpty(roleId)) {
            roleId = "99";
        }
        
        OrgOperator orgOperator = userService.getOrgOperator(user);
        UserInfo userInfo = new UserInfo(user, orgOperator);
        if (orgOperator != null) {
        	List<GroupMenuInfo> orgMenuList = pageConfigService.getOrgMenu(roleId, orgOperator.getOrgType());
            userInfo.setOrgMenuList(orgMenuList);
		}
        
        List<Menu> menuList = pageConfigService.getMenuByAppidAndDefaultTypeLessThan(user.getMiniAppId(), 2);	//表示绑定了房屋的默认菜单
	    userInfo.setMenuList(menuList);
	    userInfo.setPermission(true);

		Map<String, String> paramMap = paramService.getWuyeParam(user);
		userInfo.setCfgParam(paramMap);

        return new BaseResult<UserInfo>().success(userInfo);
    }
    
    /**
     * 小程序获取用户手机
     *
     * @param session
     * @param code
     * @param reqPath
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/miniprogram/phoneNumber/{code}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult<UserInfo> getMiniUserPhone(HttpServletRequest request, @ModelAttribute(Constants.USER)User user, @PathVariable String code) throws Exception {
        long beginTime = System.currentTimeMillis();
        MiniUserPhone miniUserPhone = userService.getMiniUserPhone(user, code);
        User savedUser = userService.saveMiniUserPhone(user, miniUserPhone);
        if (!StringUtils.isEmpty(user.getOpenid()) && !"0".equals(user.getOpenid())) {
        	userService.recacheMiniUser(user);
        }
        BeanUtils.copyProperties(savedUser, user);
        request.getSession().setAttribute(Constants.USER, user);
        long endTime = System.currentTimeMillis();
        log.info("user:" + user.getName() + "getPhoneNumber，耗时：" + ((endTime - beginTime) / 1000));
        String roleId = user.getRoleId();
        if (StringUtils.isEmpty(roleId)) {
            roleId = "99";
        }
        
        OrgOperator orgOperator = userService.getOrgOperator(user);
        UserInfo userInfo = new UserInfo(user, orgOperator);

        if (orgOperator != null) {
        	List<GroupMenuInfo> menuList = pageConfigService.getOrgMenu(roleId, orgOperator.getOrgType());
            userInfo.setOrgMenuList(menuList);
		}
        userInfo.setPermission(true);
        return new BaseResult<UserInfo>().success(userInfo);
    }
    
    /**
     * 更新小程序用户头像和昵称
     * @param session
     * @param code
     * @param reqPath
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/userInfo/update", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult<String> updateUserInfo(HttpServletRequest request, @ModelAttribute(Constants.USER)User user, @RequestBody Map<String, String> map) throws Exception {
        
    	User dbuser = userService.updateUserInfo(user, map);
        BeanUtils.copyProperties(dbuser, user);
        request.getSession().setAttribute(Constants.USER, user);
        return new BaseResult<String>().success(Constants.PAGE_SUCCESS);
    }
    
    /**
     * 小程序登录
     *
     * @param session
     * @param code
     * @param reqPath
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/alipay/miniprogram/login/v2/{appid}/{code}", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult<UserInfo> alipayMiniLogin(HttpSession session, @PathVariable String appid, @PathVariable String code, @RequestBody(required = false) String reqPath) throws Exception {
        long beginTime = System.currentTimeMillis();
        User user = null;
        if (!StringUtils.isEmpty(code)) {
            if (user == null) {
                if (userService.checkDuplicateLogin(session, code)) {
                    throw new BizValidateException("正在登陆中，请耐心等待。如较长时间无响应，请刷新重试。");
                }
                AccessTokenOAuth userOauth = userService.getAlipayAuth(appid, code);
                log.info("userOauth : " + userOauth);
                user = userService.getUserByAliUserIdAndAliAppid(userOauth.getOpenid(), userOauth.getAppid());
                if (user == null) {
                user = userService.saveAlipayMiniUserToken(userOauth);
            }
            }
            session.setAttribute(Constants.USER, user);
        }
        if (user == null) {
            return new BaseResult<UserInfo>().failMsg("用户不存在！");
        }
        long endTime = System.currentTimeMillis();
        log.info("user:" + user.getName() + "login，耗时：" + ((endTime - beginTime) / 1000));

        UserInfo userInfo = new UserInfo(user);
        userInfo.setReqPath(reqPath);
        boolean isValid = userService.validateMiniPageAccess(user, reqPath);
        userInfo.setPermission(true);
        if (!isValid) {
            throw new BizValidateException("没有访问权限");
        }
        return new BaseResult<UserInfo>().success(userInfo);
    }
    
    /**
     * 小程序获取用户手机
     *
     * @param session
     * @param code
     * @param reqPath
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/alipay/miniprogram/phoneNumber", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult<UserInfo> getAlipayMiniUserPhone(HttpServletRequest request, @ModelAttribute(Constants.USER)User user, @RequestBody Map<String, String> dataMap) throws Exception {
        long beginTime = System.currentTimeMillis();
        String encryptedData = dataMap.get("encryptedData");
        AliMiniUserPhone aliMiniUserPhone = userService.getAlipayMiniUserPhone(user, encryptedData);
        MiniUserPhone miniUserPhone = new MiniUserPhone();
        MiniUserPhone.PhoneInfo phoneInfo = new PhoneInfo();
        phoneInfo.setPhoneNumber(aliMiniUserPhone.getMobile());
        miniUserPhone.setPhone_info(phoneInfo);
        User savedUser = userService.saveMiniUserPhone(user, miniUserPhone);
        if (!StringUtils.isEmpty(user.getAliappid()) && !StringUtils.isEmpty(user.getAliuserid())) {
        	userService.recacheAliMiniUser(user);
        }
        BeanUtils.copyProperties(savedUser, user);
        request.getSession().setAttribute(Constants.USER, user);
        long endTime = System.currentTimeMillis();
        log.info("user:" + user.getName() + "getPhoneNumber，耗时：" + ((endTime - beginTime) / 1000));
        String roleId = user.getRoleId();
        if (StringUtils.isEmpty(roleId)) {
            roleId = "99";
        }
        
        OrgOperator orgOperator = userService.getOrgOperator(user);
        UserInfo userInfo = new UserInfo(user, orgOperator);

        if (orgOperator != null) {
        	List<GroupMenuInfo> menuList = pageConfigService.getOrgMenu(roleId, orgOperator.getOrgType());
            userInfo.setOrgMenuList(menuList);
		}
        userInfo.setPermission(true);
        return new BaseResult<UserInfo>().success(userInfo);
    }
    
    /**
     * 上海物业小程序缴费用户登陆
     * @param session
     * @param h5UserDTO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/alipay/h5/login", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<UserInfo> h5Login(HttpSession session, @RequestBody(required = false) H5UserDTO h5UserDTO) throws Exception {
    	
    	if (StringUtils.isEmpty(h5UserDTO.getUserId()) ) {
			throw new BizValidateException("请传入支付宝用户user_id或微信用户openid");
		}
    	
    	if (StringUtils.isEmpty(h5UserDTO.getAppid()) ) {
			throw new BizValidateException("请传入支付宝或微信appid");
		}
    	User sessionUser = (User) session.getAttribute(Constants.USER);
    	log.info("shwyLogin user in session :" + sessionUser);
    	if (sessionUser != null) {
			if (!sessionUser.getAliappid().equals(h5UserDTO.getAppid())) {
				session.setMaxInactiveInterval(1);
				session.removeAttribute(Constants.USER);
				session.invalidate();
				throw new BizValidateException(65, "clear user cache!");
			}
		}
		long beginTime = System.currentTimeMillis();
    	log.info("h5Login : " + h5UserDTO);
    	if (StringUtils.isEmpty(h5UserDTO.getClientType())) {
			if (org.apache.commons.lang3.StringUtils.isNumeric(h5UserDTO.getUserId())) {
				h5UserDTO.setClientType(ModelConstant.H5_USER_TYPE_ALIPAY);
			} else {
				h5UserDTO.setClientType(ModelConstant.H5_USER_TYPE_MINNI);
			}
		}
    	h5UserDTO.setFrom(ModelConstant.KEY_USER_SYS_SHWY);
    	User userAccount = null;
//    	if (!StringUtils.isEmpty(h5UserDTO.getAuId()) && !"987654102".equals(h5UserDTO.getAuId())) {	//987654102 for test
//    		List<User> userList = userService.getUserByOriSysAndOriUserId("_shwy", Long.valueOf(h5UserDTO.getAuId()));
//    		if (userList != null && !userList.isEmpty() ) {
//    			for (User user : userList) {
//					if (h5UserDTO.getUserId().equals(user.getAliuserid()) || h5UserDTO.getUserId().equals(user.getOpenid())) {
//						userAccount = user;
//						break;
//					}
//				}
//			}
//		}
    	if (userAccount == null) {
    		if (ModelConstant.H5_USER_TYPE_ALIPAY.equals(h5UserDTO.getClientType())) {
//				userAccount = userService.getUserByAliUserId(h5UserDTO.getUserId());
				userAccount = userService.getUserByAliUserIdAndAliAppid(h5UserDTO.getUserId(), h5UserDTO.getAppid());
			} else if (ModelConstant.H5_USER_TYPE_WECHAT.equals(h5UserDTO.getClientType())) {
				userAccount = userService.getByMiniopenid(h5UserDTO.getUserId());
//				userAccount = userService.multiFindByOpenId(h5UserDTO.getUserId());
			}
		}
    	userAccount = userService.saveH5User(userAccount, h5UserDTO);
    	
		long endTime = System.currentTimeMillis();
	    UserInfo userInfo = new UserInfo(userAccount);
	    log.info("h5 user:" + h5UserDTO.getUserId() + "login，耗时：" + ((endTime-beginTime)/1000));
	    
	    session.setAttribute(Constants.USER, userAccount);
	    return new BaseResult<UserInfo>().success(userInfo);

    }
    
    /**
     * 支付宝生活缴费用户登陆
     * @param session
     * @param h5UserDTO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/alipay/lifepay/login", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<UserInfo> lifepayLogin(HttpSession session, @RequestBody(required = false) H5UserDTO h5UserDTO) throws Exception {
        
    	if (StringUtils.isEmpty(h5UserDTO.getUserId()) ) {
			throw new BizValidateException("请传入支付宝用户user_id");
		}
        
    	if (StringUtils.isEmpty(h5UserDTO.getAppid()) ) {
			throw new BizValidateException("请传入支付宝appid");
}
    	User sessionUser = (User) session.getAttribute(Constants.USER);
    	log.info("lifepayLogin user in session :" + sessionUser);
    	if (sessionUser != null) {
			if (!sessionUser.getAliappid().equals(h5UserDTO.getAppid())) {
				session.setMaxInactiveInterval(1);
				session.removeAttribute(Constants.USER);
				session.invalidate();
				throw new BizValidateException(65, "clear user cache!");
			}
		}
		long beginTime = System.currentTimeMillis();
    	log.info("lifepayLogin : " + h5UserDTO);
		h5UserDTO.setClientType(ModelConstant.H5_USER_TYPE_ALIPAY);
		h5UserDTO.setFrom(ModelConstant.KEY_USER_SYS_LIFEPAY);
		
    	User userAccount = userService.getUserByAliUserIdAndAliAppid(h5UserDTO.getUserId(), h5UserDTO.getAppid());
    	userAccount = userService.saveH5User(userAccount, h5UserDTO);
    	log.info("lifepayLogin userId:{}, wuyeId:{}, aliuserid:{}, aliappid: {}", 
    			userAccount.getId(), userAccount.getWuyeId(), userAccount.getAliuserid(), userAccount.getAliappid());
		long endTime = System.currentTimeMillis();
	    UserInfo userInfo = new UserInfo(userAccount);
	    log.info("lifepay user:" + h5UserDTO.getUserId() + "login，耗时：" + ((endTime-beginTime)/1000));
	    
	    session.setAttribute(Constants.USER, userAccount);
	    return new BaseResult<UserInfo>().success(userInfo);

    }

    /**
     * 支付宝生活缴费用户订阅消息
     * @param session
     * @param h5UserDTO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/alipay/lifepay/subscribe", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<String> lifepayLogin(@ModelAttribute(Constants.USER)User user) throws Exception {
    	SubscribeReq subscribeReq = new SubscribeReq();
    	subscribeReq.setUser(user);
    	subscribeReq.setSubscribe(1);
    	aliSubscribeMsgService.addSubscribe(subscribeReq);
	    return new BaseResult<String>().success("");

    }
}

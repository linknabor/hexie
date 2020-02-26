package com.yumu.hexie.web.interceptor;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wechat.entity.user.UserWeiXin;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.user.UserService;

/**
 * 新用户关注
 * @author Administrator
 */
public class CheckUserAddedInterceptor implements HandlerInterceptor {
	
	private Logger logger =  LoggerFactory.getLogger(CheckUserAddedInterceptor.class); 

	@Inject
	private UserService userService;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		if (request.getSession().getAttribute(Constants.USER) == null) {
			String code = request.getParameter("code");
			if (StringUtil.isNotEmpty(code)) {
				if(request.getRequestURI().indexOf("login") >= 0) {
					return true;
				} else {
					logger.info("requeste uri : " + request.getRequestURI());
					UserWeiXin weixinUser = userService.getOrSubscibeUserByCode(code);
					User userAccount = userService.multiFindByOpenId(weixinUser.getOpenid());
					request.getSession().setAttribute(Constants.USER, userAccount);
				}
			}
		}else{
			User userAccount = (User) request.getSession().getAttribute(Constants.USER);
			if(StringUtils.isEmpty(userAccount.getWuyeId())) {
				logger.info("user:" + userAccount + ", wuyeId has no value, will update !");
				userService.bindWuYeId(userAccount);
				User dbUser = userService.getById(userAccount.getId());
				if (dbUser!=null && !StringUtils.isEmpty(dbUser.getWuyeId())) {
					userAccount.setWuyeId(dbUser.getWuyeId());
					request.getSession().setAttribute(Constants.USER, userAccount);
				}
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

}

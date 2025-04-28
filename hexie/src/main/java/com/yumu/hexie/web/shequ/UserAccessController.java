package com.yumu.hexie.web.shequ;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.common.util.RequestUtil;
import com.yumu.hexie.integration.wuye.resp.UserAccessSpotResp;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.UserAccessService;
import com.yumu.hexie.service.shequ.req.UserAccessRecordReq;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import com.yumu.hexie.web.user.req.UserAgent;

/**
 * 小区门禁控制
 * @author david
 *
 */
@RestController
@RequestMapping("/userAccess")
public class UserAccessController extends BaseController {
	
	private static Logger logger = LoggerFactory.getLogger(UserAccessController.class);
	
	@Resource
	private UserAccessService userAccessService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/spot/{spotId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<UserAccessSpotResp> getAccessSpot(@ModelAttribute(Constants.USER)User user, @PathVariable String spotId) throws Exception{
		
		UserAccessSpotResp spot = userAccessService.getAccessSpot(user, spotId);
		return BaseResult.successResult(spot);
	} 

	/**
	 * 添加评论
	 * @param user
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<String> saveAccessRecord(HttpServletRequest request,
			@ModelAttribute(Constants.USER)User user, @RequestBody UserAccessRecordReq userAccessRecordReq) throws Exception{
		
		String userAgentHead = request.getHeader("user-agent");
		UserAgent userAgent = new UserAgent(userAgentHead);
		String loginDevice = userAgent.getUser_system() + " - " + userAgent.getUser_browser();
		String loginIp = RequestUtil.getRealIp(request);
		userAccessRecordReq.setAccessDevice(loginDevice);
		userAccessRecordReq.setAccessIp(loginIp);
		userAccessRecordReq.setName(user.getNickname());
		
		logger.info("访客IP[" + loginIp+ "],访客操作系统[" + userAgent.getUser_system() + "],浏览器[" + userAgent.getUser_browser() + "]");
		
		userAccessService.saveAccessRecord(user, userAccessRecordReq);
		return BaseResult.successResult(Constants.PAGE_SUCCESS);
	}
	
}

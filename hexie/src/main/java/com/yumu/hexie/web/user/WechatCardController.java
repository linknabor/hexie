package com.yumu.hexie.web.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.integration.wechat.entity.card.PreActivateReq;
import com.yumu.hexie.integration.wechat.entity.card.PreActivateResp;
import com.yumu.hexie.integration.wechat.entity.common.JsSign;
import com.yumu.hexie.integration.wuye.vo.RefundDTO;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.card.WechatCardService;
import com.yumu.hexie.service.common.WechatCoreService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@RequestMapping(value = "/card")
@RestController
public class WechatCardController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(WechatCardController.class);
	
	@Autowired
	private WechatCardService wechatCardService;
	
	@Autowired
	 private WechatCoreService wechatCoreService;
	
	/**
	 * 微信回调激活
	 * @param request
	 * @param cardId
	 * @param encryptCode
	 * @param openid
	 * @param outerStr
	 * @param activateTicket
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/preActivate", method = RequestMethod.POST)
	public BaseResult<PreActivateResp> preActivate(HttpSession httpSession, HttpServletRequest request,
			@RequestParam(name="card_id", required = true) String cardId,
			@RequestParam(name="encrypt_code", required = true) String encryptCode,
			@RequestParam(name="openid", required = true) String openid,
			@RequestParam(name="outer_str", required = false) String outerStr,
			@RequestParam(name="activate_ticket", required = true) String activateTicket) {
		
		PreActivateReq preActivateReq = new PreActivateReq();
		preActivateReq.setCardId(cardId);
		preActivateReq.setEncryptCode(encryptCode);
		preActivateReq.setOpenid(openid);
		preActivateReq.setOuterStr(outerStr);
		preActivateReq.setActivateTicket(activateTicket);
		logger.info("preActivateReq is : " + preActivateReq);
		User user = wechatCardService.activate(preActivateReq);
		
		String url = request.getRequestURL().toString();
		JsSign jsSign = wechatCoreService.getJsSign(url, user.getAppId());
		PreActivateResp preActivateResp = new PreActivateResp();
		preActivateResp.setJsSign(jsSign);
		preActivateResp.setUser(user);
		if (!StringUtil.isEmpty(user.getTel())) {
			logger.info("new user activated, will reset user session !");
			httpSession.setAttribute(Constants.USER, user);
		}
		return BaseResult.successResult(preActivateResp);
		
	}
	
	/**
	 * 页面领卡激活
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/activateUrl", method = RequestMethod.GET)
	public BaseResult<String> getCardOnPage(@ModelAttribute(Constants.USER) User user) {
		
		String url = wechatCardService.getActivateUrlOnPage(user);
		return BaseResult.successResult(url);
		
	}
	
	/*
	 * 菜单领卡激活
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/activateUrlOnMenu", method = RequestMethod.GET)
	public BaseResult<String> getCardOnMenu(@RequestParam String oriApp){
		
		User user = new User();
		if (StringUtil.isEmpty(oriApp)) {
			oriApp = ConstantWeChat.APPID;	//合协社区取默认appid，因为合协菜单上没有带oriApp
		}
		user.setAppId(oriApp);
		String url = wechatCardService.getActivateUrlOnPage(user);
		return BaseResult.successResult(url);
	}
	
	@RequestMapping(value = "/refund", method = RequestMethod.POST)
	public String refund(@RequestParam(name="sysCode") String sysCode,
			@RequestBody RefundDTO refundDTO) {
		
		if (!"hexie".equals(sysCode)) {
			return "";
		}
		wechatCardService.wuyeRefund(refundDTO);
		return Constants.SERVICE_SUCCESS;
	}
	
}

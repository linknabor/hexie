package com.yumu.hexie.web.user;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.wechat.vo.UnionPayVO;
import com.yumu.hexie.integration.wuye.WuyeUtil;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.user.Member;
import com.yumu.hexie.model.user.MemberBill;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.user.impl.MemberServiceImpl;
import com.yumu.hexie.web.BaseController;

@Controller(value = "MemberController")
public class MemberController extends BaseController{
	
	private static final Logger log = LoggerFactory.getLogger(MemberController.class);
	@Inject
	private MemberServiceImpl memberServiceImpl;
	
	/**
	 * 获取用户会员
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/getMember",method = RequestMethod.GET)
	@ResponseBody
	public List<Member> getMember(@ModelAttribute(Constants.USER) User user){
		log.info("获取用户会员：手机号"+user.getTel());;
		return memberServiceImpl.getMember(user);
	}
	
	
	/**
	 * 会员支付接口
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/getMemberPayinfo",method = RequestMethod.GET)
	@ResponseBody
	public WechatPayInfo getMemberPayinfo(@ModelAttribute(Constants.USER) User user){
		log.info("会员支付接口：手机号"+user.getTel());;
		return memberServiceImpl.getPayInfo(user);
	}
	
	/**
	 * 会员回调
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/memberReturn",method = RequestMethod.GET)
	@ResponseBody
	public String memberReturn(@RequestBody UnionPayVO unionpayvo){
		log.info("回调进入：532858859");//数字方便搜素日志
		return memberServiceImpl.getNotify(unionpayvo);
	}
	
	
	
	@RequestMapping(value="/getMemberBillDetail",method = RequestMethod.GET)
	@ResponseBody
	public List<MemberBill> getMemberBillDetail(@ModelAttribute(Constants.USER) User user){
		
		return null;
	}
}

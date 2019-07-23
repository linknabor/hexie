package com.yumu.hexie.service.user;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yumu.hexie.integration.wechat.vo.UnionPayVO;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.user.Member;
import com.yumu.hexie.model.user.User;

public interface MemberService {
	public List<Member> getMember(User user);
	
	public WechatPayInfo getPayInfo(User user);
	
	public String getNotify(HttpServletRequest request, HttpServletResponse response);

	String getNotify(UnionPayVO unionpayvo);
}

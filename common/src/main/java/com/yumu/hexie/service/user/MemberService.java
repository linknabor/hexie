package com.yumu.hexie.service.user;

import java.util.List;

import com.yumu.hexie.integration.wechat.vo.UnionPayVO;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.user.Member;
import com.yumu.hexie.model.user.User;

public interface MemberService {
	public List<Member> getMember(User user);
	
	public WechatPayInfo getPayInfo(User user);

	public String getNotify(UnionPayVO unionpayvo);

	public List<Member> getMemberBillS(User user);
}

package com.yumu.hexie.service.user.impl;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.fabric.xmlrpc.base.Data;
import com.yumu.hexie.common.util.RSAUtil;
import com.yumu.hexie.common.util.UnionUtil;
import com.yumu.hexie.integration.wuye.WuyeUtil;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.user.Member;
import com.yumu.hexie.model.user.MemberBill;
import com.yumu.hexie.model.user.MemberBillRepository;
import com.yumu.hexie.model.user.MemberRepository;
import com.yumu.hexie.model.user.MemberRuleRelationshipsRepository;
import com.yumu.hexie.model.user.MemberRuleRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.MemberService;
import com.yumu.hexie.service.user.req.MemberVo;

public class MemberServiceImpl implements MemberService{

	private static final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);
	
	@Inject
	private MemberRepository memberRepository;
	@Inject
	private MemberRuleRepository memberRuleRepository;
	@Inject
	private MemberRuleRelationshipsRepository memberRuleRelationshipsRepository;
	@Inject
	private MemberBillRepository memberBillRepository;
	@Inject
	private UserRepository userRepository;
	
	private CouponServiceImpl couponServiceImpl;
	
	@Override
	public List<Member> getMember(User user) {
		// TODO Auto-generated method stub
		return memberRepository.findByUserid(user.getId());
	}

	@Override
	public WechatPayInfo getPayInfo(User user) {
		// TODO Auto-generated method stub
		MemberBill bill = new MemberBill();
		bill.setPrice(MemberVo.PRICE);//支付金额 98
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		bill.setStartdate(df.format(new Date()));//交易时间
		bill.setTrandate(df.format(new Date()).substring(0, 10));//交易日期
		bill.setStatus(MemberVo.MIDDLE);//交易状态
		bill.setUserid(user.getId());
		bill = memberBillRepository.save(bill);
		try {
			return WuyeUtil.getPrePayInfo(bill.getMemberbillid(), bill.getPrice(), user.getOpenid(),MemberVo.NOTIFYURL).getData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String getNotify(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		ServletInputStream sis = null;
		try {
			sis = request.getInputStream();
			byte [] bytes = new byte[4096];	//TODO大小可能要改
			sis.read(bytes);
			
			String requestStr = new String(bytes, "UTF-8");
			requestStr = requestStr.trim();
			requestStr = URLDecoder.decode(requestStr, "utf-8");
			Map<String, String> mapResp = UnionUtil.pullRespToMap(requestStr);
			requestStr = UnionUtil.mapToStr(mapResp);
			
			log.info("接受银联响应数据：" + requestStr);
			
			boolean signFlag = UnionUtil.verferSignData(requestStr);
			
			if (!signFlag) {
				//验签失败
				log.info("银联回调验签失败532858859");//数字只为记号 方便日志搜索
				return "FAIL";
			} 
			
			String respCode = (String)mapResp.get("respCode");
			log.info("银联返回状态："+respCode);
			if("0000".equals(respCode)) {
				String billid = (String)mapResp.get("orderNo");
				MemberBill mem = memberBillRepository.findByMemberbillid(Long.parseLong(billid));//根据账单id查询
				if(mem == null) {
					throw new BizValidateException("返回billID没有查询到账单");
				}
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
				mem.setEnddate(df.format(new Date()));
				mem.setStatus(MemberVo.SUCCESS);
				
				User user = userRepository.findById(mem.getUserid());
				if(user == null) {
					throw new BizValidateException("账单userid没有查询到用户");
				}
				
				memberBillRepository.save(mem);//账单完成
				
				List<Member> memberis = memberRepository.findByUserid(user.getId());

				Member member = new Member();
				member.setUserid(user.getId());
				Calendar c = Calendar.getInstance();
				if(memberis.isEmpty()) {
					c.setTime(new Date());
					c.add(Calendar.DAY_OF_MONTH, 365);
					member.setStartdate(df.format(new Date()).substring(0, 10));//获取当前日期
					member.setEnddate(df.format(c.getTime()).substring(0, 10));//当前日期加1年
				}else {
					String enddate = memberis.get(0).getEnddate();
					Date date = df.parse(enddate);
					c.setTime(date);
					c.add(Calendar.DAY_OF_MONTH, 365);
					member.setEnddate(df.format(c.getTime()).substring(0, 10));//根据之前日期加日期加1年
				}
				member.setStatus(MemberVo.MEMBER_YES);
				
				memberRepository.save(member);//保存会员
				couponServiceImpl.addCoupon4Member(user);//发放会员优惠卷
				
				return "SUCCESS";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return null;
	}
	
	
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		System.out.println(df.format(new Date()).substring(0, 10));// new Date()为获取当前系统时间
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_MONTH, 365);
		System.out.println(df.format(c.getTime()).substring(0, 10));// new Date()为获取当前系统时间
		String enddate = "2020-07-04";
		Date date = df.parse(enddate);
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, 365);
		System.out.println(df.format(c.getTime()).substring(0, 10));// new Date()为获取当前系统时间
	}

}

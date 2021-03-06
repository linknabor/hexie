package com.yumu.hexie.service.user.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yumu.hexie.common.util.OrderNoUtil;
import com.yumu.hexie.common.util.UnionUtil;
import com.yumu.hexie.integration.wechat.vo.UnionPayVO;
import com.yumu.hexie.integration.wuye.WuyeUtil;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.user.Member;
import com.yumu.hexie.model.user.MemberBill;
import com.yumu.hexie.model.user.MemberBillRepository;
import com.yumu.hexie.model.user.MemberRepository;
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
	private MemberBillRepository memberBillRepository;
	@Inject
	private UserRepository userRepository;
	@Inject
	private CouponServiceImpl couponServiceImpl;
	
	@Override
	public List<Member> getMember(User user) {
		return memberRepository.findByUserid(user.getId());
	}

	@Override
	public WechatPayInfo getPayInfo(User user) {
		
		log.info("会员支付接口：UserId:"+user.getId());
		try {
			MemberBill bill = new MemberBill();
			bill.setMemberbillid(OrderNoUtil.generateServiceOrderNo());
			bill.setPrice(MemberVo.PRICE);//支付金额 98
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			bill.setStartdate(df.format(new Date()));//交易时间
			bill.setTrandate(df.format(new Date()).substring(0, 10));//交易日期
			bill.setStatus(MemberVo.MIDDLE);//交易状态
			bill.setUserid(user.getId());
			bill = memberBillRepository.save(bill);
			
			return WuyeUtil.getMemberPrePayInfo(user, String.valueOf(bill.getMemberbillid()), bill.getPrice(),MemberVo.NOTIFYURL).getData();
		} catch (Exception e) {
			
			log.error("err msg :" + e.getMessage());
		}
		return null;
	}
	
	@Override
	public String getNotify(UnionPayVO unionpayvo) {
		try {
			
			String requestStr = unionpayvo.getUnionPayStr();
			
			log.info("接受银联响应数据：" + requestStr);
			
			boolean signFlag = UnionUtil.verferSignData(requestStr);
			
			if (!signFlag) {
				//验签失败
				log.info("银联回调验签失败532858859");//数字只为记号 方便日志搜索
				return "FAIL";
			} 
			
			log.info("银联返回状态："+unionpayvo.getRespCode());
			if("0000".equals(unionpayvo.getRespCode())) {
				String billid = unionpayvo.getOrderNo();
				MemberBill mem = memberBillRepository.findByMemberbillid(billid);//根据账单id查询
				if(mem == null) {
					throw new BizValidateException("返回billID没有查询到账单");
				}else {
					if(MemberVo.SUCCESS.equals(mem.getStatus())) {
						return "SUCCESS";//对方发起多次回调 才会进入此处
					}
				}
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
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
				df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
				if(memberis.isEmpty()) {
					c.setTime(new Date());
					c.add(Calendar.DAY_OF_MONTH, 365);
					member.setStartdate(df.format(new Date()).substring(0, 10));//获取当前日期
					member.setEnddate(df.format(c.getTime()).substring(0, 10));//当前日期加1年
				}else {
					member = memberis.get(0);
					if("0".equals(memberis.get(0).getStatus())) {
						String enddate = memberis.get(0).getEnddate();
						Date date = df.parse(enddate);
						c.setTime(date);
						c.add(Calendar.DAY_OF_MONTH, 365);
						member.setEnddate(df.format(c.getTime()).substring(0, 10));//根据之前日期加日期加1年
					}else {
						c.setTime(new Date());
						c.add(Calendar.DAY_OF_MONTH, 365);
						member.setStartdate(df.format(new Date()).substring(0, 10));//获取当前日期
						member.setEnddate(df.format(c.getTime()).substring(0, 10));//当前日期加1年
					}
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
//		String a  = "respCode=0000&orderNo=201907231633P94777&transId=10&orderDate=20190723&bankType=CFT&respDesc=交易成功&transAmt=1&signature=VBJmtX5It6Gp6scyk/FfT+ydI+N8ogfpJ58e1xrkYAAQOfK5D0AF1JRUC2JMLB//ikW5Rzak0FZNS257Q4nt3yuA3nGRXwJ6PAD4pw7/lhIjM9EhUG6D6KxmyUw7lMG/IajQQQaHUUzy/IKt5bc3wsAVg9oERmOw6NS/DARa7U8bAhnbPObJ/NS3J3jacYHERI2DFuq3l7TaK1UYkPY4xOzwj/gIA4JRQ3W6KyZNMJPeenQ7ZCXtAk4yW6VDdKcEcHNxJF8/ZdA9MYfp85Wz6xvV7hLl3ILb28rjxi/CXlRAYRZFyBlRcsHhmc/K0lMEd29dNWAIM4889vrEvMmRGA==&merNo=888290059501308&productId=0105";
//		Map<String, String> mapResp = UnionUtil.pullRespToMap(a);
	}

	@Override
	public List<Member> getMemberBillS(User user) {
		// TODO Auto-generated method stub
		try {
			List<Member> memberis = memberRepository.findByUserid(user.getId());
		
			Member member = new Member();
			member.setUserid(user.getId());
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
			if(memberis.isEmpty()) {
				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH, 365);
				member.setStartdate(df.format(new Date()).substring(0, 10));//获取当前日期
				member.setEnddate(df.format(c.getTime()).substring(0, 10));//当前日期加1年
				member.setStatus(MemberVo.MEMBER_YES);
				memberis.add(member);
				return memberis;
			}else {
				member = memberis.get(0);
				if("0".equals(memberis.get(0).getStatus())) {
					String enddate = memberis.get(0).getEnddate();
					Date date = df.parse(enddate);
					c.setTime(date);
					c.add(Calendar.DAY_OF_MONTH, 365);
					member.setEnddate(df.format(c.getTime()).substring(0, 10));//根据之前日期加日期加1年
				}else {
					c.setTime(new Date());
					c.add(Calendar.DAY_OF_MONTH, 365);
					member.setStartdate(df.format(new Date()).substring(0, 10));//获取当前日期
					member.setEnddate(df.format(c.getTime()).substring(0, 10));//当前日期加1年
				}
				member.setStatus(MemberVo.MEMBER_YES);
				memberis.remove(0);
				memberis.add(member);
				return memberis;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}


}

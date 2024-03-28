package com.yumu.hexie.service.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wuye.WuyeUtil;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.commonsupport.info.ProductRepository;
import com.yumu.hexie.model.distribution.OnSaleAreaItem;
import com.yumu.hexie.model.distribution.OnSaleAreaItemRepository;
import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.model.distribution.RgroupAreaItemRepository;
import com.yumu.hexie.model.localservice.bill.BaojieBill;
import com.yumu.hexie.model.localservice.bill.BaojieBillRepository;
import com.yumu.hexie.model.localservice.bill.YunXiyiBill;
import com.yumu.hexie.model.localservice.bill.YunXiyiBillRepository;
import com.yumu.hexie.model.market.Evoucher;
import com.yumu.hexie.model.market.EvoucherRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.market.saleplan.OnSaleRule;
import com.yumu.hexie.model.market.saleplan.OnSaleRuleRepository;
import com.yumu.hexie.model.market.saleplan.RgroupRule;
import com.yumu.hexie.model.market.saleplan.RgroupRuleRepository;
import com.yumu.hexie.model.payment.PaymentConstant;
import com.yumu.hexie.model.payment.PaymentOrderRepository;
import com.yumu.hexie.model.payment.RefundOrder;
import com.yumu.hexie.model.payment.RefundOrderRepository;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.statistic.PageView;
import com.yumu.hexie.model.statistic.PageViewRepository;
import com.yumu.hexie.model.system.BizError;
import com.yumu.hexie.model.system.BizErrorRepository;
import com.yumu.hexie.model.user.Member;
import com.yumu.hexie.model.user.MemberBill;
import com.yumu.hexie.model.user.MemberBillRepository;
import com.yumu.hexie.model.user.MemberRepository;
import com.yumu.hexie.model.user.RgroupOwner;
import com.yumu.hexie.model.user.RgroupOwnerRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.SmsService;
import com.yumu.hexie.service.common.WechatCoreService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.o2o.BaojieService;
import com.yumu.hexie.service.o2o.XiyiService;
import com.yumu.hexie.service.sales.BaseOrderService;
import com.yumu.hexie.service.sales.RgroupService;
import com.yumu.hexie.service.user.CouponService;
import com.yumu.hexie.service.user.impl.CouponServiceImpl;
import com.yumu.hexie.service.user.req.MemberVo;
import com.yumu.hexie.service.ScheduleService;
import com.yumu.hexie.model.op.ScheduleRecordRepository;
import com.yumu.hexie.model.op.ScheduleRecord;

@Service("scheduleService")
public class ScheduleServiceImpl implements ScheduleService {

	private static final Logger SCHEDULE_LOG = LoggerFactory.getLogger("com.yumu.hexie.schedule");
	@Inject
	private ServiceOrderRepository serviceOrderRepository;
	@Inject
	private PaymentOrderRepository paymentOrderRepository;
	@Inject
	private RefundOrderRepository refundOrderRepository;
	@Inject
	private WechatCoreService wechatCoreService;
    @Inject
    private RgroupService rgroupService;
    @Inject
    private BaseOrderService baseOrderService;
	@Inject
	private RgroupRuleRepository rgroupRuleRepository;
    @Inject
    private YunXiyiBillRepository yunXiyiBillRepository;
    @Inject
    private XiyiService xiyiService;
    @Inject
    private BaojieBillRepository baojieBillRepository;
    @Inject
    private BaojieService baojieService;
    @Inject
    private BizErrorRepository bizErrorRepository;
    @Inject 
    private ScheduleRecordRepository scheduleRecordRepository;
    @Inject
    private CouponService couponService;
    @Inject
    private UserRepository userRepository;
    @Inject
    private SmsService smsService;
	@Inject
	private MemberBillRepository memberBillRepository;
	@Inject
	private MemberRepository memberRepository;
	@Inject
	private CouponServiceImpl couponServiceImpl;
	@Autowired
    private OnSaleAreaItemRepository onSaleAreaItemRepository;
	@Autowired
	private OnSaleRuleRepository onSaleRuleRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private EvoucherRepository evoucherRepository;
	@Autowired
	private RgroupAreaItemRepository rgroupAreaItemRepository;
	@Autowired
	private RgroupOwnerRepository rgroupOwnerRepository;
	@Autowired
	@Qualifier("stringRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private PageViewRepository pageViewRepository;
	
	
//	1. 订单超时
    @Scheduled(cron = "50 1/3 * * * ?")
    @Override
    public void executeOrderTimeoutJob() {
    	executeOrderTimeoutJob(serviceOrderRepository.findTimeoutServiceOrder(System.currentTimeMillis()));
    }
	//2. 退款状态更新
//    @Scheduled(cron = "10 10/30 * * * ?")
    @Override
    public void executeRefundStatusJob() {
    	executeRefundStatusJob(refundOrderRepository.findAllApplyedRefund(System.currentTimeMillis()-1800*1000));
    }
	//3. 商品支付状态更新
//    @Scheduled(cron = "10 2/3 * * * ?")
    @Override
    public void executePayOrderStatusJob() {
        executeMarketPayOrderStatusJob(paymentOrderRepository.queryAllUnpayMarketOrderIds());
    }
    
    //4. 团购团超时
    @Scheduled(cron = "11 2/5 * * * ?")
    @Override
    public void executeRGroupTimeoutJob() {
    	SCHEDULE_LOG.error("--------------------executeGroupTimeoutJob[B][R]-------------------");
    	List<RgroupRule> rules = rgroupRuleRepository.findTimeoutGroup(new Date());
    	if(rules.size() == 0) {
    		SCHEDULE_LOG.error("**************executeRGroupTimeoutJob没有记录");
    		return;
    	}
    	String ids = "";
    	for(RgroupRule rule : rules) {
    		ids += rule.getId()+",";
    	}
    	ScheduleRecord sr = new ScheduleRecord(ModelConstant.SCHEDULE_TYPE_TUANGOU_TIMEOUT,ids);
    	sr = scheduleRecordRepository.save(sr);
    	
    	for(RgroupRule rule : rules) {
    		try{
    	    	SCHEDULE_LOG.error("refreshGroupStatus:" + rule.getId());
    	    	if (rule.getSectCount() > 0) {	//新版本，支持多个小区
    	    		rgroupService.refreshGroupStatus4MultiRegions(rule);
				} else {
					rgroupService.refreshGroupStatus(rule);	//老版本
				}
    	    	
    		} catch(Exception e){
    			SCHEDULE_LOG.error("超时订单更新失败"+ rule.getId(),e);
    			recordError(e);
    			sr.addErrorCount(""+rule.getId());
    		}
    	}
    	sr.setFinishDate(new Date());
    	scheduleRecordRepository.save(sr);
    	SCHEDULE_LOG.debug("--------------------executeGroupTimeoutJob[E][R]-------------------");
    }
    
    //14.团购发货完成修改状态
    @Scheduled(cron = "12 2/5 * * * ?")
    @Override
    public void executeRGroupDeliveryJob() {
    	SCHEDULE_LOG.error("--------------------executeRGroupDeliveryJob[B][R]-------------------");
    	List<RgroupRule> rules = rgroupRuleRepository.findByGroupStatus(ModelConstant.RGROUP_STAUS_DELIVERING);
    	if(rules.size() == 0) {
    		SCHEDULE_LOG.error("**************executeRGroupDeliveryJob没有记录");
    		return;
    	}
    	
    	for(RgroupRule rule : rules) {
    		try{
    	    	SCHEDULE_LOG.error("updateGroupDeliveryStatus:" + rule.getId());
    	    	rgroupService.refreshGroupDeliveryStatus(rule);
    		} catch(Exception e){
    			SCHEDULE_LOG.error("团购发货状态更新失败"+ rule.getId(),e);
    		}
    	}
    	SCHEDULE_LOG.debug("--------------------executeRGroupDeliveryJob[E][R]-------------------");
    }
    
  //4. 保洁超时  YunXiyiBillRepository
//    @Scheduled(cron = "50 1/2 * * * ?")
    @Override
    public void executeBaojieTimeoutJob() {
        SCHEDULE_LOG.debug("--------------------executeBaojieTimeoutJob[B][R]-------------------");
        List<BaojieBill> bills = baojieBillRepository.findTimeoutBill(System.currentTimeMillis() - 30000);
        if(bills.size() == 0) {
            SCHEDULE_LOG.error("**************executeBaojieTimeoutJob没有记录");
            return;
        }
        String ids = "";
        for(BaojieBill rule : bills) {
            ids += rule.getId()+",";
        }
        ScheduleRecord sr = new ScheduleRecord(ModelConstant.SCHEDULE_TYPE_BAOJIE_TIMEOUT,ids);
        sr = scheduleRecordRepository.save(sr);
        
        for(BaojieBill rule : bills) {
            try{
                SCHEDULE_LOG.debug("XIYIBILL TimeOUt:" + rule.getId());
                baojieService.timeout(rule.getId());
            } catch(Exception e){
                SCHEDULE_LOG.error("超时保洁订单更新失败"+ rule.getId(),e);
                recordError(e);
                sr.addErrorCount(""+rule.getId());
            }
        }
        sr.setFinishDate(new Date());
        scheduleRecordRepository.save(sr);
        SCHEDULE_LOG.debug("--------------------executeBaojieTimeoutJob[E][R]-------------------");
    }
    
    //4. 洗衣超时  YunXiyiBillRepository
//    @Scheduled(cron = "20 1/2 * * * ?")
    @Override
    public void executeXiyiTimeoutJob() {
        SCHEDULE_LOG.debug("--------------------executeXiyiTimeoutJob[B][R]-------------------");
        List<YunXiyiBill> bills = yunXiyiBillRepository.findTimeoutBill(System.currentTimeMillis() - 30000);
        if(bills.size() == 0) {
            SCHEDULE_LOG.error("**************executeXiyiTimeoutJob没有记录");
            return;
        }
        String ids = "";
        for(YunXiyiBill rule : bills) {
            ids += rule.getId()+",";
        }
        ScheduleRecord sr = new ScheduleRecord(ModelConstant.SCHEDULE_TYPE_XIYI_TIMEOUT,ids);
        sr = scheduleRecordRepository.save(sr);
        
        for(YunXiyiBill rule : bills) {
            try{
                SCHEDULE_LOG.debug("XIYIBILL TimeOUt:" + rule.getId());
                xiyiService.timeout(rule.getId());
            } catch(Exception e){
                SCHEDULE_LOG.error("超时洗衣订单更新失败"+ rule.getId(),e);
                recordError(e);
                sr.addErrorCount(""+rule.getId());
            }
        }
        sr.setFinishDate(new Date());
        scheduleRecordRepository.save(sr);
        SCHEDULE_LOG.debug("--------------------executeXiyiTimeoutJob[E][R]-------------------");
    }
    

	/************************************定时任务，由各业务自行调用 **************************/
	//3. 支付状态查询
    private void executeMarketPayOrderStatusJob(List<Long> orderIds) {
    	SCHEDULE_LOG.debug("--------------------executePayOrderStatusJob[B]-------------------");
    	if(orderIds.size() == 0) {
    		SCHEDULE_LOG.error("**************executePayOrderStatusJob没有记录");
    		return;
    	}
    	String ids = "";
    	for(Long id : orderIds) {
    		ids += id+",";
    	}
    	ScheduleRecord sr = new ScheduleRecord(ModelConstant.SCHEDULE_TYPE_PAY_STATUS,ids);
    	sr = scheduleRecordRepository.save(sr);
    	
    	
    	for(Long id : orderIds) {
	    	SCHEDULE_LOG.debug("PayOrderNotify:" + id);
    		try{
    			baseOrderService.notifyPayed(id);
    		} catch(Exception e){
    			SCHEDULE_LOG.error("支付状态同步失败"+ id,e);
    			recordError(e);
    			sr.addErrorCount(""+id);
    		}
    	}
    	sr.setFinishDate(new Date());
    	scheduleRecordRepository.save(sr);
    	SCHEDULE_LOG.debug("--------------------executePayOrderStatusJob[E]-------------------");
    }
	
    
	//1. 支付单超时(25分一次，30分钟超时)
    private void executeOrderTimeoutJob(List<ServiceOrder> serviceOrders) {
    	SCHEDULE_LOG.info("--------------------executeOrderTimeoutJob-------------------");
    	if(serviceOrders.size() == 0) {
    		SCHEDULE_LOG.info("**************executeOrderTimeoutJob没有记录");
    		return;
    	}
    	String ids = "";
    	for(ServiceOrder order : serviceOrders) {
    		if (ModelConstant.ORDER_TYPE_SERVICE == order.getOrderType()) {
				continue;
			}
    		ids += order.getId()+",";
    	}
    	ScheduleRecord sr = new ScheduleRecord(ModelConstant.SCHEDULE_TYPE_PAY_TIMEOUT,ids);
    	sr = scheduleRecordRepository.save(sr);
    	for(ServiceOrder order : serviceOrders) {
    		try{
    			if (ModelConstant.ORDER_TYPE_SERVICE == order.getOrderType()) {
    				continue;
    			}
    	    	SCHEDULE_LOG.info("CancelOrder:" + order.getId());
    	    	baseOrderService.cancelOrder(order);
    		} catch(Exception e){
    			SCHEDULE_LOG.info("超时支付单失败orderID"+ order.getId(),e);
    			recordError(e);
    			sr.addErrorCount(""+order.getId());
    		}
    	}
    	sr.setFinishDate(new Date());
    	scheduleRecordRepository.save(sr);
    	SCHEDULE_LOG.info("--------------------executeOrderTimeoutJob-------------------");
    }
	//2. 退款状态查询
    private void executeRefundStatusJob(List<RefundOrder> refundOrder) {
    	SCHEDULE_LOG.debug("--------------------executeRefundStatusJob-------------------");
    	if(refundOrder.size() == 0) {
    		SCHEDULE_LOG.error("**************executeRefundStatusJob没有记录");
    		return;
    	}
    	String ids = "";
    	for(RefundOrder order : refundOrder) {
    		ids += order.getId()+",";
    	}
    	ScheduleRecord sr = new ScheduleRecord(ModelConstant.SCHEDULE_TYPE_REFUND_STATUS,ids);
    	sr = scheduleRecordRepository.save(sr);
    	for(RefundOrder order : refundOrder) {
    		try{
    	    	SCHEDULE_LOG.debug("Refund:" + order.getId());
	    	    if(order.getOrderType() == PaymentConstant.TYPE_MARKET_ORDER){
	    	        baseOrderService.finishRefund(wechatCoreService.refundQuery(order.getPaymentNo()));
	            } else {
	                //xiyiService.update4Payment(payment);
	            }
    			
    		} catch(Exception e){
    			SCHEDULE_LOG.error("退款单更新失败orderID"+ order.getId(),e);
    			recordError(e);
    			sr.addErrorCount(""+order.getId());
    		}
    	}
    	sr.setFinishDate(new Date());
    	scheduleRecordRepository.save(sr);
    	SCHEDULE_LOG.debug("--------------------executeRefundStatusJob-------------------");
    }
    /************************************定时任务，由各业务自行调用 **************************/
    
    
    @Async
    private void recordError(Exception e) {
    	if(e instanceof BizValidateException){ 
    		if(((BizValidateException)e).getLevel() == ModelConstant.EXCEPTION_LEVEL_ERROR) {
		    	BizError be = new BizError(((BizValidateException)e));
		    	bizErrorRepository.save(be);
	    	}
    	} else {
    		BizError be = new BizError(e);
	    	bizErrorRepository.save(be);
    	}
	}
	@Override
    @Scheduled(cron = "15 */2 * * * ?")
	public void executeCouponTimeoutJob() {
		
		SCHEDULE_LOG.info("--------------------start executeCouponTimeoutJob-------------------");
		
		List<Coupon> coupons = couponService.findTop100TimeoutCoupon();
		for(Coupon coupon : coupons) {
			try{
				couponService.timeout(coupon);
			}catch(Exception e) {
				SCHEDULE_LOG.error("exec coupon timeout job error, couponId : " + coupon.getId(), e);
			}
		}
		SCHEDULE_LOG.info("--------------------end executeCouponTimeoutJob-------------------");
	}
	
	/**
	 * 每天下午18:00触发 
	 * 1.优惠券到期智能提醒，在券到期前2天，提示用户券到期
	 * 2.只提醒全场通用券和社区红包(TODO 暂未实现，所有红包都发)
	 * 3.不管多少券，每个用户每个月最多只能发两条短信以免造成骚扰
	 */
	@Override
//	@Scheduled(cron = "0 0 18 * * ? ")
	public void executeCouponTimeoutHintJob() {
		
		String msg = "亲爱的邻居，您有amount元的优惠券即将过期，赶紧去“合协社区”看看吧！";

		SCHEDULE_LOG.debug("--------------------start executeCouponHintJob-------------------");
		
		Date currDate = new Date();
		
		Calendar c = Calendar.getInstance();
		c.setTime(currDate);
		c.set(Calendar.DATE, c.get(Calendar.DATE)+2);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		Date fromDate = c.getTime();
		
		c = Calendar.getInstance();
		c.setTime(currDate);
		c.set(Calendar.DATE, c.get(Calendar.DATE)+2);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		Date toDate = c.getTime();
		
		List<Coupon> coupons = couponService.findTimeoutCouponByDate(fromDate, toDate);
		
		if (coupons.size()==0) {
			SCHEDULE_LOG.error("no coupon will be time out on" + currDate);
			return;
		}
		
		List<Long>userIdList = new ArrayList<Long>();
		
		for (int i = 0; i < coupons.size(); i++) {
			
			Coupon coupon = coupons.get(i);
			long userId = coupon.getUserId();
			
			//本次已经发送过短信的用户不再发送，以免短信黑名单
			if (userIdList.contains(userId)) {
				continue;
			}
			
			float couponAmt = coupon.getAmount();
			NumberFormat nf = new DecimalFormat("#");
			String displayAmt = nf.format(couponAmt);
			
			User user = userRepository.findById(userId);
			
			if (user == null) {
				SCHEDULE_LOG.debug(" user does not exist " + userId);
			}else {
				
				String mobile = user.getTel();
				
				//找出最近发送的到期提醒短信，如果一个月内已有2次，则不再做提醒。
				c = Calendar.getInstance();
				c.setTime(currDate);
				c.set(Calendar.MONTH, c.get(Calendar.MONTH)-1);
				Date lastDate = c.getTime();
				int sentCounts = smsService.getByPhoneAndMesssageTypeInOneMonth(mobile, 3, lastDate);
				if (sentCounts >= 2) {
					continue;
				}
				
				if (StringUtil.isEmpty(mobile)) {
					SCHEDULE_LOG.debug("user has no mobile , user_id :" + userId);
				}
				
				String sendMsg = msg.replace("amount", displayAmt);
				smsService.sendMsg(user, mobile, sendMsg, 12, 3);

				SCHEDULE_LOG.debug("msg sent, mobile :" + mobile + ", userId: " + userId + "msg : " + sendMsg);
				
				userIdList.add(userId);
			}
		}
		SCHEDULE_LOG.debug("--------------------end executeCouponHintJob-------------------");
	}
	
	
	
	@SuppressWarnings("rawtypes")
	@Override
//	@Scheduled(cron = "0 */20 * * * ?")
	public void executeMemberTimtout() {
		SCHEDULE_LOG.debug("--------------------会员支付定时开始：-------------------");
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
			//———————————————————————————————————————————————————会员是否到期轮询↓—————————————————————————————————————————————————————————————
			List<Member> listMb = memberRepository.getAllEndDate(df.format(new Date()).substring(0, 10));//查询结束日期已经到了的会员
			for (int i = 0; i < listMb.size(); i++) {
				Member member = listMb.get(i);
				member.setStatus(MemberVo.MEMBER_NO);//状态调整
				memberRepository.save(member);
			}
			
			//—————————————————————————————————————————————————————会员支付轮询↓—————————————————————————————————————————————————————————————
			List<MemberBill> listbill = memberBillRepository.findByStatus(MemberVo.MIDDLE);
			for (int i = 0; i < listbill.size(); i++) {
				MemberBill bill = listbill.get(i);
				User user = userRepository.findById(bill.getUserid());
				BaseResult baseResult = WuyeUtil.queryOrderInfo(user, String.valueOf(bill.getMemberbillid()));

				if("SUCCESS".equals(baseResult.getResult())) {
					bill.setEnddate(df.format(new Date()));
					bill.setStatus(MemberVo.SUCCESS);
					if(user == null) {
						throw new BizValidateException("账单userid没有查询到用户");
					}
					memberBillRepository.save(bill);//账单完成
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
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SCHEDULE_LOG.debug("--------------------会员支付定时结束：-------------------");
	}

	/**
	 * 特卖超时下架
	 */
	@Scheduled(cron = "0 */20 * * * ?")
	@Override
	public void executeOnsaleRuleTimeoutJob() {
		
		long current = System.currentTimeMillis()/1000;
		List<OnSaleRule> ruleList = onSaleRuleRepository.findTimeoutRules(current, ModelConstant.RULE_STATUS_ON);
		for (OnSaleRule onSaleRule : ruleList) {
			onSaleRuleRepository.updateStatus(ModelConstant.RULE_STATUS_OFF, onSaleRule.getId());
			List<OnSaleAreaItem> areaList = onSaleAreaItemRepository.findByRuleId(onSaleRule.getId());
			for (OnSaleAreaItem areaItem : areaList) {
				onSaleAreaItemRepository.updateStatus(ModelConstant.DISTRIBUTION_STATUS_OFF, areaItem.getId());
			}
			Product product = productRepository.findById(onSaleRule.getProductId());
			productRepository.updateStatus(ModelConstant.PRODUCT_OFF, product.getId());
		}
	
	}
	
	/**
	 * 团购商品超时下架
	 */
	@Scheduled(cron = "0 */30 * * * ?")
	@Override
	public void executeRgroupRuleTimeoutJob() {
		
		List<RgroupRule> ruleList = rgroupRuleRepository.findTimeoutGroup(new Date());
		for (RgroupRule rGroupRule : ruleList) {
			rgroupRuleRepository.updateStatus(ModelConstant.RULE_STATUS_END, rGroupRule.getId());
			List<RgroupAreaItem> areaList = rgroupAreaItemRepository.findByRuleId(rGroupRule.getId());
			for (RgroupAreaItem areaItem : areaList) {
				rgroupAreaItemRepository.updateStatus(ModelConstant.DISTRIBUTION_STATUS_OFF, areaItem.getId());
			}
//			Product product = productRepository.findById(rGroupRule.getProductId());
//			productRepository.updateStatus(ModelConstant.PRODUCT_OFF, product.getId());
		}
	
	}
	
	
	/**
	 * 特卖超时下架
	 */
	@Scheduled(cron = "0 */20 * * * ?")
	@Override
	public void executeEvoucherTimeoutJob() {
		
		long current = System.currentTimeMillis()/1000;
		List<Evoucher> list = evoucherRepository.findTimeoutEvouchers(current, ModelConstant.EVOUCHER_STATUS_NORMAL);
		for (Evoucher evoucher : list) {
			evoucher.setStatus(ModelConstant.EVOUCHER_STATUS_EXPIRED);
			evoucherRepository.save(evoucher);
		}
	
	}
	
	@Scheduled(cron = "0 */5 * * * ?")
	@Override
	public void initStockAndFreeze() {
		
		List<Product> proList = productRepository.findByStatusMultiType(ModelConstant.PRODUCT_ONSALE);
		for (Product product : proList) {
			String total = redisTemplate.opsForValue().get(ModelConstant.KEY_PRO_STOCK + product.getId());
			if (StringUtils.isEmpty(total)) {
				redisTemplate.opsForValue().setIfAbsent(ModelConstant.KEY_PRO_STOCK + product.getId(), String.valueOf(product.getTotalCount()));
				redisTemplate.opsForValue().setIfAbsent(ModelConstant.KEY_PRO_FREEZE + product.getId(), "0");
			}
		}
		SCHEDULE_LOG.info("refresh stock and freeze finished .");
	}
	
	/**
	 * 固化团长数据
	 */
	@Scheduled(cron = "10 */15 * * * ?")
	@Override
	@Transactional
	public void updateGroupOwnerInfo() {
		
		SCHEDULE_LOG.info("start to update groupOwner info .");
		
		List<RgroupOwner> ownerList = rgroupOwnerRepository.findAll();
		for (RgroupOwner rgroupOwner : ownerList) {
			String ownerAccessed = redisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_OWNER_ACCESSED + rgroupOwner.getUserId());
			String ownerOrdered = redisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_OWNER_ORDERED + rgroupOwner.getUserId());
			
			int members = 0;
			if (!StringUtils.isEmpty(ownerAccessed)) {
				members = Integer.parseInt(ownerAccessed);
			}
			int attendees = 0;
			if (!StringUtils.isEmpty(ownerOrdered)) {
				attendees = Integer.parseInt(ownerOrdered);
			}
			if (members > 0 || attendees > 0) {
				rgroupOwner.setMembers(members);
				rgroupOwner.setAttendees(attendees);
				rgroupOwnerRepository.save(rgroupOwner);
			}
			
		}
		SCHEDULE_LOG.info("finish updating groupOwner info .");
	}
	
//	@Scheduled(cron = "32 0 0 * * ?")
	@Scheduled(cron = "24 */5 * * * ?")
	@Override
	@Transactional
	public void updatePageView() {
		
		SCHEDULE_LOG.info("start to update pageView count .");
		String currDate = DateUtil.dtFormat(new Date(), "yyyyMMdd");
		String lateDate = DateUtil.getNextDateByNum(currDate, -1);
		String appid = "wx47743d3c9b0c241c";	//TODO 先写死西部的小程序appid，因为当前只需要统计这一个
		String cacheKey = ModelConstant.KEY_PAGE_VIEW_COUNT + appid + ":" + lateDate;
		Map<Object, Object> pageMap = redisTemplate.opsForHash().entries(cacheKey);
		Iterator<Map.Entry<Object, Object>> it = pageMap.entrySet().iterator();
		
		List<PageView> pageViewList = new ArrayList<>();
		while(it.hasNext()) {
			Map.Entry<Object, Object> entry = it.next();
			String page = (String) entry.getKey();
			String countStr = (String) entry.getValue();
			Integer count = 0;
			try {
				count = Integer.valueOf(countStr);
			} catch (Exception e) {
				SCHEDULE_LOG.error(e.getMessage(), e);
			}
			
			PageView pageView = new PageView();
			pageView.setAppid(appid);
			pageView.setCount(count);
			pageView.setPage(page);
			pageView.setCountDate(lateDate);
			pageViewList.add(pageView);
		}
		pageViewRepository.saveAll(pageViewList);
		redisTemplate.expire(cacheKey, 7, TimeUnit.DAYS);	//缓存数据7天后获取
		SCHEDULE_LOG.info("finish updating pageView count .");
	}
	
}

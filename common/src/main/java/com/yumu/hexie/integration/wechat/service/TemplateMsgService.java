package com.yumu.hexie.integration.wechat.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yumu.hexie.integration.wechat.entity.templatemsg.*;
import com.yumu.hexie.service.shequ.vo.InteractCommentNotice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.common.util.AppUtil;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.notify.InvoiceNotification;
import com.yumu.hexie.integration.notify.Operator;
import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;
import com.yumu.hexie.integration.notify.ReceiptNotification;
import com.yumu.hexie.integration.notify.WorkOrderNotification;
import com.yumu.hexie.integration.wechat.entity.common.WechatResponse;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.event.dto.BaseEventDTO;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.oldversion.thirdpartyorder.HaoJiaAnComment;
import com.yumu.hexie.model.localservice.oldversion.thirdpartyorder.HaoJiaAnOrder;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.msgtemplate.MsgTemplate;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.billpush.vo.BillPushDetail;
import com.yumu.hexie.service.msgtemplate.WechatMsgService;
import com.yumu.hexie.service.sales.req.NoticeRgroupSuccess;
import com.yumu.hexie.vo.RgroupVO;

@Component
public class TemplateMsgService {
	
	private static final Logger log = LoggerFactory.getLogger(TemplateMsgService.class);

	@Autowired
	private RestUtil restUtil;
	@Autowired
	private WechatMsgService wechatMsgService;
	@Value("${wechat.miniprogramAppId}")
    private String miniprogramAppid;
	
	/**
	 * 模板消息发送
	 */
	private WechatResponse sendMsg(TemplateMsg<?> msg, String accessToken) {
        
		String requestUrl = MsgCfg.TEMPLATE_MSG;
		if(StringUtil.isNotEmpty(accessToken)){
	        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
	    }
		TypeReference<WechatResponse> typeReference = new TypeReference<WechatResponse>() {};
		WechatResponse wechatResponse = new WechatResponse();
		try {
			wechatResponse = restUtil.exchangeOnBody(requestUrl, msg, typeReference);
		} catch (Exception e) {
			log.error("发送模板消息失败: " +e.getMessage());
			log.error(e.getMessage(), e);
		}
		return wechatResponse;
	}

    /**
     * 订单支付成功消息
     * @param order
     * @param accessToken
     * @param appId
     */
	public void sendPaySuccessMsg(ServiceOrder order, String accessToken, String appId) {
		
		log.error("发送模板消息！！！！！！！！！！！！！！！" + order.getOrderNo());
		PaySuccessVO vo = new PaySuccessVO();
		vo.setFirst(new TemplateItem("您的订单：("+order.getOrderNo()+")已支付成功"));

		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		String price = decimalFormat.format(order.getPrice());
		vo.setOrderMoneySum(new TemplateItem(price+"元"));
		vo.setOrderProductName(new TemplateItem(order.getProductName()));
		if(StringUtils.isEmpty(order.getSeedStr())) {
			//vo.setRemark(new TemplateItem("我们已收到您的货款，开始为您打包商品，请耐心等待: )"));
		} else {
			vo.setRemark(new TemplateItem("恭喜您得到超值现金券一枚，查看详情并分享链接即可领取。"));
		}
		TemplateMsg<PaySuccessVO> msg = new TemplateMsg<>();
		msg.setData(vo);
		
		msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_PAY_SUCCESS, appId));
		String msgUrl = wechatMsgService.getMsgUrl(MsgCfg.URL_SUCCESS);
		String url = msgUrl.replace("ORDER_ID", ""+order.getId()).replace("ORDER_TYPE", ""+order.getOrderType());
		url = AppUtil.addAppOnUrl(url, appId);
		msg.setUrl(url);
		msg.setTouser(order.getOpenId());
		sendMsg(msg, accessToken);
	}
	
	/**
	 * 发送注册成功后的模版消息
	 * @param user
	 */
	public void sendRegisterSuccessMsg(User user, String accessToken){
		
		log.error("用户注册成功，发送模版消息："+user.getId()+",openid: " + user.getOpenid());
		
		RegisterSuccessVO vo = new RegisterSuccessVO();
		vo.setFirst(new TemplateItem("您好，您已注册成功"));
		vo.setUserName(new TemplateItem(user.getRealName()));
		Date currDate = new Date();
		String registerDateTime = DateUtil.dttmFormat(currDate);
		vo.setRegisterDateTime(new TemplateItem(registerDateTime));
		vo.setRemark(new TemplateItem("点击详情查看。"));
		
		TemplateMsg<RegisterSuccessVO>msg = new TemplateMsg<>();
		msg.setData(vo);
		msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_REG_SUCCESS, user.getAppId()));
		
		String msgUrl = wechatMsgService.getMsgUrl(MsgCfg.URL_REG_SUCCESS);
		String url = AppUtil.addAppOnUrl(msgUrl, user.getAppId());
		msg.setUrl(url);
		msg.setTouser(user.getOpenid());
		sendMsg(msg, accessToken);
	
	}
	
	/**
	 * 发送注册成功后的模版消息
	 * @param user
	 */
	public void sendWuYePaySuccessMsg(User user, String tradeWaterId, String feePrice, String accessToken){
		
		log.error("用户支付物业费成功，发送模版消息："+user.getId()+",openid: " + user.getOpenid());
		
		WuyePaySuccessVO vo = new WuyePaySuccessVO();
		vo.setFirst(new TemplateItem("物业费缴费成功，缴费信息如下:"));
		vo.setTrade_water_id(new TemplateItem(tradeWaterId));
		vo.setReal_name(new TemplateItem(user.getRealName()));
		vo.setFee_price(new TemplateItem(new BigDecimal(feePrice).setScale(2).toString()));
		vo.setFee_type(new TemplateItem("物业费"));
		
		Date currDate = new Date();
		String payDateTime = DateUtil.dttmFormat(currDate);
		vo.setPay_time((new TemplateItem(payDateTime)));
		vo.setRemark(new TemplateItem("点击详情查看"));
		
		TemplateMsg<WuyePaySuccessVO>msg = new TemplateMsg<>();
		msg.setData(vo);
		msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_WUYEPAY_SUCCESS, user.getAppId()));
		String msgUrl = wechatMsgService.getMsgUrl(MsgCfg.URL_REG_SUCCESS);
		String url = AppUtil.addAppOnUrl(msgUrl, user.getAppId());
		msg.setUrl(url);
		msg.setTouser(user.getOpenid());
		sendMsg(msg, accessToken);
	
	}

	/**
	 * 发送维修单信息给维修工
	 * @param ro
	 * @param op
	 * @param accessToken
	 * @param appId
	 */
    public void sendRepairAssignMsg(RepairOrder ro, ServiceOperator op, String accessToken, String appId) {
    	
    	log.info("发送维修单分配模版消息#########" + ", order id: " + ro.getId() + "operator id : " + op.getId());

    	//更改为使用模版消息发送
    	RepairOrderVO vo = new RepairOrderVO();
    	vo.setTitle(new TemplateItem(op.getName()+"，您有新的维修单！"));
    	vo.setOrderNum(new TemplateItem(ro.getOrderNo()));
    	vo.setCustName(new TemplateItem(ro.getReceiverName()));
    	vo.setCustMobile(new TemplateItem(ro.getTel()));
    	vo.setCustAddr(new TemplateItem(ro.getAddress()));
    	vo.setRemark(new TemplateItem("有新的维修单"+ro.getXiaoquName()+"快来抢单吧"));
  
    	TemplateMsg<RepairOrderVO>msg = new TemplateMsg<>();
    	msg.setData(vo);
    	msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_REPAIR_ASSIGN,appId));
    	String msgUrl = wechatMsgService.getMsgUrl(MsgCfg.URL_WEIXIU_NOTICE);
    	String url = msgUrl + ro.getId();
    	msg.setUrl(AppUtil.addAppOnUrl(url, appId));
    	msg.setTouser(op.getOpenId());
    	sendMsg(msg, accessToken);
    	
    }
    
    /**
     * 预约服务模板
     * @param openId
     * @param title
     * @param billName
     * @param requireTime
     * @param url
     * @param accessToken
     * @param appId
     */
    public void sendYuyueBillMsg(String orderId, String openId, String title,String billName, 
    			String requireTime, String url, String accessToken, String remark, String appId) {

        //更改为使用模版消息发送
        YuyueOrderVO vo = new YuyueOrderVO();
        vo.setTitle(new TemplateItem(title));
        vo.setProjectName(new TemplateItem(billName));
        vo.setRequireTime(new TemplateItem(requireTime));
        if (StringUtils.isEmpty(remark)) {
        	vo.setRemark(new TemplateItem("请尽快处理！"));
		}else {
			vo.setRemark(new TemplateItem(remark));
		}
        TemplateMsg<YuyueOrderVO>msg = new TemplateMsg<>();
        msg.setData(vo);
        msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_YUYUE_ASSGIN, appId));
        if (StringUtils.isEmpty(url)) {
			url = wechatMsgService.getMsgUrl(MsgCfg.URL_SERVICE_RESV) + orderId;
		}
        url = AppUtil.addAppOnUrl(url, appId);
        msg.setUrl(url);
        msg.setTouser(openId);
        sendMsg(msg, accessToken);
        
    }
    
    /**
     * 好家安预约服务派单
     * @param hOrder
     * @param user
     * @param accessToken
     * @param openId
     */
    public void sendHaoJiaAnAssignMsg(HaoJiaAnOrder hOrder, User user, String accessToken,String openId) {
    	
    	HaoJiaAnOrderVO vo = new HaoJiaAnOrderVO();
    	vo.setTitle(new TemplateItem("有新的预约服务"));
    	vo.setAppointmentDate(new TemplateItem(hOrder.getExpectedTime()));
    	vo.setAppointmentContent(new TemplateItem(hOrder.getServiceTypeName()));
    	vo.setAddress(new TemplateItem("预约地址：" + hOrder.getStrWorkAddr()+" "+hOrder.getStrName()+" "+(hOrder.getStrMobile()==null?"":hOrder.getStrMobile()+"\r\n"
    			+"备注:"+(hOrder.getMemo()==null?"":hOrder.getMemo()))));
    	log.error("预约服务的userId="+user.getId()+"");
    	log.error("预约服务的user="+user+""); 	
    	
    	TemplateMsg<HaoJiaAnOrderVO> msg = new TemplateMsg<>();
    	msg.setData(vo);
    	msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_YUYUE_ASSGIN, user.getAppId()));
    	String url = wechatMsgService.getMsgUrl(MsgCfg.URL_YUYUE_NOTICE) + hOrder.getyOrderId();
    	url = AppUtil.addAppOnUrl(url, user.getAppId());
    	msg.setUrl(url);
    	msg.setTouser(openId);
    	sendMsg(msg, accessToken);
    }
    
    
    /**
     * 投诉模板，发送给商家
     * @param comment
     * @param user
     * @param accessToken
     * @param openId
     */
    public void sendHaoJiaAnCommentMsg(HaoJiaAnComment comment, User user, String accessToken,String openId) {
    	
    	log.error("sendHaoJiaAnCommentMsg的用户电话="+comment.getCommentUserTel());
    	HaoJiaAnCommentVO vo = new HaoJiaAnCommentVO();
    	vo.setTitle(new TemplateItem("用户投诉"));//标题
    	vo.setUserName(new TemplateItem(comment.getCommentUserName()));//用户姓名
    	vo.setUserTel(new TemplateItem(comment.getCommentUserTel()));//用户电话
    	vo.setReason(new TemplateItem(comment.getCommentContent()));//投诉事由
    	vo.setOrderNo(new TemplateItem(comment.getYuyueOrderNo()));//订单编号
    	vo.setMemo(new TemplateItem("用户对您的服务有投诉，请尽快联系用户处理。"));//备注（固定内容）
    	log.error("投诉的userId="+user.getId()+"");
    	log.error("投诉的user="+user+""); 
    	TemplateMsg<HaoJiaAnCommentVO> msg = new TemplateMsg<>();
    	msg.setData(vo);
    	msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_COMPLAIN, user.getAppId()));
    	String url = wechatMsgService.getMsgUrl(MsgCfg.URL_COMPLAIN_DETAIL) + comment.getId();
    	url = AppUtil.addAppOnUrl(url, user.getAppId());
    	msg.setUrl(url);
    	msg.setTouser(openId);
    	sendMsg(msg, accessToken);
    }
    
    /**
     * 快递外卖
     * @param openid
     * @param accessToken
     * @param appId
     */
    public void sendExpressDelivery(String openid, String accessToken, String appId,long userId,String type) {
    	
    	WuyeServiceVO vo = new WuyeServiceVO();
    	if("0".equals(type)) {
    		vo.setTitle(new TemplateItem("您的快递已送达！"));
    	  	vo.setOrderNum(new TemplateItem(String.valueOf(System.currentTimeMillis())));
    	  	String recvDate = DateUtil.dtFormat(new Date(), "yyyy-MM-dd HH:mm:ss");
    	  	vo.setRecvDate(new TemplateItem(recvDate));
    	  	vo.setRemark(new TemplateItem("请及时到物业领取。"));
    	}else {
    		vo.setTitle(new TemplateItem("您的外卖已送达！"));
    	  	vo.setOrderNum(new TemplateItem(String.valueOf(System.currentTimeMillis())));
    	  	String recvDate = DateUtil.dtFormat(new Date(), "yyyy-MM-dd HH:mm:ss");
    	  	vo.setRecvDate(new TemplateItem(recvDate));
    	  	vo.setRemark(new TemplateItem("请及时到物业领取。"));
    	}
	  	
	  	TemplateMsg<WuyeServiceVO>msg = new TemplateMsg<>();
    	msg.setData(vo);
    	msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_SERVICE, appId));
    	String url = wechatMsgService.getMsgUrl(MsgCfg.URL_EXPRESS) + userId;
    	msg.setUrl(AppUtil.addAppOnUrl(url, appId));
    	msg.setTouser(openid);
    	sendMsg(msg, accessToken);
  	
	}
    
    /**
     * 平台群发通知
     * @param openid
     * @param accessToken
     * @param appId
     */
    public WechatResponse sendHexieMessage(String openid, String accessToken, String appId,long messageId,String content) {
    	
    	WuyeServiceVO vo = new WuyeServiceVO();
		vo.setTitle(new TemplateItem("物业通知"));

		if(!StringUtils.isEmpty(content)) {
			if(content.length() > 60) {
				content = content.substring(0, 50);
				content += "...";
			}
		}

	  	vo.setOrderNum(new TemplateItem(content));
	  	String recvDate = DateUtil.dtFormat(new Date(), "yyyy-MM-dd HH:mm:ss");
	  	vo.setRecvDate(new TemplateItem(recvDate));
	  	vo.setRemark(new TemplateItem("请点击查看"));
	  	
	  	TemplateMsg<WuyeServiceVO>msg = new TemplateMsg<>();
    	msg.setData(vo);
    	msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_MESSAGE, appId));
    	String url = wechatMsgService.getMsgUrl(MsgCfg.URL_MESSAGE) + messageId;
    	msg.setUrl(AppUtil.addAppOnUrl(url, appId));
    	msg.setTouser(openid);
    	return sendMsg(msg, accessToken);

	}

	/**
	 * 支付到账通知
	 * @param accountNotification
	 * @param accessToken
	 */
	public void sendPayNotification(AccountNotification accountNotification, String accessToken) {
    	
    	PayNotifyMsgVO vo = new PayNotifyMsgVO();
		vo.setTitle(new TemplateItem("您好，您有一笔订单收款成功。此信息仅供参考，请最终以商户端实际到账结果为准。"));
	  	vo.setTranAmt(new TemplateItem(accountNotification.getFeePrice().toString()));
	  	vo.setPayMethod(new TemplateItem(accountNotification.getPayMethod()));
	  	vo.setTranDateTime(new TemplateItem(accountNotification.getTranDate()));
	  	vo.setTranType(new TemplateItem(accountNotification.getFeeName()));
	  	vo.setRemark(new TemplateItem(accountNotification.getRemark()));
    	
	  	TemplateMsg<PayNotifyMsgVO>msg = new TemplateMsg<>();
    	msg.setData(vo);
    	msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_PAY_NOTIFY, accountNotification.getUser().getAppId()));
//    	String url = getMsgUrl(MsgCfg.URL_PAY_NOTIFY);
//    	msg.setUrl(AppUtil.addAppOnUrl(url, accountNotification.getUser().getAppId()));
    	msg.setTouser(accountNotification.getUser().getOpenid());
    	sendMsg(msg, accessToken);

	}

	/**
	 * 预约服务模板
	 * @param sendUser
	 * @param serviceOrder
	 * @param accessToken
	 */
    public void sendServiceNotification(User sendUser, ServiceOrder serviceOrder, String accessToken) {

        //更改为使用模版消息发送
		CsOrderVO vo = new CsOrderVO();
    	String title = "您有一个新的服务订单，请及时处理。";
    	vo.setTitle(new TemplateItem(title));
    	vo.setOrderId(new TemplateItem(String.valueOf(serviceOrder.getId())));
    	vo.setServiceType(new TemplateItem(serviceOrder.getSubTypeName()));
    	String customerName = serviceOrder.getReceiverName();
    	vo.setCustomerName(new TemplateItem(customerName));
    	vo.setCustomerTel(new TemplateItem(serviceOrder.getTel()));
    	vo.setRemark(new TemplateItem(serviceOrder.getAddress()));
    	
        TemplateMsg<CsOrderVO>msg = new TemplateMsg<>();
        msg.setData(vo);
        msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_CUSTOM_SERVICE_ASSGIN, sendUser.getAppId()));
        String url = wechatMsgService.getMsgUrl(MsgCfg.URL_CUSTOM_SERVICE_ASSIGN);
        if (!StringUtils.isEmpty(url)) {
			url = url + serviceOrder.getId();
			url = AppUtil.addAppOnUrl(url, sendUser.getAppId());
		}
        msg.setUrl(url);
        msg.setTouser(sendUser.getOpenid());
        sendMsg(msg, accessToken);
        
    }

	/**
	 * 发货提醒
	 * @param sendUser
	 * @param serviceOrder
	 * @param accessToken
	 */
	public void sendDeliveryNotification(User sendUser, ServiceOrder serviceOrder, String accessToken) {

		String title = "您有一个新的订单，请及时处理。";
    	String orderDate = DateUtil.dtFormat(new Date(serviceOrder.getCreateDate()));
    	String customerName = serviceOrder.getReceiverName();

    	CommonVO vo = new CommonVO();
    	vo.setFirst(new TemplateItem(title));
    	vo.setKeyword1(new TemplateItem(String.valueOf(serviceOrder.getId())));
    	vo.setKeyword2(new TemplateItem(orderDate));	//下单时间
    	vo.setKeyword3(new TemplateItem(customerName));
    	vo.setKeyword4(new TemplateItem(serviceOrder.getAddress()));
    	String remark = "请及时发货哦～";
    	vo.setRemark(new TemplateItem(remark));
    	
        TemplateMsg<CommonVO>msg = new TemplateMsg<>();
        msg.setData(vo);
        msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_DELIVERY_MESSAGE, sendUser.getAppId()));
        String url = wechatMsgService.getMsgUrl(MsgCfg.URL_DELIVERY_DETAIL);
        if (!StringUtils.isEmpty(url)) {
			url = url + serviceOrder.getId();
			url = AppUtil.addAppOnUrl(url, sendUser.getAppId());
		}
        msg.setUrl(url);
        msg.setTouser(sendUser.getOpenid());
        sendMsg(msg, accessToken);
        
    }
    
    /**
     * 重置密码
     * @param user
     * @param password
     * @param accessToken
     */
    public void sendResetPasswordMsg(User user, String password, String accessToken) {
    	
    	ResetPasswordVO vo = new ResetPasswordVO();
    	String title = "您好，您的密码已经被重置。";
    	vo.setTitle(new TemplateItem(title));
    	vo.setUserName(new TemplateItem(user.getTel()));
    	vo.setPassword(new TemplateItem(password));
    	Date date = new Date();
    	String resetTime = DateUtil.dtFormat(date, DateUtil.dttmSimple);
    	vo.setResetTime(new TemplateItem(resetTime));
    	vo.setRemark(new TemplateItem("请用PC浏览器访问b.e-shequ.cn，进入您的运营系统"));
    	
    	TemplateMsg<ResetPasswordVO>msg = new TemplateMsg<>();
        msg.setData(vo);
        msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_RESET_PASSWORD, user.getAppId()));
        msg.setTouser(user.getOpenid());
        sendMsg(msg, accessToken);
    	
    }

	/**
	 * 用户订单发货提醒
	 * @param sendUser
	 * @param serviceOrder
	 * @param accessToken
	 */
	public void sendCustomerDeliveryMessage(User sendUser, ServiceOrder serviceOrder, String accessToken) {

		String title = "您购买的订单已经发货啦，正快马加鞭向您飞奔而去。";

    	CommonVO vo = new CommonVO();
    	vo.setFirst(new TemplateItem(title));
    	String orderNo = serviceOrder.getOrderNo();
    	if (StringUtils.isEmpty(orderNo)) {
    		orderNo = String.valueOf(serviceOrder.getId());
		}
    	vo.setKeyword1(new TemplateItem(orderNo));	//订单号
    	vo.setKeyword2(new TemplateItem(serviceOrder.getLogisticName()));	//物流公司名称
    	vo.setKeyword3(new TemplateItem(serviceOrder.getLogisticNo()));	//快递单好
//    	String remark = "";
//    	vo.setRemark(new TemplateItem(remark));
    	
        TemplateMsg<CommonVO>msg = new TemplateMsg<>();
        msg.setData(vo);
        msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_CUSTOMER_DELIVERY, sendUser.getAppId()));
        String url = wechatMsgService.getMsgUrl(MsgCfg.URL_CUSTOMER_DELIVERY);
        if (!StringUtils.isEmpty(url)) {
			url = url + serviceOrder.getId();
			url = AppUtil.addAppOnUrl(url, sendUser.getAppId());
		}
        msg.setUrl(url);
        msg.setTouser(sendUser.getOpenid());
        sendMsg(msg, accessToken);
        
    }

	/**
	 * 支付到账通知(发送给房屋绑定者)
	 * @param accountNotification
	 * @param accessToken
	 */
	public void sendPayNotification4HouseBinder(AccountNotification accountNotification, String accessToken) {
    	
    	CommonVO2 vo = new CommonVO2();
		vo.setFirst(new TemplateItem("您好，您已付款成功。"));
	  	vo.setKeyword1(new TemplateItem(accountNotification.getTranDate()));
	  	vo.setKeyword2(new TemplateItem(accountNotification.getMchName()));
	  	vo.setKeyword3(new TemplateItem(accountNotification.getFeePrice().toString()));
	  	vo.setKeyword4(new TemplateItem(accountNotification.getOrderId()));
	  	vo.setKeyword5(new TemplateItem(accountNotification.getPayMethod()));
	  	vo.setRemark(new TemplateItem("点击查看详情"));
    	
	  	TemplateMsg<CommonVO2>msg = new TemplateMsg<>();
    	msg.setData(vo);
    	msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_PAY_HOUSE_BIND_NOTIFY, accountNotification.getUser().getAppId()));
    	String url = wechatMsgService.getMsgUrl(MsgCfg.URL_PAY_HOUSE_BINDER_NOTIFY);
    	url = AppUtil.addAppOnUrl(url, accountNotification.getUser().getAppId());
    	url = url.replaceAll("TRADE_WATER_ID", accountNotification.getOrderId()).replaceAll("SYS_SOURCE", accountNotification.getSysSource());
    	msg.setUrl(url);
    	msg.setTouser(accountNotification.getUser().getOpenid());
    	sendMsg(msg, accessToken);

	}

	/**
	 * 欠费账单推送
	 * @param openid
	 * @param accessToken
	 * @param appId
	 * @param billPushDetail
	 * @return
	 */
	public String sendBillNotificationMessage(String openid, String accessToken, String appId, BillPushDetail billPushDetail) {

		MsgTemplate msgTemplate = wechatMsgService.getTemplateByNameAndAppIdV2(MsgCfg.TEMPLATE_TYPE_BILL_PUSH, appId);
		if (msgTemplate == null) {
			msgTemplate = wechatMsgService.getTemplateByNameAndAppIdV2(MsgCfg.TEMPLATE_TYPE_BILL_PUSH2, appId);
		}
		WechatResponse wechatResponse = null;
		if (msgTemplate == null) {
			wechatResponse = new WechatResponse();
			wechatResponse.setErrcode(99999);
			wechatResponse.setErrmsg("99999:[欠费账单推送]未配置模板消息");
		} else {
			int templateType = msgTemplate.getType();
			String url = wechatMsgService.getMsgUrl(MsgCfg.URL_WUYE_PAY);
			if (!StringUtils.isEmpty(url)) {
				url = AppUtil.addAppOnUrl(url, appId);
			}
			if (templateType == 1) {
				CommonVO vo = new CommonVO();
				vo.setFirst(new TemplateItem(billPushDetail.getShowFirstMsg()));
				vo.setKeyword1(new TemplateItem(billPushDetail.getSectName())); //小区名称
				vo.setKeyword2(new TemplateItem(billPushDetail.getCellAddr()));	//房屋地址
				vo.setKeyword3(new TemplateItem(billPushDetail.getPeriod())); //账期
				vo.setKeyword4(new TemplateItem(billPushDetail.getFeePrice())); //应缴金额
				vo.setRemark(new TemplateItem(billPushDetail.getRemark())); //备注

				TemplateMsg<CommonVO> msg = new TemplateMsg<>();
				msg.setData(vo);
				msg.setTemplate_id(msgTemplate.getValue());
				msg.setUrl(url);
				msg.setTouser(openid);
				wechatResponse = sendMsg(msg, accessToken);
				
			} else if (templateType == 2) {
				Map<String, Map<String, String>> map = new HashMap<>();
				Map<String, String> dataMap = new HashMap<>();
				dataMap.put("value", billPushDetail.getSectName());
				map.put("thing13", dataMap);
				
				dataMap = new HashMap<>();
				dataMap.put("value", billPushDetail.getCellAddr());
				map.put("thing3", dataMap);
				
				dataMap = new HashMap<>();
				dataMap.put("value", billPushDetail.getPeriod());
				map.put("time7", dataMap);
				
				dataMap = new HashMap<>();
				dataMap.put("value", billPushDetail.getFeePrice());
				map.put("amount4", dataMap);
				
				dataMap = new HashMap<>();
				dataMap.put("value", "物业费/租金/停车费");
				map.put("thing9", dataMap);
				
				TemplateMsg<Map<String, Map<String, String>>> msg = new TemplateMsg<>();
				msg.setData(map);
				msg.setUrl(url);
				msg.setTemplate_id(msgTemplate.getValue());
				msg.setTouser(openid);
				wechatResponse = sendMsg(msg, accessToken);
			}
		}
		
		String stat = "success";
		int code = wechatResponse.getErrcode();
		if(code != 0) {
			if(code == 43004) {
				stat =  "用户未订阅";
			}else if(code == 40036 || code == 40037) {
				stat =  "模板ID不合法";
			} else {
				stat =  code + ":" + wechatResponse.getErrmsg();
			}
		}
		return stat;
	}

	/**
	 * 业主意见回复
	 * @param commentNotice
	 * @param accessToken
	 */
	public WechatResponse sendOpinionNotificationMessage(InteractCommentNotice commentNotice, String accessToken) {
		
		
		WechatResponse wechatResponse = null;
		if (StringUtils.isEmpty(commentNotice.getAppid()) || StringUtils.isEmpty(commentNotice.getInteractId())) {
			wechatResponse = new WechatResponse();
			wechatResponse.setErrcode(99998);
			wechatResponse.setErrmsg("appid或interactId不能为空");
			return wechatResponse;
		}
		
		MsgTemplate msgTemplate = wechatMsgService.getTemplateByNameAndAppIdV2(MsgCfg.TEMPLATE_TYPE_OPINION_NOTIFY, commentNotice.getAppid());
		if (msgTemplate == null) {
			msgTemplate = wechatMsgService.getTemplateByNameAndAppIdV2(MsgCfg.TEMPLATE_TYPE_OPINION_NOTIFY2, commentNotice.getAppid());
		}
		if (msgTemplate == null) {
			wechatResponse = new WechatResponse();
			wechatResponse.setErrcode(99999);
			wechatResponse.setErrmsg("99999:[业主意见回复]未配置模板消息");
		} else {
			String url = wechatMsgService.getMsgUrl(MsgCfg.URL_OPINION_NOTICE);
			url = AppUtil.addAppOnUrl(url, commentNotice.getAppid());
			url = url.replaceAll("THREAD_ID", commentNotice.getInteractId());
			int msgType = msgTemplate.getType();
			if (msgType == 1) {
				CommonVO2 vo = new CommonVO2();
				vo.setFirst(new TemplateItem("您好，你的意见建议已有反馈。"));
				vo.setKeyword1(new TemplateItem(commentNotice.getContent()));
				vo.setKeyword2(new TemplateItem(commentNotice.getOpinionDate()));
				vo.setKeyword3(new TemplateItem(commentNotice.getCommentContent()));
				vo.setKeyword4(new TemplateItem(commentNotice.getCommentName()));
				vo.setRemark(new TemplateItem("谢谢您宝贵的意见和建议"));

				TemplateMsg<CommonVO2> msg = new TemplateMsg<>();
				msg.setData(vo);
				msg.setTemplate_id(msgTemplate.getValue());
				msg.setUrl(url);
				msg.setTouser(commentNotice.getOpenid());
				wechatResponse = sendMsg(msg, accessToken);
			} else if (msgType == 2) {
				
				Map<String, Map<String, String>> map = new HashMap<>();
				Map<String, String> dataMap = new HashMap<>();
				dataMap.put("value", commentNotice.getContent());
				map.put("thing3", dataMap);
				
				dataMap = new HashMap<>();
				dataMap.put("value", commentNotice.getOpinionDate());
				map.put("time4", dataMap);
				
				dataMap = new HashMap<>();
				dataMap.put("value", commentNotice.getCommentName());
				map.put("thing1", dataMap);
				
//				dataMap = new HashMap<>();
//				dataMap.put("value", "");
//				map.put("phone_number2", dataMap);
				
				TemplateMsg<Map<String, Map<String, String>>> msg = new TemplateMsg<>();
				msg.setData(map);
				msg.setUrl(url);
				msg.setTemplate_id(msgTemplate.getValue());
				msg.setTouser(commentNotice.getOpenid());
				wechatResponse = sendMsg(msg, accessToken);
				
			}
		}
		return wechatResponse;
		
		
	}

	/**
	 * 业主意见评价
	 * @param notice
	 * @param accessToken
	 */
	public void sendOpinionGradeNotificationMsg(InteractCommentNotice notice, String accessToken) {
		CommonVO3 vo = new CommonVO3();
		vo.setThing3(new TemplateItem(notice.getContent()));	//keyword1
		vo.setThing6(new TemplateItem(notice.getUserName()));	//keyword2
		TemplateMsg<CommonVO3> msg = new TemplateMsg<>();
		msg.setData(vo);
		msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_OPINION_GRADE_NOTIFY, notice.getAppid()));
		msg.setTouser(notice.getOpenid());

		String url = wechatMsgService.getMsgUrl(MsgCfg.URL_OPINION_GRADE_NOTICE);
		url = AppUtil.addAppOnUrl(url, notice.getAppid());
		url = url.replaceAll("INTERACT_ID", notice.getInteractId());
		msg.setUrl(url);
		sendMsg(msg, accessToken);
	}

	/**
	 * 发送维修单信息给维修工
	 * @param workOrderNotification
	 * @param accessToken
	 */
    public void sendWorkOrderMsg(WorkOrderNotification workOrderNotification, String accessToken) {
    	
    	log.info("发送工单模板消息#########" + ", order id: " + workOrderNotification.getOrderId());
    	
    	//更改为使用模版消息发送
		List<Operator> operList = workOrderNotification.getOperatorList();
		if (operList == null || operList.isEmpty()) {
			log.info("workorder owner info is empty, will return .");
			return;
		}
		Operator operator = operList.get(0);
		if (StringUtils.isEmpty(operator.getOpenid())) {
			log.info("workorder owner openid is empty, will return .");
			return;
		}
		
		String title;
		String operName;
		if ("05".equals(workOrderNotification.getOperation())) {
	    	title = "您的"+workOrderNotification.getOrderType()+"工单已被受理";
	    	operName = workOrderNotification.getAcceptor();
		} else if ("02".equals(workOrderNotification.getOperation())) {
			title = "您的"+workOrderNotification.getOrderType()+"工单已被驳回";
			operName = workOrderNotification.getRejector();
		} else if ("07".equals(workOrderNotification.getOperation())) {
			title = "您的"+workOrderNotification.getOrderType()+"工单已完工";
			operName = workOrderNotification.getFinisher();
		} else {
			log.info("unknow operation : " + workOrderNotification.getOperation() + ", will return .");
			return;
		}
		String content = workOrderNotification.getContent();
    	if(!StringUtils.isEmpty(content)) {
			if(content.length() > 120) {
				content = content.substring(0, 110);
				content += "...";
			}
		}
    	
    	CommonVO vo = new CommonVO();
    	vo.setFirst(new TemplateItem(title));
    	vo.setKeyword1(new TemplateItem(workOrderNotification.getOrderId()));
    	vo.setKeyword2(new TemplateItem(content));
    	vo.setKeyword3(new TemplateItem(workOrderNotification.getOrderStatus()));
    	vo.setKeyword4(new TemplateItem(operName));
    	
    	TemplateMsg<CommonVO> msg = new TemplateMsg<>();
    	msg.setData(vo);
    	msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_WORKORDER_NOTIFY, operator.getAppid()));
    	String msgUrl = wechatMsgService.getMsgUrl(MsgCfg.URL_WORK_ORDER_DETAIL);
    	String url = msgUrl + workOrderNotification.getOrderId();
    	msg.setUrl(AppUtil.addAppOnUrl(url, operator.getAppid()));
		msg.setTouser(operator.getOpenid());
    	sendMsg(msg, accessToken);
    	
    }


	/**
	 * 发送电子发票申请模板消息
	 * @param baseEventDTO
	 * @param accessToken
	 * @return
	 */
	public WechatResponse sendInvoiceApplicationMessage(BaseEventDTO baseEventDTO, String accessToken) {
		
		WechatResponse wechatResponse = new WechatResponse();
		String eventKey = baseEventDTO.getEventKey();
    	if (!eventKey.startsWith("01") && !eventKey.startsWith("qrscene_01")) {	//01表示扫二维码开票的场景
			return wechatResponse;
		}
    	String[]eventKeyArr = eventKey.split("\\|");
    	if (eventKeyArr == null || eventKeyArr.length < 4) {
			return wechatResponse;
		}
    	String tradeWaterId = "";
    	String tranAmt = ""; 
    	String shopName = "";
    	try {
			tradeWaterId = eventKeyArr[1];
			tranAmt = eventKeyArr[2];
			shopName = eventKeyArr[3];
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return wechatResponse;
		}
    	
		MsgTemplate msgTemplate = wechatMsgService.getTemplateByNameAndAppIdV2(MsgCfg.TEMPLATE_TYPE_INVOICE_APPLICATION_REMINDER, baseEventDTO.getAppId());
		if (msgTemplate == null) {
			msgTemplate = wechatMsgService.getTemplateByNameAndAppIdV2(MsgCfg.TEMPLATE_TYPE_INVOICE_APPLICATION_REMINDER2, baseEventDTO.getAppId());
		}
		
		String templateId = "";
		if (msgTemplate != null) {
			templateId = msgTemplate.getValue();
		}
		if (StringUtils.isEmpty(templateId)) {
			wechatResponse.setErrcode(99999);
			wechatResponse.setErrmsg("未配置模板消息，tempalteName: " + MsgCfg.TEMPLATE_TYPE_INVOICE_APPLICATION_REMINDER);
			return wechatResponse;
		}
		
		String url = wechatMsgService.getMsgUrl(MsgCfg.URL_INVOICE_APPLICATION_URL);
		url = AppUtil.addAppOnUrl(url, baseEventDTO.getAppId());
		
		String tel = baseEventDTO.getUser().getTel();
		if (StringUtils.isEmpty(tel)) {
			tel = "";
		}
		int templateType = msgTemplate.getType();
		if (templateType == 0) {
			TemplateItem firstItem = new TemplateItem("请点击查看详情进入电子发票自助申请！");
	    	TemplateItem keywordItem1 = new TemplateItem(shopName);
	    	TemplateItem keywordItem2 = new TemplateItem(tranAmt);
	    	TemplateItem remarkItem = new TemplateItem("请及时进行申请");

			CommonVO2 vo = new CommonVO2();
			vo.setFirst(firstItem);
			vo.setKeyword1(keywordItem1);
			vo.setKeyword2(keywordItem2);
			vo.setRemark(remarkItem);

			TemplateMsg<CommonVO2> msg = new TemplateMsg<>();
			msg.setData(vo);
			
			url = url.replaceAll("TRADE_WATER_ID", tradeWaterId).replaceAll("OPENID", baseEventDTO.getOpenid()).replace("TEL", tel);
			msg.setUrl(url);
			msg.setTemplate_id(templateId);
			msg.setTouser(baseEventDTO.getOpenid());
			wechatResponse = sendMsg(msg, accessToken);
		} else if (templateType == 2) {
			
			Map<String, Map<String, String>> map = new HashMap<>();
			Map<String, String> dataMap = new HashMap<>();
			dataMap.put("value", shopName);
			map.put("thing2", dataMap);
			
			dataMap = new HashMap<>();
			dataMap.put("value", tranAmt);
			map.put("amount3", dataMap);
			
			TemplateMsg<Map<String, Map<String, String>>> msg = new TemplateMsg<>();
			msg.setData(map);
			
			url = url.replaceAll("TRADE_WATER_ID", tradeWaterId).replaceAll("OPENID", baseEventDTO.getOpenid()).replace("TEL", tel);
			msg.setUrl(url);
			msg.setTemplate_id(templateId);
			msg.setTouser(baseEventDTO.getOpenid());
			wechatResponse = sendMsg(msg, accessToken);
		}
		return wechatResponse;

	}

	/**
	 * 发送电子发票开具成功的模板消息
	 * @param invoiceNotification
	 * @param accessToken
	 * @return
	 */
	public WechatResponse sendFinishInvoiceMessage(InvoiceNotification invoiceNotification, String accessToken) {
		
		String first = "您的电子发票已开具。";
		if ("1".equals(invoiceNotification.getApplyType())) {
			first = "您的电子发票已红冲。";
		}
		String shopName = invoiceNotification.getShopName();
		String title = invoiceNotification.getInvoiceTitle();
		String type = invoiceNotification.getInvoiceType();
		String amt = invoiceNotification.getJsAmt();
		amt = "-";
		String makeDate = invoiceNotification.getMakeDate();
		
		
		MsgTemplate msgTemplate = wechatMsgService.getTemplateByNameAndAppIdV2(MsgCfg.TEMPLATE_TYPE_INVOICE_FINISH, 
				invoiceNotification.getUser().getAppId());
		
		if (msgTemplate == null) {
			msgTemplate = wechatMsgService.getTemplateByNameAndAppIdV2(MsgCfg.TEMPLATE_TYPE_INVOICE_FINISH2, 
					invoiceNotification.getUser().getAppId());
		}
		String templateId = "";
		if (msgTemplate != null) {
			templateId = msgTemplate.getValue();
		}
		WechatResponse wechatResponse = new WechatResponse();
		if (StringUtils.isEmpty(templateId)) {
			wechatResponse.setErrcode(99999);
			wechatResponse.setErrmsg("未配置模板消息，tempalteName: " + MsgCfg.TEMPLATE_TYPE_INVOICE_APPLICATION_REMINDER);
			return wechatResponse;
		}
		
		int templateType = msgTemplate.getType();
		if (templateType == 0) {
			TemplateItem firstItem = new TemplateItem(first);
	    	TemplateItem keywordItem1 = new TemplateItem(shopName);
	    	TemplateItem keywordItem2 = new TemplateItem(title);
	    	TemplateItem keywordItem3 = new TemplateItem(type);
	    	TemplateItem keywordItem4 = new TemplateItem(amt);
	    	TemplateItem keywordItem5 = new TemplateItem(makeDate);
	    	TemplateItem remarkItem = new TemplateItem("点击查看发票详情");

			CommonVO2 vo = new CommonVO2();
			vo.setFirst(firstItem);
			vo.setKeyword1(keywordItem1);
			vo.setKeyword2(keywordItem2);
			vo.setKeyword3(keywordItem3);
			vo.setKeyword4(keywordItem4);
			vo.setKeyword5(keywordItem5);
			vo.setRemark(remarkItem);

			TemplateMsg<CommonVO2> msg = new TemplateMsg<>();
			msg.setData(vo);
			msg.setTemplate_id(templateId);
			String url = invoiceNotification.getPdfAddr();
			msg.setUrl(url);
			msg.setTouser(invoiceNotification.getOpenid());
			wechatResponse = sendMsg(msg, accessToken);
			
		} else if (templateType == 2) {
			
			Map<String, Map<String, String>> map = new HashMap<>();
			Map<String, String> dataMap = new HashMap<>();
			dataMap.put("value", makeDate);
			map.put("time1", dataMap);
			
//			dataMap = new HashMap<>();
//			dataMap.put("value", "0");
//			map.put("character_string3", dataMap);
			
			dataMap = new HashMap<>();
			dataMap.put("value", amt);
			map.put("amount5", dataMap);
			
			TemplateMsg<Map<String, Map<String, String>>> msg = new TemplateMsg<>();
			msg.setData(map);
			msg.setTemplate_id(templateId);
			String url = invoiceNotification.getPdfAddr();
			msg.setUrl(url);
			msg.setTouser(invoiceNotification.getOpenid());
			wechatResponse = sendMsg(msg, accessToken);
		}
    	
		return wechatResponse;

	}
	
	/**
	 * 发送电子收据申请模板消息
	 * @param baseEventDTO
	 * @param accessToken
	 */
	public WechatResponse sendReceiptApplicationMessage(BaseEventDTO baseEventDTO, String accessToken) {
		
		String eventKey = baseEventDTO.getEventKey();
    	if (!eventKey.startsWith("02") && !eventKey.startsWith("qrscene_02")) {	//01表示扫二维码开票的场景
			return new WechatResponse();
		}
    	String[] eventKeyArr = eventKey.split("\\|");
    	if (eventKeyArr.length < 6) {
			return new WechatResponse();
		}
    	String shopName;
    	String tradeWaterId;
    	String tranAmt;
//    	String payMethod = "";
    	try {
			tradeWaterId = eventKeyArr[1];
			tranAmt = eventKeyArr[2];
			shopName = eventKeyArr[3];
//			payMethod = eventKeyArr[4];
//			feeName = eventKeyArr[5];
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new WechatResponse();
		}
    	
    	TemplateItem firstItem = new TemplateItem("请点击查看详情进入电子收据自助申请！");
    	TemplateItem keywordItem1 = new TemplateItem(shopName);
    	TemplateItem keywordItem2 = new TemplateItem(tranAmt);
    	TemplateItem keywordItem3 = new TemplateItem(DateUtil.dttmFormat(new Date()));
    	TemplateItem remarkItem = new TemplateItem("请及时进行申请");

		CommonVO2 vo = new CommonVO2();
		vo.setFirst(firstItem);
		vo.setKeyword1(keywordItem1);
		vo.setKeyword2(keywordItem2);
		vo.setKeyword3(keywordItem3);
		vo.setRemark(remarkItem);

		TemplateMsg<CommonVO2> msg = new TemplateMsg<>();
		msg.setData(vo);
		msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_RECEIPT_APPLICATION_REMINDER, baseEventDTO.getAppId()));
		String url = wechatMsgService.getMsgUrl(MsgCfg.URL_RECEIPT_APPLICATION_URL);
		url = AppUtil.addAppOnUrl(url, baseEventDTO.getAppId());
		
		String tel = baseEventDTO.getUser().getTel();
		if (StringUtils.isEmpty(tel)) {
			tel = "";
		}
		url = url.replaceAll("TRADE_WATER_ID", tradeWaterId).replaceAll("OPENID", baseEventDTO.getOpenid()).replace("TEL", tel);
		msg.setUrl(url);
		msg.setTouser(baseEventDTO.getOpenid());
		return sendMsg(msg, accessToken);

	}
	
	/**
	 * 发送电子收据开具成功的模板消息
	 * @param receiptNotification
	 * @param accessToken
	 * @return
	 */
	public WechatResponse sendFinishReceiveMessage(ReceiptNotification receiptNotification, String accessToken) {
		
		WechatResponse wechatResponse = null;
		MsgTemplate msgTemplate = wechatMsgService.getTemplateByNameAndAppIdV2(MsgCfg.TEMPLATE_TYPE_RECEIPT_FINISH2, receiptNotification.getAppid());
		if (msgTemplate == null) {
			wechatResponse = new WechatResponse();
			wechatResponse.setErrcode(99999);
			wechatResponse.setErrmsg("未配置模板消息，tempalteName: " + MsgCfg.TEMPLATE_TYPE_RECEIPT_FINISH2);
		} else {
			String receiptId = receiptNotification.getReceiptId();
			String tranAmt = receiptNotification.getTranAmt();
			String createDate = receiptNotification.getApplyDate();
			String url = wechatMsgService.getMsgUrl(MsgCfg.URL_RECEIPT_VIEW_URL);
			url += receiptId;
			url = AppUtil.addAppOnUrl(url, receiptNotification.getAppid());
			int msgType = msgTemplate.getType();
			if (msgType == 1) {
				String first = "您的电子收据已开具。";
		    	TemplateItem firstItem = new TemplateItem(first);
		    	TemplateItem keywordItem1 = new TemplateItem(receiptId);
		    	TemplateItem keywordItem2 = new TemplateItem(tranAmt);
		    	TemplateItem keywordItem3 = new TemplateItem(createDate);
		    	TemplateItem remarkItem = new TemplateItem("点击\"详情\"查看收据");

				CommonVO2 vo = new CommonVO2();
				vo.setFirst(firstItem);
				vo.setKeyword1(keywordItem1);
				vo.setKeyword2(keywordItem2);
				vo.setKeyword3(keywordItem3);
				vo.setRemark(remarkItem);

				TemplateMsg<CommonVO2> msg = new TemplateMsg<>();
				msg.setData(vo);
				msg.setTemplate_id(msgTemplate.getValue());
				msg.setUrl(url);
				msg.setTouser(receiptNotification.getOpenid());
				wechatResponse = sendMsg(msg, accessToken);
			} else if (msgType == 2) {
				Map<String, Map<String, String>> map = new HashMap<>();
				Map<String, String> dataMap = new HashMap<>();
				dataMap.put("value", receiptId);
				map.put("character_string2", dataMap);
				
				dataMap = new HashMap<>();
				dataMap.put("value", tranAmt);
				map.put("amount5", dataMap);
				
				dataMap = new HashMap<>();
				dataMap.put("value", createDate);
				map.put("time8", dataMap);
				
				TemplateMsg<Map<String, Map<String, String>>> msg = new TemplateMsg<>();
				msg.setData(map);
				msg.setTemplate_id(msgTemplate.getValue());
				msg.setUrl(url);
				msg.setTouser(receiptNotification.getOpenid());
				wechatResponse = sendMsg(msg, accessToken);
			}
		}
		return wechatResponse;
	}
	
	/**
	 * 电商支付成功通知
	 * @param user
	 * @param serviceOrder
	 * @param accessToken
	 */
	public void sendOrderSuccessMsg(User user, ServiceOrder serviceOrder, String accessToken) {

		String title = "您好，您购买的"+serviceOrder.getProductName()+"已支付成功。";
		String orderDate = DateUtil.dttmFormat(new Date(serviceOrder.getCreateDate()));
		String customerName = serviceOrder.getReceiverName();

		CommonVO vo = new CommonVO();
		vo.setFirst(new TemplateItem(title));
		vo.setKeyword1(new TemplateItem(String.valueOf(serviceOrder.getId())));
		vo.setKeyword2(new TemplateItem(orderDate));    //下单时间
		vo.setKeyword3(new TemplateItem(customerName));
		vo.setKeyword4(new TemplateItem(serviceOrder.getAddress()));
		vo.setRemark(new TemplateItem(serviceOrder.getProductName() + ", 支付金额:" + serviceOrder.getPrice()));

		TemplateMsg<CommonVO> msg = new TemplateMsg<>();
		msg.setData(vo);
		msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_DELIVERY_MESSAGE, user.getAppId()));
		String msgUrl = "";
		if(ModelConstant.ORDER_TYPE_ONSALE == serviceOrder.getOrderType()) {
			msgUrl = wechatMsgService.getMsgUrl(MsgCfg.URL_CUSTOMER_DELIVERY);
		} else if(ModelConstant.ORDER_TYPE_RGROUP == serviceOrder.getOrderType()) {
			msgUrl = wechatMsgService.getMsgUrl(MsgCfg.URL_CUSTOMER_GROUP_DELIVERY);
		}
		if (!StringUtils.isEmpty(msgUrl)) {
			msgUrl = msgUrl + serviceOrder.getId();
			msgUrl = AppUtil.addAppOnUrl(msgUrl, user.getAppId());
		}
		msg.setUrl(msgUrl);
		msg.setTouser(user.getOpenid());
		sendMsg(msg, accessToken);
	}
	
	/**
	 * 团购到货通知
	 * @param user
	 * @param serviceOrder
	 * @param accessToken
	 */
	public boolean sendRgroupArrivalNotice(User user, ServiceOrder serviceOrder, String accessToken) {

		String title = "您参与的团购【"+serviceOrder.getProductName()+"】，商品已到货。";
		CommonVO vo = new CommonVO();
		vo.setFirst(new TemplateItem(title));
		vo.setKeyword1(new TemplateItem(serviceOrder.getOrderNo()));
		vo.setKeyword2(new TemplateItem(serviceOrder.getProductName()));
		vo.setKeyword3(new TemplateItem(String.valueOf(serviceOrder.getId())));
		vo.setKeyword4(new TemplateItem(serviceOrder.getGroupLeaderAddr()));
		String orderDate = DateUtil.dttmFormat(new Date(serviceOrder.getCreateDate()));
		vo.setKeyword5(new TemplateItem(orderDate));
		vo.setRemark(new TemplateItem("点击查看订单详情。"));

		TemplateMsg<CommonVO> msg = new TemplateMsg<>();
		msg.setData(vo);
		msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_NOTICE_ARRIVAL, user.getAppId()));
		String msgUrl = wechatMsgService.getMsgUrl(MsgCfg.URL_CUSTOMER_GROUP_DELIVERY);
		if (!StringUtils.isEmpty(msgUrl)) {
			msgUrl += serviceOrder.getId();
			msgUrl = AppUtil.addAppOnUrl(msgUrl, user.getAppId());
		}
		msg.setUrl(msgUrl);
		msg.setTouser(user.getOpenid());
		WechatResponse wechatResponse = sendMsg(msg, accessToken);
		
		boolean isSuccess = false;
		if (wechatResponse.getErrcode() == 0) {
            isSuccess = true;
        } else {
        	log.warn(wechatResponse.getErrmsg());
        }
		return isSuccess;
		
	}
	
	/**
     * 成团团长发货提醒
     *
     * @param noticeRgroupSuccess
     * @param accessToken
     */
    public void sendGroupSuccessNotification(NoticeRgroupSuccess noticeRgroupSuccess, String accessToken) {

    	User sendUser = noticeRgroupSuccess.getSendUser();
        String title = "您好，您有新的团购成团。";
        CommonVO vo = new CommonVO();
        vo.setFirst(new TemplateItem(title));
        vo.setKeyword1(new TemplateItem(noticeRgroupSuccess.getProductName()));
        
        String priceStr = noticeRgroupSuccess.getPrice();
        if (!StringUtils.isEmpty(priceStr)) {
        	float price = 0F;
			try {
				price = Float.parseFloat(priceStr);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			if (price == 0F) {
				priceStr = "-";
			}
		} else {
			priceStr = "-";
		}
        vo.setKeyword2(new TemplateItem(priceStr));
        
        vo.setKeyword3(new TemplateItem("当前小区:" + noticeRgroupSuccess.getSectName() + ", " + noticeRgroupSuccess.getGroupNum()+"人"));
        vo.setRemark(new TemplateItem("请尽快安排发货，谢谢。"));

        TemplateMsg<CommonVO> msg = new TemplateMsg<>();
        msg.setData(vo);
        msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_GROUP_SUCCESS_MESSAGE, sendUser.getAppId()));
        String msgUrl = wechatMsgService.getMsgUrl(MsgCfg.URL_GROUP_SUCCESS);
        String url = msgUrl + noticeRgroupSuccess.getRuleId();

        MiniprogramVO miniVo = new MiniprogramVO();
        miniVo.setAppid(miniprogramAppid);
        miniVo.setPagepath(url);
        msg.setMiniprogram(miniVo);
        msg.setUrl(url);
        msg.setTouser(sendUser.getOpenid());
        sendMsg(msg, accessToken);

    }

	/**
	 * 成团团长发货提醒
	 * @param sendUser
	 * @param rgroupVO
	 * @param accessToken
	 */
	public void sendGroupLeaderSubscribe(User sendUser, RgroupVO rgroupVO, String accessToken) {

        String title = "您关注的团长：" + rgroupVO.getRgroupOwner().getOwnerName() + "发布了新团购";
        CommonVO vo = new CommonVO();
        vo.setFirst(new TemplateItem(title));
        String desc = rgroupVO.getDescription();
        if (desc.length() > 10) {
        	desc = desc.substring(0, 10) + "...";
		}
        desc = desc.trim();
        vo.setKeyword1(new TemplateItem(desc));
        vo.setKeyword2(new TemplateItem(rgroupVO.getStartDate()));
        vo.setKeyword3(new TemplateItem(rgroupVO.getRgroupOwner().getOwnerName()));
        String productName = rgroupVO.getProductList()[0].getName();
        if (productName.length() > 8) {
        	productName = productName.substring(0, 8) + "...";
        	productName = "【" + productName + "】正在团购中";
		}
        vo.setKeyword4(new TemplateItem(productName));
        vo.setRemark(new TemplateItem("该消息仅推送给已订阅用户，如有打扰请点击链接进入跟团界面，取消对该团长的订阅即可。"));
        

        TemplateMsg<CommonVO> msg = new TemplateMsg<>();
        msg.setData(vo);
        msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_GROUP_START_MESSAGE, sendUser.getAppId())); //TODO 更换模板id
        String msgUrl = wechatMsgService.getMsgUrl(MsgCfg.URL_LEADER_GROUP_START) + rgroupVO.getRgroupOwner().getOwnerId();
        String url = msgUrl + rgroupVO.getRuleId();

        MiniprogramVO miniVo = new MiniprogramVO();
        miniVo.setAppid(miniprogramAppid);
        miniVo.setPagepath(url);
        msg.setMiniprogram(miniVo);
        msg.setUrl(url);
        msg.setTouser(sendUser.getOpenid());
        sendMsg(msg, accessToken);

    }


	/**
	 * 成团团长发货提醒
	 * @param sendUser
	 * @param rgroupVO
	 * @param accessToken
	 */
	public void sendGroupRegionSubscribe(User sendUser, RgroupVO rgroupVO, String accessToken) {

        String title = "您关注的小区：" + rgroupVO.getRegion().getName() + "发布了新的小区团，一大波人正在跟团";
        CommonVO vo = new CommonVO();
        vo.setFirst(new TemplateItem(title));
        
        String desc = rgroupVO.getDescription();
        if (desc.length() > 24) {
        	desc = desc.substring(0, 24) + "...";
		}
        desc = desc.trim();
        
        vo.setKeyword1(new TemplateItem(desc));
        vo.setKeyword2(new TemplateItem(rgroupVO.getStartDate()));
        vo.setKeyword3(new TemplateItem(rgroupVO.getRegion().getName()));
        String productName = rgroupVO.getProductList()[0].getName();
        if (productName.length() > 8) {
        	productName = productName.substring(0, 8) + "...";
        	productName = "【" + productName + "】正在团购中";
		}
        vo.setKeyword4(new TemplateItem(productName));
        vo.setRemark(new TemplateItem("该消息仅推送给已订阅用户，如有打扰请点击链接进入跟团界面，取消对该小区的订阅即可。"));

        TemplateMsg<CommonVO> msg = new TemplateMsg<>();
        msg.setData(vo);
        msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_GROUP_START_MESSAGE, sendUser.getAppId()));
        String msgUrl = wechatMsgService.getMsgUrl(MsgCfg.URL_REGION_GROUP_START ) + rgroupVO.getRegion().getId();
        String url = msgUrl + rgroupVO.getRuleId();

        MiniprogramVO miniVo = new MiniprogramVO();
        miniVo.setAppid(miniprogramAppid);
        miniVo.setPagepath(url);
        msg.setMiniprogram(miniVo);
        msg.setUrl(url);
        msg.setTouser(sendUser.getOpenid());
        sendMsg(msg, accessToken);

    }

}

package com.yumu.hexie.integration.wechat.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.common.util.AppUtil;
import com.yumu.hexie.common.util.ConfigUtil;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;
import com.yumu.hexie.integration.wechat.entity.common.WechatResponse;
import com.yumu.hexie.integration.wechat.entity.templatemsg.CommonVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.CsOrderVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.HaoJiaAnCommentVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.HaoJiaAnOrderVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.PayNotifyMsgVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.PaySuccessVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.RegisterSuccessVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.RepairOrderVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.ResetPasswordVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.TemplateItem;
import com.yumu.hexie.integration.wechat.entity.templatemsg.TemplateMsg;
import com.yumu.hexie.integration.wechat.entity.templatemsg.WuyePaySuccessVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.WuyeServiceVO;
import com.yumu.hexie.integration.wechat.entity.templatemsg.YuyueOrderVO;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.oldversion.thirdpartyorder.HaoJiaAnComment;
import com.yumu.hexie.model.localservice.oldversion.thirdpartyorder.HaoJiaAnOrder;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.msgtemplate.MsgTemplateService;

@Component
public class TemplateMsgService {
	
	private static final Logger log = LoggerFactory.getLogger(TemplateMsgService.class);

	@Autowired
	private MsgTemplateService msgTemplateService;
	@Autowired
	private RestUtil restUtil;
	
	/**
	 * 模板消息发送
	 */
	private boolean sendMsg(TemplateMsg<?> msg, String accessToken) {
        
		String requestUrl = MsgCfg.TEMPLATE_MSG;
		if(StringUtil.isNotEmpty(accessToken)){
	        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
	    }
		TypeReference<WechatResponse> typeReference = new TypeReference<WechatResponse>() {};
		try {
			WechatResponse wechatResponse = restUtil.exchangeOnBody(requestUrl, msg, typeReference);
			if (wechatResponse.getErrcode() == 0) {
				return true;
			}
			
		} catch (Exception e) {
			log.error("发送模板消息失败: " +e.getMessage());
			log.error(e.getMessage(), e);
		}
		return false;
	}
	
	 /**
     * 不同公众号用不同模板消息
     */
    public String getTemplateByAppId(String appId, String templateType) {
    	
    	Assert.hasText(templateType, "模板消息类型不能为空。");
    	
    	if (StringUtils.isEmpty(appId)) {
			appId = ConfigUtil.get("appId");
		}
    	String templateId = msgTemplateService.getTemplateFromCache(templateType, appId);
    	return templateId;
    	
    }
    
    /**
     * 不同公众号用不同模板消息
     */
    public String getMsgUrl(String urlName) {
    	
    	Assert.hasText(urlName, "消息跳转链接不能为空。");
    	
    	String msgUrl = msgTemplateService.getMsgUrlFromCache(urlName);
    	return msgUrl;
    	
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
		TemplateMsg<PaySuccessVO> msg = new TemplateMsg<PaySuccessVO>();
		msg.setData(vo);
		
		msg.setTemplate_id(getTemplateByAppId(appId, MsgCfg.TEMPLATE_TYPE_PAY_SUCCESS));
		String msgUrl = getMsgUrl(MsgCfg.URL_SUCCESS);
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
		
		TemplateMsg<RegisterSuccessVO>msg = new TemplateMsg<RegisterSuccessVO>();
		msg.setData(vo);
		msg.setTemplate_id(getTemplateByAppId(user.getAppId(), MsgCfg.TEMPLATE_TYPE_REG_SUCCESS));
		
		String msgUrl = getMsgUrl(MsgCfg.URL_REG_SUCCESS);
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
		
		TemplateMsg<WuyePaySuccessVO>msg = new TemplateMsg<WuyePaySuccessVO>();
		msg.setData(vo);
		msg.setTemplate_id(getTemplateByAppId(user.getAppId(), MsgCfg.TEMPLATE_TYPE_WUYEPAY_SUCCESS));
		String msgUrl = MsgCfg.URL_REG_SUCCESS;
		String url = AppUtil.addAppOnUrl(msgUrl, user.getAppId());
		msg.setUrl(url);
		msg.setTouser(user.getOpenid());
		sendMsg(msg, accessToken);
	
	}

	/**
	 * 发送维修单信息给维修工
	 * @param seed
	 * @param ro
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
  
    	TemplateMsg<RepairOrderVO>msg = new TemplateMsg<RepairOrderVO>();
    	msg.setData(vo);
    	msg.setTemplate_id(getTemplateByAppId(appId, MsgCfg.TEMPLATE_TYPE_REPAIR_ASSIGN));
    	String msgUrl = getMsgUrl(MsgCfg.URL_WEIXIU_NOTICE);
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
        TemplateMsg<YuyueOrderVO>msg = new TemplateMsg<YuyueOrderVO>();
        msg.setData(vo);
        msg.setTemplate_id(getTemplateByAppId(appId, MsgCfg.TEMPLATE_TYPE_YUYUE_ASSGIN));
        if (StringUtils.isEmpty(url)) {
			url = getMsgUrl(MsgCfg.URL_SERVICE_RESV) + orderId;
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
    	
    	TemplateMsg<HaoJiaAnOrderVO> msg = new TemplateMsg<HaoJiaAnOrderVO>();
    	msg.setData(vo);
    	msg.setTemplate_id(getTemplateByAppId(user.getAppId(), MsgCfg.TEMPLATE_TYPE_YUYUE_ASSGIN));
    	String url = getMsgUrl(MsgCfg.URL_YUYUE_NOTICE) + hOrder.getyOrderId();
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
    	vo.setOrderNo(new TemplateItem(comment.getYuyueOrderNo()));;//订单编号
    	vo.setMemo(new TemplateItem("用户对您的服务有投诉，请尽快联系用户处理。"));//备注（固定内容）
    	log.error("投诉的userId="+user.getId()+"");
    	log.error("投诉的user="+user+""); 
    	TemplateMsg<HaoJiaAnCommentVO> msg = new TemplateMsg<HaoJiaAnCommentVO>();
    	msg.setData(vo);
    	msg.setTemplate_id(getTemplateByAppId(user.getAppId(), MsgCfg.TEMPLATE_TYPE_COMPLAIN));
    	String url = getMsgUrl(MsgCfg.URL_COMPLAIN_DETAIL) + comment.getId();
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
	  	
	  	TemplateMsg<WuyeServiceVO>msg = new TemplateMsg<WuyeServiceVO>();
    	msg.setData(vo);
    	msg.setTemplate_id(getTemplateByAppId(appId, MsgCfg.TEMPLATE_TYPE_SERVICE));
    	String url = getMsgUrl(MsgCfg.URL_EXPRESS) + userId;
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
    public void sendHexieMessage(String openid, String accessToken, String appId,long messageId,String content) {
    	
    	WuyeServiceVO vo = new WuyeServiceVO();
		vo.setTitle(new TemplateItem("物业通知"));
	  	vo.setOrderNum(new TemplateItem(content));
	  	String recvDate = DateUtil.dtFormat(new Date(), "yyyy-MM-dd HH:mm:ss");
	  	vo.setRecvDate(new TemplateItem(recvDate));
	  	vo.setRemark(new TemplateItem("请点击查看"));
	  	
	  	TemplateMsg<WuyeServiceVO>msg = new TemplateMsg<WuyeServiceVO>();
    	msg.setData(vo);
    	msg.setTemplate_id(getTemplateByAppId(appId, MsgCfg.TEMPLATE_TYPE_MESSAGE));
    	String url = getMsgUrl(MsgCfg.URL_MESSAGE) + messageId;
    	msg.setUrl(AppUtil.addAppOnUrl(url, appId));
    	msg.setTouser(openid);
    	sendMsg(msg, accessToken);

	}
   
    /**
     * 支付到账通知
     * @param openid
     * @param accessToken
     * @param appId
     */
    public void sendPayNotification(AccountNotification accountNotification, String accessToken) {
    	
    	PayNotifyMsgVO vo = new PayNotifyMsgVO();
		vo.setTitle(new TemplateItem("您好，您有一笔订单收款成功。此信息仅供参考，请最终以商户端实际到账结果为准。"));
	  	vo.setTranAmt(new TemplateItem(accountNotification.getFeePrice().toString()));
	  	vo.setPayMethod(new TemplateItem(accountNotification.getPayMethod()));
	  	vo.setTranDateTime(new TemplateItem(accountNotification.getTranDate()));
	  	vo.setTranType(new TemplateItem(accountNotification.getFeeName()));
	  	vo.setRemark(new TemplateItem(accountNotification.getRemark()));
    	
	  	TemplateMsg<PayNotifyMsgVO>msg = new TemplateMsg<PayNotifyMsgVO>();
    	msg.setData(vo);
    	msg.setTemplate_id(getTemplateByAppId(accountNotification.getUser().getAppId(), MsgCfg.TEMPLATE_TYPE_PAY_NOTIFY));
//    	String url = getMsgUrl(MsgCfg.URL_PAY_NOTIFY);
//    	msg.setUrl(AppUtil.addAppOnUrl(url, accountNotification.getUser().getAppId()));
    	msg.setTouser(accountNotification.getUser().getOpenid());
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
    public void sendServiceNotification(User sendUser, ServiceOrder serviceOrder, String accessToken) {

        //更改为使用模版消息发送
    	User user = sendUser;
    	CsOrderVO vo = new CsOrderVO();
    	String title = "您有一个新的服务订单，请及时处理。";
    	vo.setTitle(new TemplateItem(title));
    	vo.setOrderId(new TemplateItem(String.valueOf(serviceOrder.getId())));
    	vo.setServiceType(new TemplateItem(serviceOrder.getSubTypeName()));
    	String customerName = serviceOrder.getReceiverName();
    	vo.setCustomerName(new TemplateItem(customerName));
    	vo.setCustomerTel(new TemplateItem(serviceOrder.getTel()));
    	vo.setRemark(new TemplateItem(serviceOrder.getAddress()));
    	
        TemplateMsg<CsOrderVO>msg = new TemplateMsg<CsOrderVO>();
        msg.setData(vo);
        msg.setTemplate_id(getTemplateByAppId(user.getAppId(), MsgCfg.TEMPLATE_TYPE_CUSTOM_SERVICE_ASSGIN));
        String url = getMsgUrl(MsgCfg.URL_CUSTOM_SERVICE_ASSIGN);
        if (!StringUtils.isEmpty(url)) {
			url = url + serviceOrder.getId();
			url = AppUtil.addAppOnUrl(url, user.getAppId());
		}
        msg.setUrl(url);
        msg.setTouser(user.getOpenid());
        sendMsg(msg, accessToken);
        
    }
    
    /**
     * 发货提醒
     * @param openId
     * @param title
     * @param billName
     * @param requireTime
     * @param url
     * @param accessToken
     * @param appId
     */
    public void sendDeliveryNotification(User sendUser, ServiceOrder serviceOrder, String accessToken) {

    	User user = sendUser;
    	String title = "您有一个新的订单，请及时处理。";
    	String orderDate = serviceOrder.getCreateDateStr();
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
        msg.setTemplate_id(getTemplateByAppId(user.getAppId(), MsgCfg.TEMPLATE_TYPE_DELIVERY_MESSAGE));	
        String url = getMsgUrl(MsgCfg.URL_DELIVERY_DETAIL);
        if (!StringUtils.isEmpty(url)) {
			url = url + serviceOrder.getId();
			url = AppUtil.addAppOnUrl(url, user.getAppId());
		}
        msg.setUrl(url);
        msg.setTouser(user.getOpenid());
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
    	
    	TemplateMsg<ResetPasswordVO>msg = new TemplateMsg<ResetPasswordVO>();
        msg.setData(vo);
        msg.setTemplate_id(getTemplateByAppId(user.getAppId(), MsgCfg.TEMPLATE_TYPE_RESET_PASSWORD));
        msg.setTouser(user.getOpenid());
        sendMsg(msg, accessToken);
    	
    }
    
    /**
     * 用户订单发货提醒
     * @param openId
     * @param title
     * @param billName
     * @param requireTime
     * @param url
     * @param accessToken
     * @param appId
     */
    public void sendCustomerDeliveryMessage(User sendUser, ServiceOrder serviceOrder, String accessToken) {

    	User user = sendUser;
    	String title = "您购买的订单已经发货啦，正快马加鞭向您飞奔而去。";

    	CommonVO vo = new CommonVO();
    	vo.setFirst(new TemplateItem(title));
    	vo.setKeyword1(new TemplateItem(String.valueOf(serviceOrder.getId())));	//订单号
    	vo.setKeyword2(new TemplateItem(serviceOrder.getLogisticName()));	//物流公司名称
    	vo.setKeyword4(new TemplateItem(serviceOrder.getLogisticNo()));	//快递单好
    	String remark = "请及时发货哦～";
    	vo.setRemark(new TemplateItem(remark));
    	
        TemplateMsg<CommonVO>msg = new TemplateMsg<>();
        msg.setData(vo);
        msg.setTemplate_id(getTemplateByAppId(user.getAppId(), MsgCfg.TEMPLATE_TYPE_CUSTOMER_DELIVERY));	
        String url = getMsgUrl(MsgCfg.URL_CUSTOMER_DELIVERY);
        if (!StringUtils.isEmpty(url)) {
			url = url + serviceOrder.getId();
			url = AppUtil.addAppOnUrl(url, user.getAppId());
		}
        msg.setUrl(url);
        msg.setTouser(user.getOpenid());
        sendMsg(msg, accessToken);
        
    }

}

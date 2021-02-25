package com.yumu.hexie.integration.wechat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.common.util.AppUtil;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;
import com.yumu.hexie.integration.wechat.entity.common.WechatResponse;
import com.yumu.hexie.integration.wechat.entity.subscribemsg.ArrivalNotificationVO;
import com.yumu.hexie.integration.wechat.entity.subscribemsg.OrderNotificationVO;
import com.yumu.hexie.integration.wechat.entity.subscribemsg.SubscribeItem;
import com.yumu.hexie.integration.wechat.entity.subscribemsg.SubscribeMsg;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.msgtemplate.WechatMsgService;

/**
 * 微信订阅消息服务
 * @author huym
 *
 */
@Service
public class SubscribeMsgService {
	
	@Autowired
	private RestUtil restUtil;
	@Autowired
	private WechatMsgService wechatMsgService;
	
	private static Logger logger = LoggerFactory.getLogger(SubscribeMsgService.class);
	
	
	 /**
     * 支付到账通知
     * @param openid
     * @param accessToken
     * @param appId
     */
    public void sendPayNotification(AccountNotification accountNotification, String accessToken) {

    	ArrivalNotificationVO vo = new ArrivalNotificationVO();
    	vo.setRemark(new SubscribeItem(accountNotification.getPayMethod()));
    	vo.setTranDate(new SubscribeItem(accountNotification.getTranDate()));
    	vo.setAmount(new SubscribeItem(accountNotification.getFeePrice().toString()));
    	vo.setFeeName(new SubscribeItem(accountNotification.getFeeName()));
    	
    	SubscribeMsg<ArrivalNotificationVO> msg = new SubscribeMsg<>();
    	msg.setData(vo);
    	msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_SUBSCRIBE_PAY_NOTIFY, accountNotification.getUser().getAppId()));
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
    	OrderNotificationVO vo = new OrderNotificationVO();
    	vo.setOrderType(new SubscribeItem(serviceOrder.getSubTypeName()));
    	String customerName = serviceOrder.getReceiverName();
    	vo.setReceiver(new SubscribeItem(customerName + "," + serviceOrder.getTel()));
    	
    	String address = serviceOrder.getAddress();
    	if (!StringUtils.isEmpty(address)) {
    		address = address.substring(address.length()-18, address.length());
        	address = "……"+address;
		}
    	
    	vo.setRecvAddr(new SubscribeItem(address));
    	vo.setCreateDate(new SubscribeItem(serviceOrder.getCreateDateStr()));
    	
    	SubscribeMsg<OrderNotificationVO> msg = new SubscribeMsg<>();
        msg.setData(vo);
        msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_SUBSCRIBE_ORDER_NOTIFY, user.getAppId()));
        String url = wechatMsgService.getMsgUrl(MsgCfg.URL_CUSTOM_SERVICE_ASSIGN);
        if (!StringUtils.isEmpty(url)) {
			url = url + serviceOrder.getId();
			url = AppUtil.addAppOnUrl(url, user.getAppId());
		}
        msg.setPage(url);
        msg.setTouser(user.getOpenid());
        sendMsg(msg, accessToken);
        
    }
    
    /**
	 * 发送维修单信息给维修工
	 * @param seed
	 * @param ro
	 */
    public void sendRepairAssignMsg(RepairOrder ro, ServiceOperator op, String accessToken, String appId) {
    	
    	logger.info("发送维修单分配订阅消息#########" + ", order id: " + ro.getId() + "operator id : " + op.getId());
    	
    	//更改为使用模版消息发送
    	OrderNotificationVO vo = new OrderNotificationVO();
    	vo.setReceiver(new SubscribeItem(ro.getReceiverName()+"," + ro.getTel()));
    	
    	String address = ro.getAddress();
    	if (!StringUtils.isEmpty(address)) {
    		address = address.substring(address.length()-18, address.length());
        	address = "……"+address;
		}
    	vo.setRecvAddr(new SubscribeItem(address));
    	vo.setOrderType(new SubscribeItem("维修单"));
    	vo.setCreateDate(new SubscribeItem(DateUtil.dtFormat(ro.getCreateDate(), DateUtil.dttmSimple)));
  
    	SubscribeMsg<OrderNotificationVO>msg = new SubscribeMsg<>();
    	msg.setData(vo);
    	msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_SUBSCRIBE_ORDER_NOTIFY, appId));
    	String msgUrl = wechatMsgService.getMsgUrl(MsgCfg.URL_WEIXIU_NOTICE);
    	String url = msgUrl + ro.getId();
    	msg.setPage(AppUtil.addAppOnUrl(url, appId));
    	msg.setTouser(op.getOpenId());
    	sendMsg(msg, accessToken);
    	
    }
    
    
    
    /**
	 * 模板消息发送
	 */
	private boolean sendMsg(SubscribeMsg<?> msg, String accessToken) {
        
		String requestUrl = MsgCfg.SUBSCRIBE_MSG;
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
			logger.error("发送模板消息失败: " +e.getMessage());
			logger.error(e.getMessage(), e);
		}
		return false;
	}
	
}

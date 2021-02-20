package com.yumu.hexie.integration.wechat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;
import com.yumu.hexie.integration.wechat.entity.common.WechatResponse;
import com.yumu.hexie.integration.wechat.entity.subscribemsg.BillNotificationVO;
import com.yumu.hexie.integration.wechat.entity.subscribemsg.SubscribeItem;
import com.yumu.hexie.integration.wechat.entity.subscribemsg.SubscribeMsg;
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

    	BillNotificationVO vo = new BillNotificationVO();
    	vo.setName(new SubscribeItem(accountNotification.getFeeName()));
    	vo.setDate(new SubscribeItem(accountNotification.getTranDate()));
    	vo.setAmount(new SubscribeItem(accountNotification.getFeeName().toString()));
    	vo.setPhone(new SubscribeItem(accountNotification.getPayMethod()));
    	vo.setThing(new SubscribeItem(accountNotification.getRemark()));
    	
    	
    	SubscribeMsg<BillNotificationVO> msg = new SubscribeMsg<>();
    	msg.setData(vo);
    	msg.setTemplate_id(wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_SUBSCRIBE_PAY_NOTIFY, accountNotification.getUser().getAppId()));
    	msg.setTouser(accountNotification.getUser().getOpenid());
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

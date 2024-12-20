package com.yumu.hexie.integration.alipay.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayEcoCityserviceMessageSendModel;
import com.alipay.api.domain.AlipayEcoMessageEntity;
import com.alipay.api.request.AlipayEcoCityserviceMessageSendRequest;
import com.alipay.api.response.AlipayEcoCityserviceMessageSendResponse;
import com.yumu.hexie.integration.notify.Operator;
import com.yumu.hexie.integration.notify.WorkOrderNotification;
import com.yumu.hexie.integration.wechat.entity.templatemsg.AliTemplateMsg;
import com.yumu.hexie.integration.wechat.service.MsgCfg;
import com.yumu.hexie.model.msgtemplate.MsgTemplate;
import com.yumu.hexie.service.msgtemplate.AliMsgService;

@Component
public class AliTemplateMsgService {

	private static Logger log = LoggerFactory.getLogger(AliTemplateMsgService.class);
	
	@Resource
	private AliSDKService aliSDKService;
	@Resource
	private AliMsgService aliMsgService;
	
	private static final String TEMPLATE_NAME_POSTFIX = "Ali";
	private static final String ALIPAY_SCHEME = "alipays://platformapi/startApp?appId=APPID";
	private static final String SERVICE_INDUSTRY_TYPE = "14";
	private static final String DEFAULT_CITY_CODE = "310000";	//默认上海
	
//	缴费支付W100731791913920610
//	维修工单W100597608124903955
//	联系管家W100647071540228659
//	消息订阅W100591778785193343
//	缴费记录W100597152137104045
	private Map<String, String> APP_CODE_MAP;
	
	@PostConstruct
	public void initAppCode() {
		APP_CODE_MAP = new HashMap<>();
		APP_CODE_MAP.put("wuyepay", "W100731791913920610");
		APP_CODE_MAP.put("workorder", "W100597608124903955");
		APP_CODE_MAP.put("opinion", "W100647071540228659");
		APP_CODE_MAP.put("payrecord", "W100597152137104045");
		APP_CODE_MAP.put("msgsubscribe", "W100591778785193343");
	}
	
	/**
	 * 发送维修单信息给业主
	 * @param workOrderNotification
	 * @param accessToken
	 * @throws UnsupportedEncodingException 
	 */
    public void sendWorkOrderMsg(WorkOrderNotification workOrderNotification) {
    	
    	log.info("ali city service, 发送工单订阅消息#########" + ", order id: " + workOrderNotification.getOrderId());
    	
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
		
		@SuppressWarnings("unused")
		String title;
		if ("05".equals(workOrderNotification.getOperation())) {
	    	title = "您的"+workOrderNotification.getOrderType()+"工单已被受理";
		} else if ("02".equals(workOrderNotification.getOperation())) {
			title = "您的"+workOrderNotification.getOrderType()+"工单已被驳回";
		} else if ("07".equals(workOrderNotification.getOperation())) {
			title = "您的"+workOrderNotification.getOrderType()+"工单已完工";
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
    	String templateName = MsgCfg.TEMPLATE_TYPE_WORKORDER_NOTIFY + TEMPLATE_NAME_POSTFIX;
    	MsgTemplate msgTemplate = aliMsgService.getTemplateByNameAndAppId(templateName, operator.getAppid());
    	if (msgTemplate == null) {
    		log.warn("ali city service, [工单]未配置模板消息, appid : " + operator.getAppid());
    		return;
		}
    	String miniUrl = aliMsgService.getMsgUrl(MsgCfg.URL_WORK_ORDER_DETAIL_ALI_MINI);
    	miniUrl += workOrderNotification.getOrderId();
//    	String mpPage = "alipays://platformapi/startApp?appId=2021004116648237&page=/pages/workorder/workorder?orderid=";
    	String msPage = ALIPAY_SCHEME.replace("APPID", operator.getAppid());
    	msPage += "&page=" + miniUrl;
    	
//    	String templateId = "10710";
    	String templateId = msgTemplate.getValue();
    	
    	JSONObject extInfo = new JSONObject();
    	extInfo.put("appid", operator.getAppid());
    	try {
			extInfo.put("pageUrl", URLEncoder.encode(miniUrl, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		}
    	extInfo.put("bxnr", content);
    	extInfo.put("bxzt", workOrderNotification.getOrderStatus());	//不行的话写title
    	
    	AliTemplateMsg<JSONObject> msg = new AliTemplateMsg<>();
    	msg.setData(extInfo);
    	msg.setTemplateId(templateId);
    	msg.setUrl(msPage);
		msg.setAliUserId(operator.getOpenid());
		msg.setAppid(operator.getAppid());
		String cityCode = workOrderNotification.getCityCode();
		if (StringUtils.isEmpty(cityCode)) {
			cityCode = DEFAULT_CITY_CODE;
		}
		msg.setCityCode(cityCode); 
		String appCode = APP_CODE_MAP.get("workorder");
		msg.setAppCode(appCode);
		msg.setIndudstryType(SERVICE_INDUSTRY_TYPE);
    	sendMsg(msg);
    }
    
    
    
    
    /**
	 * 发送模板消息
	 * @param marketingConsultParam
	 * @return 
	 */
	public void sendMsg(AliTemplateMsg<JSONObject> msg) {
		
		// 构造请求参数以调用接口
        AlipayEcoCityserviceMessageSendRequest request = new AlipayEcoCityserviceMessageSendRequest();
        AlipayEcoCityserviceMessageSendModel model = new AlipayEcoCityserviceMessageSendModel();
        
        // 设置消息列表
        List<AlipayEcoMessageEntity> msgList = new ArrayList<>();
        AlipayEcoMessageEntity msgList0 = new AlipayEcoMessageEntity();
        
        msgList0.setTargetUrl(msg.getUrl());
        // uid参数未来计划废弃，存量商户可继续使用，新商户请使用openid。请根据应用-开发配置-openid配置选择支持的字段。
        msgList0.setAlipayUserId(msg.getAliUserId());
        msgList0.setCityCode(msg.getCityCode());
        msgList0.setIndustryType(Long.valueOf(msg.getIndudstryType()));	//房产物业固定为“14” TODO
        msgList0.setExtInfo(msg.getData().toString());
        msgList0.setMsgType(Long.valueOf(msg.getTemplateId()));	//模板消息id
        msgList0.setAppCode(msg.getAppCode());	//TODO 光华消息id 10710
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-", "");
        msgList0.setUuid(uuid);
        msgList.add(msgList0);
        model.setMsgList(msgList);
        // 设置敏感字段加密类型
//        model.setEncryptType("md5");
        // 设置消息条数
        model.setBatchSize(1L);
        request.setBizModel(model);
        
        String appid = msg.getAppid();
        log.info("msg uuid : {}, appid: {}", uuid, appid);
        log.info("send ali template msg, {}", JSONObject.toJSON(request));

		AlipayClient execClient = aliSDKService.getClient(appid);
		try {
			AlipayEcoCityserviceMessageSendResponse response = execClient.execute(request);
			log.info("aliMsgSendResp : {}", response.getBody());
		} catch (AlipayApiException e) {
			log.error(e.getMessage(), e);
		}
		
	}
}

package com.yumu.hexie.service.common.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.yumu.hexie.integration.notify.*;
import com.yumu.hexie.service.shequ.vo.InteractCommentNotice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.integration.wechat.entity.common.WechatResponse;
import com.yumu.hexie.integration.wechat.entity.customer.Article;
import com.yumu.hexie.integration.wechat.entity.customer.News;
import com.yumu.hexie.integration.wechat.entity.customer.NewsMessage;
import com.yumu.hexie.integration.wechat.entity.customer.Text;
import com.yumu.hexie.integration.wechat.entity.customer.TextMessage;
import com.yumu.hexie.integration.wechat.service.CustomService;
import com.yumu.hexie.integration.wechat.service.MsgCfg;
import com.yumu.hexie.integration.wechat.service.SubscribeMsgService;
import com.yumu.hexie.integration.wechat.service.TemplateMsgService;
import com.yumu.hexie.model.card.dto.EventSubscribeDTO;
import com.yumu.hexie.model.event.dto.BaseEventDTO;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.localservice.bill.YunXiyiBill;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.subscribemsg.UserSubscribeMsg;
import com.yumu.hexie.model.subscribemsg.UserSubscribeMsgRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.billpush.vo.BillPushDetail;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.msgtemplate.WechatMsgService;
import com.yumu.hexie.service.o2o.OperatorService;
import com.yumu.hexie.service.sales.req.NoticeRgroupSuccess;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: GotongServiceImple.java, v 0.1 2016年1月8日 上午10:01:41  Exp $
 */
@Service("gotongService")
public class GotongServiceImpl implements GotongService {

    private static final Logger LOG = LoggerFactory.getLogger(GotongServiceImpl.class);
    
    @Inject
    private ServiceOperatorRepository  serviceOperatorRepository;
    @Inject
    private OperatorService  operatorService;
    @Inject
    private SystemConfigService systemConfigService;
    @Inject
    private UserRepository userRepository;
    @Autowired
    private TemplateMsgService templateMsgService;
    @Autowired
    private WechatMsgService wechatMsgService;
    @Autowired
    private SubscribeMsgService subscribeMsgService;
    @Autowired
    private UserSubscribeMsgRepository userSubscribeMsgRepository;
    
    
    @Async
    @Override
    public void sendRepairAssignMsg(long opId,RepairOrder order){
        ServiceOperator op = serviceOperatorRepository.findById(opId).get();
        User opUser = userRepository.findById(op.getUserId());
        if (opUser == null) {
        	LOG.error("operator user is null, will return.");
			return;
		}
        User orderUser = userRepository.findById(order.getUserId());
        if (orderUser == null) {
        	LOG.error("order user is null, will return.");
			return;
		}
        if (StringUtils.isEmpty(opUser.getAppId()) ) {
			return;
		}
        if (!opUser.getAppId().equals(orderUser.getAppId())) {
			return;
		}
        String accessToken = systemConfigService.queryWXAToken(opUser.getAppId());
        
        String templateId = wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_SUBSCRIBE_ORDER_NOTIFY, opUser.getAppId());
        UserSubscribeMsg userSubscribeMsg = userSubscribeMsgRepository.findByOpenidAndTemplateId(opUser.getOpenid(), templateId);
        
        LOG.info("userSubscribeMsg : " + userSubscribeMsg);
        if (userSubscribeMsg != null) {
        	subscribeMsgService.sendRepairAssignMsg(order, op, accessToken, opUser.getAppId());
		} else {
			templateMsgService.sendRepairAssignMsg(order, op, accessToken, opUser.getAppId());
		}
        
    }
    
    @Async
    @Override
    public void sendRepairAssignedMsg(RepairOrder order){
        
    	User user = userRepository.findById(order.getUserId());
        String url = wechatMsgService.getMsgUrl(MsgCfg.URL_WEIXIU_DETAIL) + order.getId();
        News news = new News(new ArrayList<>());
        Article article = new Article();
        article.setTitle("您的维修单已被受理");
        article.setDescription("点击查看详情");
        article.setUrl(url);
        news.getArticles().add(article);
        NewsMessage msg = new NewsMessage(news);
        msg.setTouser(user.getOpenid());
        msg.setMsgtype(ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);
        String accessToken = systemConfigService.queryWXAToken(user.getAppId());
        CustomService.sendCustomerMessage(msg, accessToken);
    }
    
    /**
     * 发送用户关注消息
     */
    @Override
	public boolean sendSubscribeMsg(EventSubscribeDTO subscribeVO) {
    	
    	TextMessage textmsg;
    	boolean flag = false;
    	User user = subscribeVO.getUser();
    	
    	boolean cardService = systemConfigService.isCardServiceAvailable(user.getAppId());
    	String accessToken = systemConfigService.queryWXAToken(user.getAppId());
    	
    	String key = "DEFAULT_SIGN";
		key = key + "_" + user.getAppId();
		String appName = systemConfigService.getSysConfigByKey(key);
		if (StringUtils.isEmpty(appName)) {
			appName = "合协社区";
		}
    	
		if (cardService) {
			Text text = new Text();
			text.setContent(appName + "欢迎您的加入！");
			textmsg = new TextMessage(text);
			textmsg.setTouser(user.getOpenid());
			textmsg.setMsgtype(ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
			flag = CustomService.sendCustomerMessage(textmsg, accessToken);
		}
    	
    	return flag;
         
	}
    
    /**
     * 注册红包
     */
    @Override
	public void sendRegiserMsg(User user) {

         Article article = new Article();
         article.setTitle("欢迎您的加入！");
         article.setDescription("您已获得注册红包，点击查看。");
         
         String picUrl = wechatMsgService.getMsgUrl(MsgCfg.URL_SUBSCRIBE_IMG);
         String url = wechatMsgService.getMsgUrl(MsgCfg.URL_SUBSCRIBE_DETAIL);
         article.setPicurl(picUrl);
         article.setUrl(url);
         News news = new News(new ArrayList<>());
         news.getArticles().add(article);
         NewsMessage msg = new NewsMessage(news);
         msg.setTouser(user.getOpenid());
         msg.setMsgtype(ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);
         String accessToken = systemConfigService.queryWXAToken(user.getAppId());
         CustomService.sendCustomerMessage(msg, accessToken);
	}
    

    /** 
     * @param opId
     * @param bill
     * @see com.yumu.hexie.service.common.GotongService#sendXiyiAssignMsg(long, com.yumu.hexie.model.localservice.bill.YunXiyiBill)
     */
    @Override
    public void sendXiyiAssignMsg(long opId, YunXiyiBill bill) {
    	
    	ServiceOperator op = serviceOperatorRepository.findById(opId).get();
    	User user = userRepository.findById(op.getUserId());
        String url = wechatMsgService.getMsgUrl(MsgCfg.URL_XIYI_NOTICE) + bill.getId();
        
        News news = new News(new ArrayList<>());
        Article article = new Article();
        article.setTitle(op.getName()+":您有新的洗衣订单！");
        article.setDescription("有新的维修单"+bill.getProjectName()+"快来抢单吧");
        article.setUrl(url);
        news.getArticles().add(article);
        NewsMessage msg = new NewsMessage(news);
        msg.setTouser(op.getOpenId());
        msg.setMsgtype(ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);
        
        String accessToken = systemConfigService.queryWXAToken(user.getAppId());
        CustomService.sendCustomerMessage(msg, accessToken);
    }

    /**
     *
     * @param serviceType
     * @param title
     * @param billName
     * @param requireTime
     * @param url
     * @param remark
     */
    @Async
    @Override
    public void sendCommonYuyueBillMsg(int serviceType,String title, String billName, String requireTime, String url, String remark) {
        LOG.error("发送预约通知！["+serviceType+"]" + billName + " -- " + requireTime);
        List<ServiceOperator> ops = operatorService.findByType(serviceType);
        for(ServiceOperator op: ops) {
            LOG.error("发送到操作员！["+serviceType+"]" + billName + " -- " + op.getName() + "--" + op.getId());
            User user = userRepository.findById(op.getUserId());
            String accessToken = systemConfigService.queryWXAToken(user.getAppId());
            templateMsgService.sendYuyueBillMsg("", op.getOpenId(), title, billName, requireTime, url, accessToken, user.getAppId(), remark);    
        }
        
    }
    
    /**
     * 服务预约模板消息
     */
	@Override
	public void sendServiceResvMsg(long threadId, String openId, String title, String content, String requireTime, String remark, String appId) {
		
		String accessToken = systemConfigService.queryWXAToken(appId);
		templateMsgService.sendYuyueBillMsg(String.valueOf(threadId), openId, title, content, requireTime, "", accessToken, remark, appId);    
		
	}
	
	/**
     * 平台公告通知群发
     */
	@Override
	public WechatResponse sendGroupMessage(String openId, String appId, long msgId, String content) {
		
		String accessToken = systemConfigService.queryWXAToken(appId);
		return templateMsgService.sendHexieMessage(openId, accessToken, appId, msgId, content);
	}
	
	/**
	 * 交易到账通知
	 */
	@Override
	public void sendPayNotification(AccountNotification accountNotify) {
		
		String accessToken = systemConfigService.queryWXAToken(accountNotify.getUser().getAppId());
		
		String templateId = wechatMsgService.getTemplateByNameAndAppId(MsgCfg.TEMPLATE_TYPE_SUBSCRIBE_PAY_NOTIFY, accountNotify.getUser().getAppId());
		UserSubscribeMsg userSubscribeMsg = userSubscribeMsgRepository.findByOpenidAndTemplateId(accountNotify.getUser().getOpenid(), templateId);
		LOG.info("userSubscribeMsg : " + userSubscribeMsg);
		if (userSubscribeMsg != null) {
			subscribeMsgService.sendPayNotification(accountNotify, accessToken);
		} else {
			templateMsgService.sendPayNotification(accountNotify, accessToken);
		}
	}

	@Async
    @Override
    public void sendCustomServiceAssignedMsg(ServiceOrder serviceOrder){
		
		LOG.info("发送自定服务接单通知， serviceOrder : " + serviceOrder.getId());
		
        User user = userRepository.findById(serviceOrder.getUserId());
        String url = wechatMsgService.getMsgUrl(MsgCfg.URL_CUSTOM_SERVICE_DETAIL) + serviceOrder.getId();
        
        News news = new News(new ArrayList<>());
        Article article = new Article();
        article.setTitle("您的服务订单已被受理");
        article.setDescription("点击查看详情");
        article.setUrl(url);
        news.getArticles().add(article);
        NewsMessage msg = new NewsMessage(news);
        msg.setTouser(user.getOpenid());
        msg.setMsgtype(ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);
        String accessToken = systemConfigService.queryWXAToken(user.getAppId());
        CustomService.sendCustomerMessage(msg, accessToken);
    }
	
	@Override
	public void sendResetPasswordMsg(User user, String password) {

		LOG.info("发送重置密码模板消息！ sendUser : " + user);
		String accessToken = systemConfigService.queryWXAToken(user.getAppId());
		templateMsgService.sendResetPasswordMsg(user, password, accessToken);
	}
	
	/**
	 * 用户发货提醒
	 */
	@Override
	public void sendCustomerDelivery(User user, ServiceOrder serviceOrder) {

		LOG.info("用户发货提醒！ sendUser : " + user);
		String accessToken = systemConfigService.queryWXAToken(user.getAppId());
		templateMsgService.sendCustomerDeliveryMessage(user, serviceOrder, accessToken);
	}

    /**
	 * 交易到账通知(发送给房屋绑定者)
	 */
	@Override
	public void sendPayNotification4HouseBinder(AccountNotification accountNotify) {
		
		String accessToken = systemConfigService.queryWXAToken(accountNotify.getUser().getAppId());
		templateMsgService.sendPayNotification4HouseBinder(accountNotify, accessToken);
		
	}

    /**
     * 账单推送通知
     */
    @Override
    public String sendBillPush(String openId, String appId, BillPushDetail billPushDetail) {
        String accessToken = systemConfigService.queryWXAToken(appId);
        return templateMsgService.sendBillNotificationMessage(openId, accessToken, appId, billPushDetail);
    }
    
    /**
	 * 工单消息通知
	 */
	@Override
	public boolean sendWorkOrderNotification(WorkOrderNotification workOrderNotification) {
		
		boolean success = true;
		//模板消息
		List<Operator> operList = workOrderNotification.getOperatorList();
		if (operList == null || operList.isEmpty()) {
			LOG.info("workorder oper is empty, will return .");
			return true;
		}
		Operator operator = operList.get(0);
        
		String accessToken = systemConfigService.queryWXAToken(operator.getAppid());
		templateMsgService.sendWorkOrderMsg(workOrderNotification, accessToken);
		return success;
		
	}
	
	/**
     * 发送用户申请电子发票消息
     */
    @Override
	public WechatResponse sendMsg4ApplicationInvoice(BaseEventDTO baseEventDTO) {

    	String accessToken = systemConfigService.queryWXAToken(baseEventDTO.getAppId());
        return templateMsgService.sendInvoiceApplicationMessage(baseEventDTO, accessToken);
        
	}
    
    /**
     * 发送开具电子发票消息
     */
    @Override
	public WechatResponse sendMsg4FinishInvoice(InvoiceNotification invoiceNotification) {

    	String accessToken = systemConfigService.queryWXAToken(invoiceNotification.getUser().getAppId());
        return templateMsgService.sendFinishInvoiceMessage(invoiceNotification, accessToken);
        
	}
    
    /**
     * 发送用户申请电子收据消息
     */
    @Override
	public WechatResponse sendMsg4ApplicationReceipt(BaseEventDTO baseEventDTO) {

    	String accessToken = systemConfigService.queryWXAToken(baseEventDTO.getAppId());
        return templateMsgService.sendReceiptApplicationMessage(baseEventDTO, accessToken);
        
	}
    
    /**
     * 发送开具电子收据完成消息
     */
    @Override
	public WechatResponse sendMsg4FinishReceipt(ReceiptNotification receiptNotification) {

    	String accessToken = systemConfigService.queryWXAToken(receiptNotification.getAppid());
        return templateMsgService.sendFinishReceiveMessage(receiptNotification, accessToken);
        
	}
    
    /**
     * 团购成团模板消息发送
     * @param noticeRgroupSuccess
     */
    @Override
    public void sendGroupSuccessNotification(NoticeRgroupSuccess noticeRgroupSuccess) {
        List<Long> listUser = noticeRgroupSuccess.getOpers();
        for (long userId : listUser) {
            User sendUser = userRepository.findById(userId);
            String accessToken = systemConfigService.queryWXAToken(sendUser.getAppId());
            noticeRgroupSuccess.setSendUser(sendUser);
            templateMsgService.sendGroupSuccessNotification(noticeRgroupSuccess, accessToken);
        }

    }

    @Override
    public WechatResponse sendInteractNotification(InteractCommentNotice commentNotice) {
        if(ObjectUtils.isEmpty(commentNotice.getAppid())) {
            WechatResponse response = new WechatResponse();
            response.setErrcode(99998);
            return response;
        }
        String accessToken = systemConfigService.queryWXAToken(commentNotice.getAppid());
        return templateMsgService.sendOpinionNotificationMessage(commentNotice, accessToken);
    }

    @Override
    public void sendInteractGradeNotification(InteractCommentNotice notice) {
        String accessToken = systemConfigService.queryWXAToken(notice.getAppid());
        templateMsgService.sendOpinionGradeNotificationMsg(notice, accessToken);
    }

    @Override
    public WechatResponse sendRenovationNotification(RenovationNotification notice) {
        if(ObjectUtils.isEmpty(notice.getAppid())) {
            WechatResponse response = new WechatResponse();
            response.setErrcode(99998);
            return response;
        }
        String accessToken = systemConfigService.queryWXAToken(notice.getAppid());
        return templateMsgService.sendRenovationNotificationMessage(notice, accessToken);
    }

}

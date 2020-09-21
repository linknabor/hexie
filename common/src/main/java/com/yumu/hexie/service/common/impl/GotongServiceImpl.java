/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.common.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.integration.notify.PayNotification.AccountNotification;
import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.integration.wechat.entity.customer.Article;
import com.yumu.hexie.integration.wechat.entity.customer.News;
import com.yumu.hexie.integration.wechat.entity.customer.NewsMessage;
import com.yumu.hexie.integration.wechat.service.CustomService;
import com.yumu.hexie.integration.wechat.service.MsgCfg;
import com.yumu.hexie.integration.wechat.service.TemplateMsgService;
import com.yumu.hexie.model.card.dto.EventSubscribeDTO;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.localservice.bill.YunXiyiBill;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.o2o.OperatorService;

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
    
    
    @Async
    @Override
    public void sendRepairAssignMsg(long opId,RepairOrder order,int distance){
        ServiceOperator op = serviceOperatorRepository.findById(opId).get();
        User opUser = userRepository.findById(op.getUserId());
        User orderUser = userRepository.findById(order.getUserId());
        if (StringUtils.isEmpty(opUser.getAppId()) ) {
			return;
		}
        if (!opUser.getAppId().equals(orderUser.getAppId())) {
			return;
		}
        String accessToken = systemConfigService.queryWXAToken(opUser.getAppId());
        templateMsgService.sendRepairAssignMsg(order, op, accessToken, opUser.getAppId());
    }
    
    @Async
    @Override
    public void sendRepairAssignedMsg(RepairOrder order){
        
    	User user = userRepository.findById(order.getUserId());
        String url = templateMsgService.getMsgUrl(MsgCfg.URL_WEIXIU_DETAIL) + order.getId();
        News news = new News(new ArrayList<Article>());
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
     * 发送关注客服消息
     */
    @Override
	public boolean sendSubscribeMsg(EventSubscribeDTO subscribeVO) {
    	
    	NewsMessage msg = null;
    	User user = subscribeVO.getUser();
    	String url = templateMsgService.getMsgUrl(MsgCfg.URL_SUBSCRIBE_IMG);
    	String cardServiceApps = systemConfigService.getSysConfigByKey("CARD_SERVICE_APPS");
    	String accessToken = systemConfigService.queryWXAToken(user.getAppId());
    	if (!StringUtils.isEmpty(cardServiceApps)) {
    		if (cardServiceApps.indexOf(user.getAppId()) > -1) {
    			Article article = new Article();
    			article.setTitle("欢迎您的加入！");
    			article.setDescription("点击这里注册会员，新会员独享多重好礼。");
    			article.setPicurl(url);
    			article.setUrl(subscribeVO.getGetCardUrl());	//开卡组件获取链接
    			
    			News news = new News(new ArrayList<Article>());
    			news.getArticles().add(article);
    			msg = new NewsMessage(news);
    			msg.setTouser(user.getOpenid());
    			msg.setMsgtype(ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);
			}
    		
		}
    	if (msg == null) {
			return false;
		}
    	
		return CustomService.sendCustomerMessage(msg, accessToken);
         
	}
    
    /**
     * 注册红包
     */
    @Override
	public void sendRegiserMsg(User user) {

         Article article = new Article();
         article.setTitle("欢迎您的加入！");
         article.setDescription("您已获得注册红包，点击查看。");
         
         String picUrl = templateMsgService.getMsgUrl(MsgCfg.URL_SUBSCRIBE_IMG);
         String url = templateMsgService.getMsgUrl(MsgCfg.URL_SUBSCRIBE_DETAIL);
         article.setPicurl(picUrl);
         article.setUrl(url);
         News news = new News(new ArrayList<Article>());
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
        String url = templateMsgService.getMsgUrl(MsgCfg.URL_XIYI_NOTICE) + bill.getId();
        
        News news = new News(new ArrayList<Article>());
        Article article = new Article();
        article.setTitle(op.getName()+":您有新的洗衣订单！");
        article.setDescription("有新的维修单"+bill.getProjectName()+"快来抢单吧");
        //article.setPicurl(so.getProductPic());
        article.setUrl(url);
        news.getArticles().add(article);
        NewsMessage msg = new NewsMessage(news);
        msg.setTouser(op.getOpenId());
        msg.setMsgtype(ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);
        
        String accessToken = systemConfigService.queryWXAToken(user.getAppId());
        CustomService.sendCustomerMessage(msg, accessToken);
    }
    /** 
     * @param count
     * @param billName
     * @param requireTime
     * @param url
     * @see com.yumu.hexie.service.common.GotongService#sendYuyueBillMsg(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
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
	public void sendGroupMessage(String openId, String appId, long msgId, String content) {
		
		String accessToken = systemConfigService.queryWXAToken(appId);
		templateMsgService.sendHexieMessage(openId, accessToken, appId, msgId, content);
	}
	
	/**
	 * 交易到账通知
	 */
	@Override
	public void sendPayNotification(AccountNotification accountNotify) {
		
		String accessToken = systemConfigService.queryWXAToken(accountNotify.getUser().getAppId());
		templateMsgService.sendPayNotification(accountNotify, accessToken);
		
	}
	
	/**
	 * 自定义服务通知
	 */
	@Override
	public void sendServiceNotification(User sendUser, ServiceOrder serviceOrder) {

		LOG.info("发送自定义服务通知！ sendUser : " + sendUser);
		String accessToken = systemConfigService.queryWXAToken(sendUser.getAppId());
		templateMsgService.sendServiceNotification(sendUser, serviceOrder, accessToken);
	}
	
	
	@Async
    @Override
    public void sendCustomServiceAssignedMsg(ServiceOrder serviceOrder){
		
		LOG.info("发送自定服务接单通知， serviceOrder : " + serviceOrder.getId());
		
        User user = userRepository.findById(serviceOrder.getUserId());
        String url = templateMsgService.getMsgUrl(MsgCfg.URL_CUSTOM_SERVICE_DETAIL) + serviceOrder.getId();
        
        News news = new News(new ArrayList<Article>());
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
	
	/**
	 * 自定义服务通知
	 */
	@Override
	public void sendDeliveryNotification(User sendUser, ServiceOrder serviceOrder) {

		LOG.info("发送自定义服务通知！ sendUser : " + sendUser);
		String accessToken = systemConfigService.queryWXAToken(sendUser.getAppId());
		templateMsgService.sendDeliveryNotification(sendUser, serviceOrder, accessToken);
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
	

}

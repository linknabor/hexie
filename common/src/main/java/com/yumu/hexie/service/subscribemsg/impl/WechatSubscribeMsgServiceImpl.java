package com.yumu.hexie.service.subscribemsg.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.msgtemplate.MsgTemplate;
import com.yumu.hexie.model.subscribemsg.UserSubscribeMsg;
import com.yumu.hexie.model.subscribemsg.UserSubscribeMsgRepository;
import com.yumu.hexie.model.subscribemsg.dto.EventSubscribeMsg;
import com.yumu.hexie.model.subscribemsg.dto.EventSubscribeMsg.TemplateDetail;
import com.yumu.hexie.service.msgtemplate.WechatMsgService;
import com.yumu.hexie.service.subscribemsg.WechatSubscribeMsgService;

@Service
public class WechatSubscribeMsgServiceImpl implements WechatSubscribeMsgService {

	private static Logger logger = LoggerFactory.getLogger(WechatSubscribeMsgServiceImpl.class);
	
	@Autowired
	private UserSubscribeMsgRepository userSubscribeMsgRepository;
	@Autowired
	private WechatMsgService wechatMsgService;
	
	@Transactional
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_CACHED, key = "#eventSubscribeMsg.fromUserName")
	@Override
	public void eventSubscribeMsg(EventSubscribeMsg eventSubscribeMsg) {

		String openid = eventSubscribeMsg.getFromUserName();
		List<TemplateDetail> templateList = eventSubscribeMsg.getList();
		for (TemplateDetail templateDetail : templateList) {
			
			MsgTemplate msgTemplate = wechatMsgService.getTemplateByTemplateId(templateDetail.getTemplateId());
			if (msgTemplate == null) {
				logger.info("cannot find msgTemplate, templateId : " + templateDetail.getTemplateId());
				continue;
			}
			UserSubscribeMsg userSubscribeMsg = userSubscribeMsgRepository.findByOpenidAndTemplateId(openid, templateDetail.getTemplateId());
			
			if (userSubscribeMsg == null) {
				userSubscribeMsg = new UserSubscribeMsg();
				userSubscribeMsg.setOpenid(openid);
				userSubscribeMsg.setTemplateId(templateDetail.getTemplateId());
				userSubscribeMsg.setType(msgTemplate.getType());
				userSubscribeMsg.setBizType(msgTemplate.getBizType());
			}
			String statusStr = templateDetail.getSubscribeStatusString();
			if ("accept".equals(statusStr)) {
				userSubscribeMsg.setStatus(1);
			} else if ("reject".equals(statusStr)) {
				userSubscribeMsg.setStatus(2);
			}else {
				logger.info("unknow subscribe status, statusString :" + statusStr);
				userSubscribeMsg.setStatus(99);	//TODO 99未知
			}
			userSubscribeMsgRepository.save(userSubscribeMsg);
			
		}
		
		
	}

	

}

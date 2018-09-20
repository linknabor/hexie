package com.yumu.hexie.service.home.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yumu.hexie.integration.wechat.service.TemplateMsgService;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.localservice.oldversion.thirdpartyorder.HaoJiaAnComment;
import com.yumu.hexie.model.localservice.oldversion.thirdpartyorder.HaoJiaAnCommentRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.home.HaoJiaAnCommentService;

public class HaoJiaAnCommentServiceImpl implements HaoJiaAnCommentService{
	private static final Logger log = LoggerFactory.getLogger(HaoJiaAnCommentServiceImpl.class);
	@Inject
	private HaoJiaAnCommentRepository haoJiaAnCommentRepository;
	@Inject
	private SystemConfigService systemConfigService;
	
	//保存评论或投诉
	@Override
	@Transactional
	public int saveComment(User user,HaoJiaAnComment comment) {
		log.error("saveComment的用户="+user);
		log.error("saveComment的用户id="+user.getId());
		log.error("saveComment的用户电话="+user.getTel());
		int count = 0;
		if(comment.getCommentType()==ModelConstant.HAOJIAAN_COMMPENT_STATUS_COMPLAIN) {
			comment.setComplainStatus(0);//0待确认 1已确认 2拒绝
		}
		comment.setCreateDate(System.currentTimeMillis());
		comment.setCommentUserName(user.getName());//真实姓名
		comment.setCommentUserId(user.getId());//评论人
		comment.setCommentUserTel(user.getTel());//用户电话
		HaoJiaAnComment haoJiaAnComment = haoJiaAnCommentRepository.save(comment);
		String accessToken = systemConfigService.queryWXAToken();//微信token
		//1评论 2投诉，如果是投诉发送短信模板给商家，确认是否承认投诉
		if(comment.getCommentType() == ModelConstant.HAOJIAAN_COMMPENT_STATUS_COMPLAIN) {
			TemplateMsgService.sendHaoJiaAnCommentMsg(haoJiaAnComment, user, accessToken);//发送模板消息
		}
		//不为空表示保存成功
		if(haoJiaAnComment!=null) {
			return count = 1;
		}
		return count;
	}

	//处理投诉
	@Override
	@Transactional
	public int solveComplain(User user, String feedBack, int complainStatus,long commentId) {
		int count = 0;
		HaoJiaAnComment haoJiaAnComment = haoJiaAnCommentRepository.findOne(commentId);
		haoJiaAnComment.setComplainStatus(complainStatus);//投诉状态
		haoJiaAnComment.setFeedBack(feedBack);//投诉反馈
		haoJiaAnComment.setComplainTime(System.currentTimeMillis());//处理投诉的时间
		haoJiaAnComment.setProcessUserId(user.getId());//处理投诉的人的id
		haoJiaAnComment.setProcessUserName(user.getName());//处理投诉的人的姓名
		if(haoJiaAnCommentRepository.save(haoJiaAnComment) != null) {
			count = 1;
		}
		return count;
	}

}

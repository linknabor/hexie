package com.yumu.hexie.service.home.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yumu.hexie.integration.wechat.service.TemplateMsgService;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.localservice.oldversion.YuyueOrder;
import com.yumu.hexie.model.localservice.oldversion.YuyueOrderRepository;
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
	
	//获得投诉详情页信息
	@Override
	public Map<String,Object> getComplainDetail(long commentId) {
		List<Object[]> list = haoJiaAnCommentRepository.getComplainDetail(commentId);
		Map<String,Object> map = new HashMap<String,Object>();
		for (Object[] object : list) {
			map.put("address", object[0]);//服务地址
			map.put("tel", object[1]);//手机号
			map.put("status", object[2]);//预约状态
			map.put("productName", object[3]);//服务名称
			map.put("orderNo", object[4]);//订单编号
			map.put("complainStatus", object[5]);//投诉状态
			map.put("commentContent", object[6]);//投诉内容
		}
		return map;
	}

	//处理投诉
	@Override
	@Transactional
	public int solveComplain(User user, String feedBack, int complainStatus,long commentId) {
		log.error("feedBack = "+feedBack);
		log.error("complainStatus = "+complainStatus);
		log.error("commentId = "+commentId);
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

	//根据订单id和评论类型查看当前订单是否有被评论和投诉
	@Override
	public HaoJiaAnComment getCommentByOrderNoAndType(String yuyueOrderNo, int commentType) {
		HaoJiaAnComment hjac = haoJiaAnCommentRepository.getCommentByOrderNoAndType(yuyueOrderNo, commentType);
		return hjac;
	}


}

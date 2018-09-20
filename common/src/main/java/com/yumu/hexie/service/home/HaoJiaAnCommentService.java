package com.yumu.hexie.service.home;

import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.model.localservice.oldversion.thirdpartyorder.HaoJiaAnComment;
import com.yumu.hexie.model.user.User;

public interface HaoJiaAnCommentService {
	
	/**
	 * 保存评论或投诉
	 * @param user
	 * @param comment
	 * @return
	 */
	public int saveComment(User user,HaoJiaAnComment comment);
	
	/**
	 * 处理投诉
	 * @param user
	 * @param feedBack 投诉反馈
	 * @param complainStatus 投诉状态
	 * @param commentId 处理的投诉id
	 * @return
	 */
	public int solveComplain(User user,String feedBack,int complainStatus,long commentId);
}

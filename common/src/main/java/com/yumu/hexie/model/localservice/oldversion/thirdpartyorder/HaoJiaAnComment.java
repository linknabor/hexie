package com.yumu.hexie.model.localservice.oldversion.thirdpartyorder;

import javax.persistence.Entity;

import com.yumu.hexie.integration.wechat.entity.templatemsg.TemplateItem;
import com.yumu.hexie.model.BaseModel;

/**
 * 评论和投诉表
 * @author Jeffrey
 *
 */
@Entity
public class HaoJiaAnComment extends BaseModel{

	private Long commentUserId;//评论人or投诉人
	
	private String commentUserName;//评论人
	
	private String commentContent;//评论内容
	
	private Integer commentType;//评论类型 评论和投诉
	
	private Integer commentLevel;//评论等级（5星最高，仅评论类型有效，投诉无效）
	
	private Integer complainStatus;//确认投诉（商家确认投诉是否属实）
	
	private Long complainTime;//确认投诉时间
	
	private Long processUserId;//处理投诉人Id
	
	private String processUserName;//处理人姓名
	
	private Long yuyueOrderNo;//预约订单编号（对此订单作评价）
	
	private String serviceName;//服务名称
	
	private String feedBack;//投诉反馈内容

	public Long getCommentUserId() {
		return commentUserId;
	}

	public void setCommentUserId(Long commentUserId) {
		this.commentUserId = commentUserId;
	}

	public String getCommentUserName() {
		return commentUserName;
	}

	public void setCommentUserName(String commentUserName) {
		this.commentUserName = commentUserName;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public Integer getCommentType() {
		return commentType;
	}

	public void setCommentType(Integer commentType) {
		this.commentType = commentType;
	}

	public Integer getCommentLevel() {
		return commentLevel;
	}

	public void setCommentLevel(Integer commentLevel) {
		this.commentLevel = commentLevel;
	}

	public Integer getComplainStatus() {
		return complainStatus;
	}

	public void setComplainStatus(Integer complainStatus) {
		this.complainStatus = complainStatus;
	}

	public Long getComplainTime() {
		return complainTime;
	}

	public void setComplainTime(Long complainTime) {
		this.complainTime = complainTime;
	}

	public Long getProcessUserId() {
		return processUserId;
	}

	public void setProcessUserId(Long processUserId) {
		this.processUserId = processUserId;
	}

	public String getProcessUserName() {
		return processUserName;
	}

	public void setProcessUserName(String processUserName) {
		this.processUserName = processUserName;
	}

	public Long getYuyueOrderNo() {
		return yuyueOrderNo;
	}

	public void setYuyueOrderNo(Long yuyueOrderNo) {
		this.yuyueOrderNo = yuyueOrderNo;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getFeedBack() {
		return feedBack;
	}

	public void setFeedBack(String feedBack) {
		this.feedBack = feedBack;
	}

	
	
}

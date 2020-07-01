package com.yumu.hexie.integration.customservice.dto;

import com.yumu.hexie.model.user.User;

public class ServiceCommentDTO {
	
	private User user;
	
	private String orderId;
    private int commentQuality;
    private int commentAttitude;
    private int commentService;
    private String comment;
    private String commentImgUrls;
    
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public int getCommentQuality() {
		return commentQuality;
	}
	public void setCommentQuality(int commentQuality) {
		this.commentQuality = commentQuality;
	}
	public int getCommentAttitude() {
		return commentAttitude;
	}
	public void setCommentAttitude(int commentAttitude) {
		this.commentAttitude = commentAttitude;
	}
	public int getCommentService() {
		return commentService;
	}
	public void setCommentService(int commentService) {
		this.commentService = commentService;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getCommentImgUrls() {
		return commentImgUrls;
	}
	public void setCommentImgUrls(String commentImgUrls) {
		this.commentImgUrls = commentImgUrls;
	}
	@Override
	public String toString() {
		return "ServiceCommentDTO [user=" + user + ", orderId=" + orderId + ", commentQuality=" + commentQuality
				+ ", commentAttitude=" + commentAttitude + ", commentService=" + commentService + ", comment=" + comment
				+ ", commentImgUrls=" + commentImgUrls + "]";
	}
    
    
	

}

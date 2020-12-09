package com.yumu.hexie.integration.posting.vo;

import java.io.Serializable;

public class SaveCommentVO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5182383928014275331L;
	private String id;
	private String content;
	private String userId;
	private String userName;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	@Override
	public String toString() {
		return "SaveCommentRequest [id=" + id + ", content=" + content + ", userId=" + userId + ", userName=" + userName
				+ "]";
	}

	
}

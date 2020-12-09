package com.yumu.hexie.integration.posting.mapper;

import java.io.Serializable;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryCommentMapper implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3713569750846234470L;
	
	@JsonProperty("comment_datetime")
	private BigInteger commentDateTime;
	@JsonProperty("comment_username")
	private String commentUserName;
	@JsonProperty("commnet_content")
	private String commentContent;
	
	public QueryCommentMapper(BigInteger commentDateTime, String commentUserName, String commentContent) {
		super();
		this.commentDateTime = commentDateTime;
		this.commentUserName = commentUserName;
		this.commentContent = commentContent;
	}

	public BigInteger getCommentDateTime() {
		return commentDateTime;
	}

	public void setCommentDateTime(BigInteger commentDateTime) {
		this.commentDateTime = commentDateTime;
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

	
	
}

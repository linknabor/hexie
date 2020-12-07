package com.yumu.hexie.integration.posting.mapper;

import java.io.Serializable;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryPostingMapper implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 359659450472057901L;
	
	//商品信息
	@JsonProperty("id")
	private BigInteger threadId;	//帖子id
	@JsonProperty("create_date")
	private BigInteger createDateTime;	//发帖日期
	@JsonProperty("sect_id")
	private BigInteger userSectId;	//发帖用户小区ID
	@JsonProperty("sect_name")	
	private String userSectName;	//发帖用户小区名称
	@JsonProperty("csp_id")
	private String userCspId;	//发帖用户所在物业ID
	@JsonProperty("user_name")
	private String userName;	//发帖用户名称
	@JsonProperty("attachment_url")
	private String attachmentUrl;	//附件图片链接
	@JsonProperty("content")
	private String threadContent;	//帖子内容
	
	public QueryPostingMapper(BigInteger threadId, BigInteger createDateTime, BigInteger userSectId, String userSectName,
			String userCspId, String userName, String attachmentUrl, String threadContent) {
		super();
		this.threadId = threadId;
		this.createDateTime = createDateTime;
		this.userSectId = userSectId;
		this.userSectName = userSectName;
		this.userCspId = userCspId;
		this.userName = userName;
		this.attachmentUrl = attachmentUrl;
		this.threadContent = threadContent;
	}

	public BigInteger getThreadId() {
		return threadId;
	}

	public void setThreadId(BigInteger threadId) {
		this.threadId = threadId;
	}

	public BigInteger getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(BigInteger createDateTime) {
		this.createDateTime = createDateTime;
	}

	public BigInteger getUserSectId() {
		return userSectId;
	}

	public void setUserSectId(BigInteger userSectId) {
		this.userSectId = userSectId;
	}

	public String getUserSectName() {
		return userSectName;
	}

	public void setUserSectName(String userSectName) {
		this.userSectName = userSectName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	public String getThreadContent() {
		return threadContent;
	}

	public void setThreadContent(String threadContent) {
		this.threadContent = threadContent;
	}

	public String getUserCspId() {
		return userCspId;
	}

	public void setUserCspId(String userCspId) {
		this.userCspId = userCspId;
	}

	@Override
	public String toString() {
		return "QueryPostingMapper [threadId=" + threadId + ", createDateTime=" + createDateTime + ", userSectId="
				+ userSectId + ", userSectName=" + userSectName + ", userCspId=" + userCspId + ", userName=" + userName
				+ ", attachmentUrl=" + attachmentUrl + ", threadContent=" + threadContent + "]";
	}


}

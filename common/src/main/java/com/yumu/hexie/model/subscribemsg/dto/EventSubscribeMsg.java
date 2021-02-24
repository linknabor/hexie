package com.yumu.hexie.model.subscribemsg.dto;

import java.io.Serializable;
import java.util.List;

public class EventSubscribeMsg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1673723968781749056L;

	private String toUserName;
	private String fromUserName;
	private String createTime;
	private String msgType;
	private String event;
	private List<TemplateDetail> list;
	private String appId;
	
	public static class TemplateDetail{
		
		private String templateId;
		private String subscribeStatusString;
		private String popupScene;
		
		public TemplateDetail() {
			super();
		}
		public TemplateDetail(String templateId, String subscribeStatusString, String popupScene) {
			super();
			this.templateId = templateId;
			this.subscribeStatusString = subscribeStatusString;
			this.popupScene = popupScene;
		}
		public String getTemplateId() {
			return templateId;
		}
		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}
		public String getSubscribeStatusString() {
			return subscribeStatusString;
		}
		public void setSubscribeStatusString(String subscribeStatusString) {
			this.subscribeStatusString = subscribeStatusString;
		}
		public String getPopupScene() {
			return popupScene;
		}
		public void setPopupScene(String popupScene) {
			this.popupScene = popupScene;
		}
		@Override
		public String toString() {
			return "TemplateDetail [templateId=" + templateId + ", subscribeStatusString=" + subscribeStatusString
					+ ", popupScene=" + popupScene + "]";
		}
		
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public List<TemplateDetail> getList() {
		return list;
	}

	public void setList(List<TemplateDetail> list) {
		this.list = list;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	@Override
	public String toString() {
		return "EventSubscribeMsg [toUserName=" + toUserName + ", fromUserName=" + fromUserName + ", createTime="
				+ createTime + ", msgType=" + msgType + ", event=" + event + ", list=" + list + ", appId=" + appId
				+ "]";
	}

}

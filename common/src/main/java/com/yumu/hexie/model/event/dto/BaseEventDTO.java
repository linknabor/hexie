package com.yumu.hexie.model.event.dto;

import java.io.Serializable;

/**
 * 微信公众号事件的基类
 * TODO 以后把诸如开卡、订阅消息的事件类都继承这个类
 * @author david
 *
 */
public class BaseEventDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3469783432887323651L;

	private String openid;
	private String appId;
	private String event;	//事件类型
	private String eventKey;	//事件key
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getEventKey() {
		return eventKey;
	}
	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	@Override
	public String toString() {
		return "BaseEventDTO [openid=" + openid + ", appId=" + appId + ", event=" + event + ", eventKey=" + eventKey
				+ "]";
	}
		
	
	
	
}

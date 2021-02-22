package com.yumu.hexie.vo;

import java.io.Serializable;

import com.yumu.hexie.model.user.User;

public class WuyeMsgQueue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9187957497448360748L;
	
	private String sectId;
	private int sendMethod;	//消息推送方式，0公众号，1短信，2全部
	private int range = 0;	//发送范围,0指定方位，1全部
	private String content;	//发送内容
	private User operator;
	
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public int getSendMethod() {
		return sendMethod;
	}
	public void setSendMethod(int sendMethod) {
		this.sendMethod = sendMethod;
	}
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public User getOperator() {
		return operator;
	}
	public void setOperator(User operator) {
		this.operator = operator;
	}
	
	

}

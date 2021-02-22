package com.yumu.hexie.integration.wechat.entity.subscribemsg;

import java.io.Serializable;

public class SubscribeMsg<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8865890850211465705L;
	
	private String touser;//用户openid
	private String template_id;//模板ID
	private String page;//跳转网页时填写
	private Miniprogram miniprogram;//跳转小程序时填写
	private T data;
	
	public static class Miniprogram {
		
		private String appid;	//小程序appid
		private String pagepath;	//小程序跳转路径
		public String getAppid() {
			return appid;
		}
		public void setAppid(String appid) {
			this.appid = appid;
		}
		public String getPagepath() {
			return pagepath;
		}
		public void setPagepath(String pagepath) {
			this.pagepath = pagepath;
		}
		@Override
		public String toString() {
			return "Miniprogram [appid=" + appid + ", pagepath=" + pagepath + "]";
		}
		
	}
	
	public String getTouser() {
		return touser;
	}
	public void setTouser(String touser) {
		this.touser = touser;
	}
	public String getTemplate_id() {
		return template_id;
	}
	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public Miniprogram getMiniprogram() {
		return miniprogram;
	}
	public void setMiniprogram(Miniprogram miniprogram) {
		this.miniprogram = miniprogram;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "SubscribeMsg [touser=" + touser + ", template_id=" + template_id + ", page=" + page + ", miniprogram="
				+ miniprogram + ", data=" + data + "]";
	}
	

}

package com.yumu.hexie.service.page.pojo.dto;

import java.io.Serializable;

public class PageViewDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4027994716477741307L;
	
	private String appid;
	private String page;
	private int count;
	
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "PageViewDTO [appid=" + appid + ", page=" + page + ", count=" + count + "]";
	}
	

}

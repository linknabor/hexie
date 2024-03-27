package com.yumu.hexie.model.statistic;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.yumu.hexie.model.BaseModel;

/**
 * 页面访问统计
 * @author david
 *
 */
@Entity
public class PageView extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -928121921143166918L;

	private String appid;
	private String page;
	private int count;
	@Column(name = "countDate", length = 8)
	private String countDate;
	
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
	public String getCountDate() {
		return countDate;
	}
	public void setCountDate(String countDate) {
		this.countDate = countDate;
	}
	
}

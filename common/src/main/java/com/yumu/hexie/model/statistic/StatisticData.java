package com.yumu.hexie.model.statistic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.yumu.hexie.model.BaseModel;

@Entity
@Table(indexes= {@Index(columnList="recordDate")})
public class StatisticData extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7290125702326461017L;
	private String appid;
	private int clickCount;	//点击数
	private int registerCount;	//注册用户数，有手机的
	private int bindCount;	//绑定房屋的用户数
	@Column(name = "recordDate", length = 8)
	private String recordDate;	//记录日期
	
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public int getClickCount() {
		return clickCount;
	}
	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}
	public int getRegisterCount() {
		return registerCount;
	}
	public void setRegisterCount(int registerCount) {
		this.registerCount = registerCount;
	}
	public int getBindCount() {
		return bindCount;
	}
	public void setBindCount(int bindCount) {
		this.bindCount = bindCount;
	}
	public String getRecordDate() {
		return recordDate;
	}
	public void setRecordDate(String recordDate) {
		this.recordDate = recordDate;
	}
	@Override
	public String toString() {
		return "StatisticData [appid=" + appid + ", clickCount=" + clickCount + ", registerCount=" + registerCount
				+ ", bindCount=" + bindCount + ", recordDate=" + recordDate + "]";
	}
	
}

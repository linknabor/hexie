package com.yumu.hexie.vo;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.model.market.OrderItem;

public class RgroupOrderRecordVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6713588109265389692L;

	private String recordDate;
	private long userId;
	private long ruleId;
	private long orderId;
	private boolean first;
	private String headUrl;
	private String tel;
	private String userName;
	private int count = 1;//份数
	List<OrderItem> items;
	
	public String getRecordDate() {
		return recordDate;
	}
	public void setRecordDate(String recordDate) {
		this.recordDate = recordDate;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public boolean isFirst() {
		return first;
	}
	public void setFirst(boolean first) {
		this.first = first;
	}
	public String getHeadUrl() {
		return headUrl;
	}
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<OrderItem> getItems() {
		return items;
	}
	public void setItems(List<OrderItem> items) {
		this.items = items;
	}
	@Override
	public String toString() {
		return "OrderRecordVO [recordDate=" + recordDate + ", userId=" + userId + ", ruleId=" + ruleId
				+ ", orderId=" + orderId + ", first=" + first + ", headUrl=" + headUrl + ", tel=" + tel
				+ ", userName=" + userName + ", count=" + count + ", items=" + items + "]";
	}
}

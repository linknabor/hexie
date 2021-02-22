package com.yumu.hexie.integration.wechat.entity.subscribemsg;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 账单模板
 * @author huym
 *
 */
public class BillNotificationVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6718291292914374882L;
	
	@JsonProperty("name1")
	private SubscribeItem name;	//用户名称
	@JsonProperty("phone_number2")
	private SubscribeItem phone;	//电话号码
	@JsonProperty("date3")
	private SubscribeItem date;	//账单日期
	@JsonProperty("thing4")
	private SubscribeItem thing;	//账单信息
	@JsonProperty("amount5")
	private SubscribeItem amount;	//账单总费用
	
	public SubscribeItem getName() {
		return name;
	}
	public void setName(SubscribeItem name) {
		this.name = name;
	}
	public SubscribeItem getPhone() {
		return phone;
	}
	public void setPhone(SubscribeItem phone) {
		this.phone = phone;
	}
	public SubscribeItem getDate() {
		return date;
	}
	public void setDate(SubscribeItem date) {
		this.date = date;
	}
	public SubscribeItem getThing() {
		return thing;
	}
	public void setThing(SubscribeItem thing) {
		this.thing = thing;
	}
	public SubscribeItem getAmount() {
		return amount;
	}
	public void setAmount(SubscribeItem amount) {
		this.amount = amount;
	}
	@Override
	public String toString() {
		return "BillNotificationVO [name=" + name + ", phone=" + phone + ", date=" + date + ", thing=" + thing
				+ ", amount=" + amount + "]";
	}
	
	
}

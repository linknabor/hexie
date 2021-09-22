package com.yumu.hexie.vo;

import java.io.Serializable;

import com.yumu.hexie.model.user.User;

public class BindHouseQueue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8171021633196646617L;
	
	private String bindType;
	private String tradeWaterId;
	private User user;
	
	public String getTradeWaterId() {
		return tradeWaterId;
	}
	public void setTradeWaterId(String tradeWaterId) {
		this.tradeWaterId = tradeWaterId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getBindType() {
		return bindType;
	}
	public void setBindType(String bindType) {
		this.bindType = bindType;
	}
	@Override
	public String toString() {
		return "BindHouseQueue [bindType=" + bindType + ", tradeWaterId=" + tradeWaterId + ", user=" + user + "]";
	}
	
	

}

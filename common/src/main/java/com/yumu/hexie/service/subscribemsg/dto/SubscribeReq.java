package com.yumu.hexie.service.subscribemsg.dto;

import java.io.Serializable;

import com.yumu.hexie.model.user.User;

public class SubscribeReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6148603515483146219L;

	private User user;
	private int subscribe;	//accept 1, reject 2, off 0
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getSubscribe() {
		return subscribe;
	}
	public void setSubscribe(int subscribe) {
		this.subscribe = subscribe;
	}
	@Override
	public String toString() {
		return "SubscribeReq [user=" + user + ", subscribe=" + subscribe + "]";
	}
	
}

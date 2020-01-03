package com.yumu.hexie.vo;

import java.io.Serializable;

import com.yumu.hexie.model.user.User;

public class AddPointQueue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1659727998303347105L;
	private User user;
	private String point;
	private String key;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getPoint() {
		return point;
	}
	public void setPoint(String point) {
		this.point = point;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	@Override
	public String toString() {
		return "AddPointQueue [user=" + user + ", point=" + point + ", key=" + key + "]";
	}
	
	
}

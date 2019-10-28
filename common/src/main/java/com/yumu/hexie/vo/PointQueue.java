package com.yumu.hexie.vo;

import java.io.Serializable;

import com.yumu.hexie.model.user.User;

/**
 * 积分服务队列
 * @author david
 *
 */
public class PointQueue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2539977621529870162L;
	
	private User user;
	private int type;	//1.芝麻，2绿豆
	private Integer point;	//积分
	private String key;	//业务key
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Integer getPoint() {
		return point;
	}
	public void setPoint(Integer point) {
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
		return "PointQueue [user=" + user + ", type=" + type + ", point=" + point + ", key=" + key + "]";
	}
	
	
	
}

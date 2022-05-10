package com.yumu.hexie.model.user;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.yumu.hexie.model.BaseModel;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames="userId"))
public class RgroupOwner extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7728845122983998226L;
	
	private long userId;
	private String miniopenid;
	private int followers;	//TODO 关注人数  这个不知道有什么用？
	private int members;	//成员数，即访问数
	private int attendees;		//跟团人次
	private String feeRate;
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getMiniopenid() {
		return miniopenid;
	}
	public void setMiniopenid(String miniopenid) {
		this.miniopenid = miniopenid;
	}
	public int getFollowers() {
		return followers;
	}
	public void setFollowers(int followers) {
		this.followers = followers;
	}
	public int getMembers() {
		return members;
	}
	public void setMembers(int members) {
		this.members = members;
	}
	public int getAttendees() {
		return attendees;
	}
	public void setAttendees(int attendees) {
		this.attendees = attendees;
	}
	public String getFeeRate() {
		return feeRate;
	}
	public void setFeeRate(String feeRate) {
		this.feeRate = feeRate;
	}
	@Override
	public String toString() {
		return "RgroupOwner [userId=" + userId + ", miniopenid=" + miniopenid + ", followers=" + followers
				+ ", members=" + members + ", attendees=" + attendees + ", feeRate=" + feeRate + "]";
	}
	

}

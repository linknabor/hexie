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
	private String openid;
	private String appid;
	private String miniopenid;
	private String miniappid;
	private int followers;	//TODO 关注人数  这个不知道有什么用？
	private int members;	//成员数，即访问数
	private int attendees;		//跟团人次
	private String feeRate;
	private String name;
	private String headImgUrl;
	private String tel;
	
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
	public String getMiniappid() {
		return miniappid;
	}
	public void setMiniappid(String miniappid) {
		this.miniappid = miniappid;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getHeadImgUrl() {
		return headImgUrl;
	}
	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	

}
